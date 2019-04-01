#!/bin/sh
DATE=`date +%Y-%m-%d`
mongodump -h mongo -o /backups/mongo/mongo-${DATE}
tar -cvzf /backups/mongo/mongo-${DATE}.tgz /backups/mongo/mongo-${DATE}/
rm -Rrf /backups/mongo/mongo-${DATE}
