# Multi-stage build for DayTrader Spring Boot
# Supports multi-arch: linux/amd64, linux/ppc64le

# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create non-root user
RUN groupadd -r daytrader && useradd -r -g daytrader daytrader

COPY --from=builder /app/target/daytrader-spring-*.jar app.jar

RUN chown -R daytrader:daytrader /app
USER daytrader

EXPOSE 9082

ENTRYPOINT ["java", "-jar", "app.jar"]
