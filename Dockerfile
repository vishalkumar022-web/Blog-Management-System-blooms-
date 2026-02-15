# ---------- BUILD STAGE ----------
# Hum Java 21 wala Maven use kar rahe hain
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Pehle files copy karte hain
COPY pom.xml .
COPY src ./src

# Phir build karte hain (Tests skip karke taaki jaldi ho)
RUN mvn clean package -DskipTests

# ---------- RUN STAGE ----------
# Yahan Maine Change Kiya Hai: Java 17 -> Java 21 ✅
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Build stage se bani hui JAR file copy kar rahe hain
COPY --from=build /app/target/*.jar app.jar

# Port expose aur run command
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]