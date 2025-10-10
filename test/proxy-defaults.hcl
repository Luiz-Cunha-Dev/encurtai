kind = "proxy-defaults"
name = "global"

config {
  protocol = "http"
  connect_timeout = "5s"

  retries {
    attempts = 3
    per_try_timeout = "2s"
  }

  envoy_extra_static_clusters_json = <<EOF
  [
    {
      "name": "*",
      "circuit_breakers": {
        "thresholds": [
          {
            "max_connections": 10,
            "max_pending_requests": 20,
            "max_requests": 50,
            "max_retries": 3
          }
        ]
      }
    }
  ]
EOF
}
