#!/usr/bin/env bash

env='local'

nohup java -jar microservice/activity-center/target/activity-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/agent-center/target/agent-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/bike-center/target/bike-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/control-center/target/control-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/iot-center/target/iot-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/order-center/target/order-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/station-center/target/station-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/third/target/third.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

sleep 10

nohup java -jar microservice/user-center/target/user-center.jar \
--spring.profiles.active=${env} > qdigo.log 2>&1 &

