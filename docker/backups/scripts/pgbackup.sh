#!/bin/sh
DATE=`date +%Y-%m-%d`
pg_dump -h db -U dataarc -f /backups/dataarc-${DATE}.sql dataarc
gzip /backups/dataarc-${DATE}.sql