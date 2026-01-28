# 1️⃣ Java ka base image
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ Container ke andar working directory
WORKDIR /app

# 3️⃣ Pehle jar file copy karo
COPY target/*.jar app.jar

# 4️⃣ Spring Boot ka port expose karo
EXPOSE 8080

# 5️⃣ Application run command
ENTRYPOINT ["java", "-jar", "app.jar"]
