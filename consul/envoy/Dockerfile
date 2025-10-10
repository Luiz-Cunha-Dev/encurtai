FROM envoyproxy/envoy:v1.27-latest

USER root

RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    ca-certificates

ENV CONSUL_VERSION=1.19.0
RUN curl -Lo /tmp/consul.zip "https://releases.hashicorp.com/consul/${CONSUL_VERSION}/consul_${CONSUL_VERSION}_linux_amd64.zip" && \
    unzip /tmp/consul.zip -d /usr/local/bin && \
    chmod +x /usr/local/bin/consul && \
    rm /tmp/consul.zip

RUN consul version
RUN envoy --version