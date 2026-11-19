output "ec2_instance_id" {
  value = aws_instance.server.id
}

output "ec2_public_ip" {
  value = aws_eip.server.public_ip
}

output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}

output "s3_bucket_name" {
  value = aws_s3_bucket.files.id
}

output "github_actions_user_name" {
  value = aws_iam_user.github_actions.name
}
