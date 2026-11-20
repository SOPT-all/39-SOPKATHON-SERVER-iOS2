data "aws_caller_identity" "current" {}

locals {
  # 앱 환경변수(SecureString)가 저장되는 Parameter Store 경로
  app_parameter_path = "/sopkathon/prod"
  app_parameter_arn  = "arn:aws:ssm:ap-southeast-2:${data.aws_caller_identity.current.account_id}:parameter${local.app_parameter_path}"
}

# ========================================
# 1. VPC 및 Subnet 조회
# ========================================
data "aws_vpc" "default" {
  default = true
}

# ========================================
# 2. EC2 운영체제 이미지 조회
# ========================================
data "aws_ssm_parameter" "ubuntu" {
  name = "/aws/service/canonical/ubuntu/server/26.04/stable/current/amd64/hvm/ebs-gp3/ami-id"
}

# ========================================
# 3. Security Group
# ========================================
resource "aws_security_group" "server" {
  name        = "sopkathon-sg"
  description = "Security group for SOPT server"
  vpc_id      = data.aws_vpc.default.id
}

resource "aws_vpc_security_group_ingress_rule" "http" {
  security_group_id = aws_security_group.server.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port         = 80
  to_port           = 80
}

resource "aws_vpc_security_group_ingress_rule" "https" {
  security_group_id = aws_security_group.server.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
  from_port         = 443
  to_port           = 443
}

resource "aws_vpc_security_group_egress_rule" "outbound" {
  security_group_id = aws_security_group.server.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

# ========================================
# 4. EC2 IAM Role
# ========================================
resource "aws_iam_role" "ec2" {
  name = "sopkathon-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm" {
  role       = aws_iam_role.ec2.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_role_policy_attachment" "ecr" {
  role       = aws_iam_role.ec2.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

# 배포 시 Parameter Store의 앱 환경변수를 읽어 .env를 만든다
resource "aws_iam_role_policy" "app_parameters" {
  name = "sopkathon-ec2-read-app-parameters"
  role = aws_iam_role.ec2.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid    = "ReadAppParameters"
      Effect = "Allow"
      Action = "ssm:GetParametersByPath"
      Resource = [
        local.app_parameter_arn,
        "${local.app_parameter_arn}/*",
      ]
    }]
  })
}

resource "aws_iam_instance_profile" "ec2" {
  name = "sopkathon-ec2-profile"
  role = aws_iam_role.ec2.name
}

# ========================================
# 5. EC2
# ========================================
resource "aws_instance" "server" {
  ami                    = data.aws_ssm_parameter.ubuntu.value
  instance_type          = var.instance_type
  vpc_security_group_ids = [aws_security_group.server.id]
  iam_instance_profile   = aws_iam_instance_profile.ec2.name

  # 첫 부팅 때 Docker, Compose, AWS CLI 설치
  user_data = replace(file("${path.module}/user-data.sh"), "\r\n", "\n")

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
    encrypted   = true
  }

  tags = {
    Name = "sopkathon-server"
  }

  # 새 Ubuntu AMI가 나와도 인스턴스를 교체하지 않는다
  lifecycle {
    ignore_changes = [ami, user_data]
  }
}

# ========================================
# 6. Elastic IP
# ========================================
resource "aws_eip" "server" {
  domain   = "vpc"
  instance = aws_instance.server.id
}

# ========================================
# 7. ECR
# ========================================
resource "aws_ecr_repository" "app" {
  name                 = "sopkathon"
  image_tag_mutability = "IMMUTABLE"
}

# ========================================
# 8. S3
# ========================================
resource "aws_s3_bucket" "files" {
  bucket_prefix = "sopkathon-files-"
}
