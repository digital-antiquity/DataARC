#!/bin/bash

if [ ! -f docker/postgres/docker-data-load ]
then
    /usr/bin/mvn clean compile -PloadTestData
    echo "" > docker/postgres/docker-data-load
fi
export MAVEN_OPT='-XX:MaxPermSize=1024m -Xmx2148m'
/usr/bin/mvn clean compile jetty:run -Pliquibase
