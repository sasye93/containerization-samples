#!/bin/sh
docker network disconnect timeservice thesis_samples_timeservice_service
               |docker network disconnect thesis_samples_timeservice_multitierapi thesis_samples_timeservice_servicedocker network disconnect thesis_samples_timeservice_service_localdb thesis_samples_timeservice_service
docker stop thesis_samples_timeservice_service_localdb
docker container rm -f thesis_samples_timeservice_service_localdb
docker stop thesis_samples_timeservice_service
docker container rm -f thesis_samples_timeservice_service