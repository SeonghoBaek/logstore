#!/bin/sh
. ./build.sh
storm jar target/logstore-0.8.jar log.server.storm.topology.V1 V1 test wilson:2181

