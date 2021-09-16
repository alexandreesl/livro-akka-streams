#!/usr/bin/env bash

docker-compose up -d --scale api=0

sleep 10

sbt run