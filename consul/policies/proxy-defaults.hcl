kind = "proxy-defaults"
name = "global"

config {
  connect_timeout_ms = 5000

  envoy_extra_static_clusters_json = <<EOF
  [
    {
      "name": "local_app",
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
