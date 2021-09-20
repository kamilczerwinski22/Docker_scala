FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

# Standard packages
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -y vim sudo git wget unzip curl

# Java
RUN apt-get install -y openjdk-8-jdk

# Scala
RUN wget http://scala-lang.org/files/archive/scala-2.12.13.deb
RUN dpkg -i scala-2.12.13.deb

# Npm
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y nodejs

# Sbt
RUN wget https://scala.jfrog.io/artifactory/debian/sbt-1.5.1.deb
RUN dpkg -i sbt-1.5.1.deb
RUN apt-get update
RUN apt-get install sbt

# User
RUN useradd -ms /bin/bash kamilczerwinski
RUN adduser kamilczerwinski sudo


SHELL ["/bin/bash", "-c"]

EXPOSE 8080
EXPOSE 3000
EXPOSE 9000

USER kamilczerwinski

VOLUME ["/home/kamilczerwinsk/ebiznes2021"]
COPY . /home/kamilczerwinsk/ebiznes2021
WORKDIR /home/kamilczerwinsk/ebiznes2021

RUN cd /home/kamilczerwinsk/ebiznes2021
CMD ["sbt"]
