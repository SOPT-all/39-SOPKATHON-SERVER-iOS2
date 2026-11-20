#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 3 ]]; then
  echo "Usage: $0 <aws-region> <ecr-registry> <image-uri>"
  exit 1
fi

aws_region="$1"
ecr_registry="$2"
image_uri="$3"
deploy_dir="/opt/sopkathon"
parameter_path="/sopkathon/prod"

# Parameter Store의 앱 환경변수로 .env를 만든다
env_tmp="$(mktemp "${deploy_dir}/.env.XXXXXX")"
trap 'rm -f "${env_tmp}"' EXIT

aws ssm get-parameters-by-path \
  --region "${aws_region}" \
  --path "${parameter_path}" \
  --with-decryption \
  --query 'Parameters[].[Name,Value]' \
  --output text \
  | while IFS=$'\t' read -r name value; do
      printf "%s='%s'\n" "${name##*/}" "${value}"
    done > "${env_tmp}"

test -s "${env_tmp}"
chmod 0600 "${env_tmp}"
mv "${env_tmp}" "${deploy_dir}/.env"

export IMAGE_URI="${image_uri}"

aws ecr get-login-password --region "${aws_region}" \
  | docker login --username AWS --password-stdin "${ecr_registry}"

compose=(
  docker compose
  --env-file "${deploy_dir}/.env"
  -f "${deploy_dir}/docker-compose.yml"
)

"${compose[@]}" config >/dev/null
"${compose[@]}" pull app

printf 'IMAGE_URI=%s\n' "${image_uri}" > "${deploy_dir}/.deploy.env"
chmod 0600 "${deploy_dir}/.deploy.env"

compose=(
  docker compose
  --env-file "${deploy_dir}/.env"
  --env-file "${deploy_dir}/.deploy.env"
  -f "${deploy_dir}/docker-compose.yml"
)

"${compose[@]}" up -d
"${compose[@]}" exec -T caddy caddy reload --config /etc/caddy/Caddyfile

app_healthy="false"
for attempt in {1..60}; do
  app_container_id="$("${compose[@]}" ps -q app)"

  if [[ -n "${app_container_id}" ]]; then
    health_status="$(
      docker inspect \
        --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' \
        "${app_container_id}"
    )"
    echo "App health check ${attempt}: ${health_status}"

    if [[ "${health_status}" == "healthy" ]]; then
      app_healthy="true"
      break
    fi
  fi

  sleep 5
done

if [[ "${app_healthy}" != "true" ]]; then
  "${compose[@]}" ps
  "${compose[@]}" logs --tail 200 app
  exit 1
fi

"${compose[@]}" ps