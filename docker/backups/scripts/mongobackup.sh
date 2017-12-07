#!/usr/bin/sh
DATE=`date +%Y-%m-%d`
mongodump -h mongo -o /backups/mongo-${DATE}
