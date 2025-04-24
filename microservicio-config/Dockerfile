FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -U -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/microservicio-config-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
	