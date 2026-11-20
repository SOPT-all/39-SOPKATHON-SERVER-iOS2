# 팀원용 IAM User
variable "db_access_users" {
  description = "운영 DB 접속용 IAM User를 만들 팀원 이름"
  type        = set(string)
  default     = ["hyorim", "yeji"]
}

resource "aws_iam_user" "db_access" {
  for_each = var.db_access_users
  name     = "sopkathon-${each.key}"
}

resource "aws_iam_user_policy" "db_access" {
  for_each = var.db_access_users
  name     = "sopkathon-db-port-forwarding"
  user     = aws_iam_user.db_access[each.key].name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "StartPortForwardingToServer"
        Effect = "Allow"
        Action = "ssm:StartSession"
        Resource = [
          aws_instance.server.arn,
          "arn:aws:ssm:ap-southeast-2::document/AWS-StartPortForwardingSession",
        ]

        Condition = {
          BoolIfExists = { "ssm:SessionDocumentAccessCheck" = "true" }
        }
      },
      {
        Sid      = "ManageOwnSessions"
        Effect   = "Allow"
        Action   = ["ssm:TerminateSession", "ssm:ResumeSession"]
        Resource = "arn:aws:ssm:*:*:session/$${aws:username}-*"
      },
    ]
  })
}
