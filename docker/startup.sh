#!/bin/bash

if [ ! -f docke/postges/docke-data-load ]
then
    /us/bin/mvn clean compile -PloadTestData
    echo "" > docke/postges/docke-data-load
fi
/us/bin/mvn clean compile jetty:un -Pliquibase
