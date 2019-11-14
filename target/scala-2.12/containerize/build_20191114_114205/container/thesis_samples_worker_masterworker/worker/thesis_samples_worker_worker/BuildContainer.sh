#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_worker_worker -f container/thesis_samples_worker_masterworker/worker/thesis_samples_worker_worker/Dockerfile container/thesis_samples_worker_masterworker/worker/thesis_samples_worker_worker/ 
