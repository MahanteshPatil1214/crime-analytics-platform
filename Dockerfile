FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY shared-models/pom.xml shared-models/
COPY gateway-service/pom.xml gateway-service/
COPY incident-service/pom.xml incident-service/
COPY person-service/pom.xml person-service/
COPY financial-service/pom.xml financial-service/
COPY graph-service/pom.xml graph-service/
COPY search-service/pom.xml search-service/
COPY report-service/pom.xml report-service/
COPY conversational-ai-service/pom.xml conversational-ai-service/
COPY etl-service/pom.xml etl-service/

RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B || true

COPY . .
RUN mvn clean package -DskipTests -B

ARG SERVICE_NAME
RUN mv ${SERVICE_NAME}/target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -sf http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
