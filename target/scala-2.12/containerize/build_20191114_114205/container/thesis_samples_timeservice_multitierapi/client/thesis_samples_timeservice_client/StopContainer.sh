#!/bin/sh
docker network disconnect timeservice thesis_samples_timeservice_client
               |docker network disconnect thesis_samples_timeservice_multitierapi thesis_samples_timeservice_clientdocker stop thesis_samples_timeservice_client
docker container rm -f thesis_samples_timeservice_client