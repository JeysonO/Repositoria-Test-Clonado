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
RUN mkdir -p tramite/file/documento-firma/externo

RUN echo 'deb http://deb.debian.org/debian/ buster contrib non-free' >> /etc/apt/sources.list
RUN echo ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true | debconf-set-selections
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y ttf-mscorefonts-installer && fc-cache -f -v

#RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*
#RUN apk --no-cache add msttcorefonts-installer fontconfig && \
#    update-ms-fonts && \
#    fc-cache -f


#ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "-Dspring.profiles.active=${profile}", "-Dsiops.cloud.config.host=${siopscloudhost}", "-Dsiops.cloud.config.port=${siopscloudport}", "/app/application.jar", "server"]

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "/app/application.jar", "server"]


