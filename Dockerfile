# Dockerfile to build API Service Container
############################################################
#FROM openjdk:8u131-jre-alpine
FROM openjdk:11
MAINTAINER ealvino (edgard.alvino@gmail.com)
EXPOSE 8100
COPY ./target/amsac-tramite-api-1.0.jar /app/application.jar
RUN mkdir -p tramite/logs
RUN mkdir -p tramite/resource/reporte
RUN mkdir -p tramite/file
RUN apt add --update ttf-dejavu && rm -rf /var/cache/apk/*
RUN apt --no-cache add msttcorefonts-installer fontconfig && \
    update-ms-fonts && \
    fc-cache -f
#ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "-Dspring.profiles.active=${profile}", "-Dsiops.cloud.config.host=${siopscloudhost}", "-Dsiops.cloud.config.port=${siopscloudport}", "/app/application.jar", "server"]

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "/app/application.jar", "server"]


