-- init/01_init.sql
CREATE USER cluster23 WITH ENCRYPTED PASSWORD 'cluster23';
CREATE DATABASE quant_helper;
GRANT ALL PRIVILEGES ON DATABASE quant_helper TO cluster23;
