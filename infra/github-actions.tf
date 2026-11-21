resource "aws_iam_user" "github_actions" {
  name = "sopkathon-github-actions"
}

resource "aws_iam_user_policy" "github_actions" {
  name = "sopkathon-github-actions-deploy"
  user = aws_iam_user.github_actions.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid      = "GetECRAuthorizationToken"
        Effect   = "Allow"
        Action   = "ecr:GetAuthorizationToken"
        Resource = "*"
      },
      {
        Sid    = "PushImageToSopkathonRepository"
        Effect = "Allow"
        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:DescribeImages",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:PutImage",
        ]
        Resource = aws_ecr_repository.app.arn
      },
      {
        Sid    = "DeployToSopkathonServer"
        Effect = "Allow"
        Action = "ssm:SendCommand"
        Resource = [
          aws_instance.server.arn,
          "arn:aws:ssm:ap-southeast-2::document/AWS-RunShellScript",
        ]
      },
      {
        Sid      = "ReadDeploymentCommandResult"
        Effect   = "Allow"
        Action   = "ssm:GetCommandInvocation"
        Resource = "*"
      },
      {
        Sid      = "WriteAppParameters"
        Effect   = "Allow"
        Action   = "ssm:PutParameter"
        Resource = "${local.app_parameter_arn}/*"
      },
    ]
  })
}
