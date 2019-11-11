#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_timeservice_service -f container/thesis_samples_timeservice_multitierapi/server/thesis_samples_timeservice_service/Dockerfile container/thesis_samples_timeservice_multitierapi/server/thesis_samples_timeservice_service/ 
