#!/usr/bin/env bash

sbt docker:publishLocal

docker-compose down
docker-compose up -d

sleep 10

sbt gatling:test