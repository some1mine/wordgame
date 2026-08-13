# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY gradle ./gradle
COPY gradlew build.gradle settings.gradle ./
RUN chmod +x gradlew

COPY src ./src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon && \
    cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)" app.jar

FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system wordgame && \
    useradd --system --gid wordgame --home-dir /app --no-create-home wordgame && \
    chown wordgame:wordgame /app

COPY --from=build --chown=wordgame:wordgame /workspace/app.jar ./app.jar

USER wordgame
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
