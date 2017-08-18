#!/bin/sh

if [ ! -f docker/postgres/docker-data-load ]; then
    /usr/bin/mvn clean compile -PloadTestData
    echo "" > postgres/docker-data-load
fi
/usr/bin/mvn clean compile jetty:run -Pliquibase