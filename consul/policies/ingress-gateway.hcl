kind = "ingress-gateway"
name = "ingress-gateway"

listeners = [
  {
    port     = 8080
    protocol = "http"
    services = [
      {
        name  = "short-link-manager-service"
        hosts = ["*"]
      }
    ]
  },
  {
    port     = 8082
    protocol = "http"
    services = [
      {
        name  = "redirect-service"
        hosts = ["*"]
      }
    ]
  },
  {
    port     = 8083
    protocol = "http"
    services = [
      {
        name  = "short-link-metrics-service"
        hosts = ["*"]
      }
    ]
  }
]