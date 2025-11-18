
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

copy Server .
copy Server/pom.xml ./pom.xml
COPY ../openapi.yaml ../openapi.yaml
RUN mvn clean verify package


FROM eclipse-temurin:21-jre-jammy AS final

# Nastavíme pracovní adresář
WORKDIR /app

COPY --from=build /app/rest-api-module/target/*jar app.jar

# Příkaz pro spuštění aplikace
ENTRYPOINT ["java", "-jar", "app.jar"]