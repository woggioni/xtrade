FROM docker.io/eclipse-temurin:25-jre-alpine
RUN adduser -S -h /var/lib/rbcs rbcs
USER rbcs
WORKDIR /var/lib/rbcs
ADD --chown=rbcs:rbcs https://gitea.woggioni.net/api/packages/woggioni/maven/net/woggioni/rbcs-cli/0.3.6/rbcs-cli-0.3.6.jar /var/lib/rbcs/rbcs-cli.jar
ADD --chown=rbcs:rbcs ./rbcs-client.xml ./rbcs-client.xml
ENTRYPOINT ["java", "-jar", "rbcs-cli.jar", "client", "-c", "rbcs-client.xml", "-p", "xcache", "benchmark", "-e", "50000", "-s", "1024", "-r"]
