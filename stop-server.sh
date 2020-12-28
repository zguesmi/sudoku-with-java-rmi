#!/bin/bash

serverPID=`ps -aux | grep ".* \? .*java DynamicSudokuServer.*" | tr -s " " | cut -d' ' -f2 | cut -d' ' -f1`
IFS=' ' read -r -a array <<< "$serverPID"
PID=${array[0]}
echo $PID
kill -9 $PID
