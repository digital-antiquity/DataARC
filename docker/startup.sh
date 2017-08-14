#!/bin/sh
/usr/bin/mvn clean compile -PloadTestData
/usr/bin/mvn clean compile jetty:run -Pliquibase