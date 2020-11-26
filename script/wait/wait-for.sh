#!/bin/sh

if [ $WAIT_FOR ]; then
    echo "等待端口:$WAIT_FOR"
    sh /script/entrypoint.sh -d "$WAIT_FOR"
    echo "等待结束！！！"
else
    echo "无需等待,直接启动"
fi