#!/bin/sh
docker network disconnect masterworker thesis_samples_worker_worker
               |docker network disconnect thesis_samples_worker_masterworker thesis_samples_worker_workerdocker stop thesis_samples_worker_worker
docker container rm -f thesis_samples_worker_worker