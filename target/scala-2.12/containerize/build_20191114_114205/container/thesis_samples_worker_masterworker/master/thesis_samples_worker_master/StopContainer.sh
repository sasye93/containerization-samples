#!/bin/sh
docker network disconnect masterworker thesis_samples_worker_master
               |docker network disconnect thesis_samples_worker_masterworker thesis_samples_worker_masterdocker stop thesis_samples_worker_master
docker container rm -f thesis_samples_worker_master