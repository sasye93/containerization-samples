#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_worker_master -f container/thesis_samples_worker_masterworker/master/thesis_samples_worker_master/Dockerfile container/thesis_samples_worker_masterworker/master/thesis_samples_worker_master/ 
