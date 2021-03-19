FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

# Standard packages
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y vim sudo git wget unzip curl

# Java
RUN apt-get install -y openjdk-8-jdk

# Scala
RUN wget http://scala-lang.org/files/archive/scala-2.12.2.deb
RUN dpkg -i scala-2.12.2.deb

# Npm
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash - 
RUN apt-get install -y nodejs 

# Sbt
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
RUN apt-get update && apt-get install -y sbt 

# User
RUN useradd -ms /bin/bash kamilczerwinski
RUN adduser kamilczerwinski sudo

EXPOSE 3000

USER kamilczerwinski
WORKDIR /home/kamilczerwinski
RUN mkdir /home/kamilczerwinski/ebiznes2021

VOLUME /home/kamilczerwinsk/ebiznes2021