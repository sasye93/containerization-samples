#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_timeservice_client -f container/thesis_samples_timeservice_multitierapi/client/thesis_samples_timeservice_client/Dockerfile container/thesis_samples_timeservice_multitierapi/client/thesis_samples_timeservice_client/ 
