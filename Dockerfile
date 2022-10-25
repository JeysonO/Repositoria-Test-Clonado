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
#RUN apt install fonts-dejavu && rm -rf /var/cache/apt/*

#RUN apt-get update
#RUN echo 'deb http://ftp.us.debian.org/debian jessie main contrib' >> /etc/apt/sources.list
RUN echo 'deb http://deb.debian.org/debian/ buster contrib non-free' >> /etc/apt/sources.list
RUN echo ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true | debconf-set-selections
#RUN DEBIAN_FRONTEND=noninteractive apt-get install ttf-mscorefonts-installer fonts-liberation && fc-cache -f -v
#RUN DEBIAN_FRONTEND=noninteractive apt-get install -y ttf-mscorefonts-installer && fc-cache -f -v

#RUN apt-get update
#RUN echo "deb http://us-west-2.ec2.archive.ubuntu.com/ubuntu/ trusty multiverse \
#          deb http://us-west-2.ec2.archive.ubuntu.com/ubuntu/ trusty-updates multiverse \
#          deb http://us-west-2.ec2.archive.ubuntu.com/ubuntu/ trusty-backports main restricted universe multiverse" | sudo tee /etc/apt/sources.list.d/multiverse.list
RUN apt-get update
#RUN apt-get install -y ttf-mscorefonts-installer
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y ttf-mscorefonts-installer && fc-cache -f -v

#RUN echo "deb http://deb.debian.org/debian/ buster contrib non-free" >> /etc/apt/sources.list
#RUN echo ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true | debconf-set-selections
#RUN DEBIAN_FRONTEND=noninteractive apt-get install ttf-mscorefonts-installer fonts-liberation && fc-cache -f -v


#RUN apt-get install ttf dejavu extra -y
#RUN echo "ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true" | debconf-set-selections
#RUN apt-get install -y --no-install-recommends fontconfig ttf-mscorefonts-installer
#ADD localfonts.conf /etc/fonts/fonts.conf
#RUN fc-cache -f -v

#RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*
#RUN apk --no-cache add msttcorefonts-installer fontconfig && \
    #update-ms-fonts && \
    #fc-cache -f


#ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "-Dspring.profiles.active=${profile}", "-Dsiops.cloud.config.host=${siopscloudhost}", "-Dsiops.cloud.config.port=${siopscloudport}", "/app/application.jar", "server"]

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-Duser.timezone=America/Lima", "-Dfile.encoding=UTF-8", "-jar", "/app/application.jar", "server"]


