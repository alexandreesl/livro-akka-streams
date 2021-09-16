#!/usr/bin/env bash

docker-compose down
docker-compose up -d

sleep 10

sbt run