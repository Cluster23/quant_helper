FROM postgres:16

ENV POSTGRES_USER=myuser
ENV POSTGRES_PASSWORD=mypassword
ENV POSTGRES_DB=mydatabase

COPY initial.sql /docker-entrypoint-initdb.d/
