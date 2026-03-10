FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY /target/springAi-0.0.1-SNAPSHOT.jar springAi.jar

EXPOSE 8081

ENTRYPOINT [ "java","-jar","springAi.jar" ]