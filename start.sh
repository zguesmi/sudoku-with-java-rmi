#!/bin/bash

cd www/
javac *.java
cd ../server/
java DynamicSudokuServer &
cd ../client/
terminator --geometry 1000x800+300 -x java DynamicSudokuClient
java DynamicSudokuClient

