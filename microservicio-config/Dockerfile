FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/microservicio-config-1.0.0-RELEASE.jar app.jar

EXPOSE 8005

ENTRYPOINT ["java", "-jar", "app.jar"]
	