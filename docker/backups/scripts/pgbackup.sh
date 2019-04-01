#!/bin/sh
DATE=`date +%Y-%m-%d`
pg_dump -h db -U dataarc -f /backups/pg/dataarc-${DATE}.sql dataarc
gzip /backups/pg/dataarc-${DATE}.sql