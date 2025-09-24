FROM ubuntu:22.04
LABEL maintainer="mbarna@eliassen.com"
RUN apt-get update -y
RUN apt install -y wget unzip
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get install -y tzdata
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install ./google-chrome-stable_current_amd64.deb -y
RUN apt -f install -y
RUN apt-get update -y
RUN apt-get install openjdk-17-jre -y
RUN useradd -ms /bin/bash jenkins
RUN echo "jenkins:jenkins" | chpasswd
RUN mkdir testing
RUN chmod -R a+rwx testing
COPY target/DemoTAFramework-1.0.jar /testing/TestFramework.jar
RUN chmod a+rwx /testing/TestFramework.jar
EXPOSE 22

