#!/bin/sh
. ./build.sh
. ./deploy_storm.sh
scp target/logstore-0.8.jar major@wilson:/home/major/Development/flume/lib/

