#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER dataarc;
    CREATE DATABASE dataarc;
    ALTER USER dataarc WITH PASSWORD 'dataarc';
    GRANT ALL PRIVILEGES ON DATABASE dataarc TO dataarc;
EOSQL
mkdir /var/lib/postgresql/backups/
echo  "0 1 * * * pg_dump -U dataarc dataarc > /var/lib/postgresql/backups/\`date  +%Y-%m-%d\`.sql" > .crontab 
crontab .crontab