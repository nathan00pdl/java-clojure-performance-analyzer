#!/usr/bin/env bash

docker exec -it app-performance-test mvn clean test-compile
docker exec -it app-performance-test mvn gatling:test