# Dockerfile padrão para microserviço Spring Boot
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copia o jar gerado pelo build
COPY build/libs/*.jar app.jar

# Expõe a porta do serviço (substituir conforme cada serviço)
EXPOSE 8081

# Comando para rodar o jar
ENTRYPOINT ["java","-jar","app.jar"]
