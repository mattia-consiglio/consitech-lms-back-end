# Fase di compilazione
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Copia il file pom.xml e i sorgenti
COPY pom.xml /usr/src/app/
COPY src /usr/src/app/src

# Esegue la compilazione senza eseguire i test
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

# Fase di esecuzione
FROM openjdk:21-jdk-slim

# Espone la porta 8080 per l'applicazione
EXPOSE 8080

# Copia l'applicazione compilata dalla fase di compilazione
COPY --from=build /usr/src/app/target/*.jar app.jar

# Installa ffmpeg
RUN apk update
RUN apk upgrade
RUN apk add --no-cache ffmpeg

# Comando per eseguire l'applicazione
ENTRYPOINT ["java","-jar","/app.jar"]
