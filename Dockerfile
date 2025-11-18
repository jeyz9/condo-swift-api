FROM openjdk:17-jdk-slim
FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres
ENV SPRING_DATASOURCE_USERNAME=postgres.syrkqqdlwkpzpwuedspt
ENV SPRING_DATASOURCE_PASSWORD=P9ewo6o9MzgaGFza

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/target/condo-swift-api-0.0.1-SNAPSHOT.jar"]