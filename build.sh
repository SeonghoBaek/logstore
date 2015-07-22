#!/bin/sh
mvn clean install
cp -R ~/workspace/LogStore/* ~/wilson_logstore_repo/
cp -f target/logstore-0.8.jar /cygdrive/d/workspace/TestLogStore/
