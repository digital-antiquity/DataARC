#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER dataarc;
    CREATE DATABASE dataarc;
    GRANT ALL PRIVILEGES ON DATABASE dataarc TO dataarc;
EOSQL