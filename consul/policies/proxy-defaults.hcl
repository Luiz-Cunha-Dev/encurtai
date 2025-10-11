kind = "proxy-defaults"
name = "global"

config {
  protocol = "http"
  connect_timeout_ms = 3000
  
  envoy_cluster_json = <<EOF
{
  "circuit_breakers": {
    "thresholds": [
      {
        "priority": "DEFAULT",
        "max_connections": 100,
        "max_pending_requests": 50,
        "max_requests": 200,
        "max_retries": 3
      }
    ]
  },
  "outlier_detection": {
    "consecutive_5xx": 5,
    "interval": "30s",
    "base_ejection_time": "10s",
    "max_ejection_percent": 20
  }
}
EOF

  envoy_listener_json = <<EOF
{
  "@type": "type.googleapis.com/envoy.config.listener.v3.Listener",
  "filter_chains": [
    {
      "filters": [
        {
          "name": "envoy.filters.network.http_connection_manager",
          "typed_config": {
            "@type": "type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager",
            "route_config": {
              "virtual_hosts": [
                {
                  "name": "default",
                  "domains": ["*"],
                  "routes": [
                    {
                      "match": {"prefix": "/"},
                      "route": {
                        "retry_policy": {
                          "retry_on": "5xx,gateway-error,connect-failure,refused-stream",
                          "num_retries": 3,
                          "per_try_timeout": "5s",
                          "retry_back_off": {
                            "base_interval": "1s",
                            "max_interval": "10s"
                          }
                        }
                      }
                    }
                  ]
                }
              ]
            }
          }
        }
      ]
    }
  ]
}
EOF
}
