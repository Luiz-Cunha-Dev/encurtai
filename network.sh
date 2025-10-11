#!/bin/bash

echo "Configuring Docker network with fixed IPs..."

echo "Removing existing network (if any)..."
docker network rm encurta-ai-network 2>/dev/null || true

echo "Creating encurta-ai-network with subnet 172.20.0.0/16..."
docker network create \
    --driver bridge \
    --subnet=172.20.0.0/16 \
    --ip-range=172.20.240.0/20 \
    --gateway=172.20.0.1 \
    encurta-ai-network

if docker network inspect encurta-ai-network >/dev/null 2>&1; then
        echo "Network created successfully!"
        echo ""
        echo "Network information:"
        docker network inspect encurta-ai-network --format="{{.Name}}: {{.IPAM.Config}}"
else
        echo "Error creating the network!"
        exit 1
fi