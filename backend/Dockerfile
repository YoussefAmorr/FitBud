# Stage 1: Build the Spring Boot application
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd --system fitbud && \
    useradd --system --gid fitbud --home-dir /app fitbud

COPY --from=build --chown=fitbud:fitbud /app/target/*.jar app.jar

USER fitbud

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]