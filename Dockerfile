FROM ubuntu:xenial

LABEL MAINTAINER=adam.brin@asu.edu




# Install Java.
RUN apt-get update && apt-get install -y  --no-install-recommends software-properties-common \
  && echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer postgresql-client && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer
  
  
  

  # Define commonly used JAVA_HOME variable
  ENV JAVA_HOME /usr/lib/jvm/java-8-oracle


  ##### MAVEN

ARG MAVEN_VERSION=3.5.2
ARG USER_HOME_DIR="/root"
# ARG SHA1=a677b8398313325d6c266279cb8d385bbc9d435d
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries



  RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
    && wget -O /tmp/apache-maven.tar.gz "${BASE_URL}/apache-maven-$MAVEN_VERSION-bin.tar.gz" \
    && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
    && rm -f /tmp/apache-maven.tar.gz \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

  ENV MAVEN_HOME /usr/share/maven
  ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"


  WORKDIR /app

  # Copy the current directory contents into the container at /app
  ADD . /app

# RUN mvn clean compile jetty:run
  ENTRYPOINT ["/bin/sh","/app/docker/startup.sh"]
  EXPOSE 8280