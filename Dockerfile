FROM openjdk:17-jdk-slim
FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d3k9o5l6ubrc73dps1q0-a.singapore-postgres.render.com:5432/condo_swift_db
ENV SPRING_DATASOURCE_USERNAME=condo_swift_db_user
ENV SPRING_DATASOURCE_PASSWORD=vfXRbkhZQ9fHtTf7DTdDe78v3WIxUTPe

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/target/condo-swift-api-0.0.1-SNAPSHOT.jar"]