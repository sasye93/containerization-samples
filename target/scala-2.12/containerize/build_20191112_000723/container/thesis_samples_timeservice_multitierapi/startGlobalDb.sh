#!/bin/sh
docker volume create thesis_samples_timeservice_multitierapi_globaldb
docker run -d --name thesis_samples_timeservice_multitierapi_globaldb --network thesis_samples_timeservice_multitierapi --volume thesis_samples_timeservice_multitierapi_globaldb:/data -t mongo:latest
