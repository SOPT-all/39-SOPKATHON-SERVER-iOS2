#!/usr/bin/env bash

set -euo pipefail

export DEBIAN_FRONTEND=noninteractive

# Docker Engine + Compose plugin (Ubuntu 공식 패키지)
if ! command -v docker >/dev/null 2>&1 || ! docker compose version >/dev/null 2>&1; then
  apt-get update
  apt-get install -y docker.io docker-compose-v2
fi
systemctl enable --now docker

# AWS CLI v2 (ECR 로그인, Parameter Store 조회에 사용)
if ! command -v aws >/dev/null 2>&1; then
  apt-get install -y unzip curl
  tmp_dir="$(mktemp -d)"
  curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "${tmp_dir}/awscliv2.zip"
  unzip -q "${tmp_dir}/awscliv2.zip" -d "${tmp_dir}"
  "${tmp_dir}/aws/install"
  rm -rf "${tmp_dir}"
fi

install -d -m 0755 /opt/sopkathon

docker --version
docker compose version
aws --version
