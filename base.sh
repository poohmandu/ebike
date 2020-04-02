#!/usr/bin/env bash

env='local'

nohup java -jar gateway/target/gateway.jar --spring.profiles.active=${env} \
> base.log 2>&1 &

nohup java -jar monitor/target/monitor.jar --spring.profiles.active=${env} \
> base.log 2>&1 &