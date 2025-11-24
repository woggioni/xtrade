FROM docker.io/eclipse-temurin:25-jdk-alpine AS build-base
RUN adduser -D luser -u 1000 -g 1000
USER luser
WORKDIR /home/luser

FROM docker.io/eclipse-temurin:25-jre-alpine AS run-base
RUN --mount=type=cache,target=/var/cache/apk apk add curl
RUN adduser -S -h /var/lib/xtrade xtrade
USER xtrade
WORKDIR /var/lib/xtrade

FROM build-base AS build
ADD --chown=1000:1000 . ./xtrade
WORKDIR /home/luser/xtrade
RUN --mount=type=cache,target=/home/luser/.gradle,uid=1000,gid=1000 ./gradlew distTar

FROM run-base AS release
RUN --mount=type=cache,ro,from=build,source=/home/luser/xtrade/build,target=/build tar -C /var/lib -xf /build/distributions/xtrade.tar
ADD conf/application.properties .
ADD --chown=xtrade:xtrade https://repo1.maven.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/2.22.0/opentelemetry-javaagent-2.22.0.jar opentelemetry-javaagent.jar
ENV JAVA_OPTS=-javaagent:opentelemetry-javaagent.jar
ENTRYPOINT "./bin/xtrade"
HEALTHCHECK CMD curl 'http://localhost:8080/actuator/health' -X GET -H 'Accept: application/json'
EXPOSE 8080