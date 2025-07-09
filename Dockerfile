FROM eclipse-temurin:17-jdk-jammy
RUN apt-get update && apt-get upgrade -y && apt-get install -y --no-install-recommends maven curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
EXPOSE 8080
CMD ["tail", "-f", "/dev/null"]