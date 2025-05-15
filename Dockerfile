FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY target/*.jar /app/banking.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "banking.jar"]