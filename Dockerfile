# ---- build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradlew settings.gradle build.gradle lombok.config ./
COPY gradle gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src src
RUN ./gradlew bootJar -x test --no-daemon \
 && cp "$(ls build/libs/*.jar | grep -v plain)" app.jar

# ---- run ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/app.jar app.jar
USER app

EXPOSE 8080
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -Dfile.encoding=UTF-8"
HEALTHCHECK --interval=10s --timeout=5s --start-period=90s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
