# STAGE 1: BUILD
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# STAGE 2: RUN
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# EXPOSE PORT
EXPOSE 8080

# RUN APPLICATION
ENTRYPOINT ["java", "-jar", "app.jar"]