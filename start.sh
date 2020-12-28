#!/bin/bash


# remove old server if found
bash ./stop-server.sh

# compile server codebase
cd www/
javac *.java

# start server
cd ../server/
javac *.java
java DynamicSudokuServer > server.log 2>&1 &

# start client
cd ../client/
javac *.java
terminator --geometry 1000x800+300 -x java DynamicSudokuClient
# java DynamicSudokuClient
