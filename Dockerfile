FROM eclipse-temurin:21
WORKDIR /app
COPY target/TTN-EcommerceProject-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]