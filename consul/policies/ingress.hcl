ingress_gateway {
  name = "http-echo-ingress"
  listeners = [
    {
      port     = 20000
      protocol = "http"
      services = [
        { name = "http-echo", port = 5678 }
      ]
    }
  ]
}
