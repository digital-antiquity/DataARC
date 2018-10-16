#!/bin/sh
DATE=`date +%Y-%m-%d`
mongodump -h mongo -o /backups/mongo-${DATE}
tar -cvzf /backups/mongo-${DATE}.tgz /backups/mongo-${DATE}/
rm -Rrf /backups/mongo-${DATE}
