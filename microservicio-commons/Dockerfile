FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
COPY settings.xml /root/.m2/settings.xml
COPY artifact-uploader.json /home/bruno/google-cloud-sdk/contractar/contract-ar-artifact-registry-maven.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/home/bruno/google-cloud-sdk/contractar/contract-ar-artifact-registry-maven.json
RUN mvn clean package -s /root/.m2/settings.xml

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/microservicio-commons-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
