FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY pom.xml .
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven curl && \
    mvn -B dependency:go-offline && \
    rm -rf /var/lib/apt/lists/*

COPY src ./src

RUN mvn -B -DskipTests clean package

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]