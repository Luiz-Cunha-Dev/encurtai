#!/bin/bash

echo "Configuring Docker network with fixed IPs..."

echo "Removing existing network (if any)..."
docker network rm encurta-ai-network 2>/dev/null || true

echo "Creating encurta-ai-network with subnet 10.200.0.0/16..."
docker network create \
    --driver bridge \
    --subnet=10.200.0.0/16 \
    --ip-range=10.200.240.0/20 \
    --gateway=10.200.0.1 \
    encurta-ai-network

if docker network inspect encurta-ai-network >/dev/null 2>&1; then
        echo "Network created successfully!"
        echo ""
        echo "Network information:"
        docker network inspect encurta-ai-network --format="{{.Name}}: {{.IPAM.Config}}"
        echo ""
        echo "Available IP ranges: 10.200.0.10 - 10.200.0.100"
else
        echo "Error creating the network!"
        exit 1
fi