# Dockerfile to build API Service Container
############################################################
#FROM openjdk:8u131-jre-alpine
FROM openjdk:11
MAINTAINER ealvino (edgard.alvino@gmail.com)
EXPOSE 8300
COPY ./target/amsac-tramite-service-1.0.jar /app/application.jar
RUN mkdir -p tramite/logs
RUN mkdir -p tramite/resource/reporte
RUN mkdir -p tramite/file
RUN mkdir -p tramite/file/documento-firma/externo
RUN mkdir -p tramite/file/logo-firma

RUN echo 'deb http://deb.debian.org/debian/ buster contrib non-free' >> /etc/apt/sources.list
RUN echo ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true | debconf-set-selections
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y ttf-mscorefonts-installer && fc-cache -f -v

#RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*
#RUN apk --no-cache add msttcorefonts-installer fontconfig && \
#    update-ms-fonts && \
#    fc-cache -f

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-javaagent:/tramite/config/elk/apm-agent/elastic-apm-agent-1.30.0.jar", "-Delastic.apm.service_name=amsac-tramite-service", "-Delastic.apm.server_urls=http://185.202.239.173:58200", "-Delastic.apm.environment=produccion", "-Delastic.apm.application_packages=pe.com.amsac.tramite", "-jar", "/app/application.jar", "server"]

#ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "-Dspring.profiles.active=${profile}", "-Dsiops.cloud.config.host=${siopscloudhost}", "-Dsiops.cloud.config.port=${siopscloudport}", "/app/application.jar", "server"]

#ENTRYPOINT ["java", "-Xms256m", "-Xmx2048m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "/app/application.jar", "server"]


