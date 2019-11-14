#!/bin/sh
docker network inspect thesis_samples_eval_msa_pipeline > /dev/null 2>&1
if [ $? -eq 0 ]; then
 docker network rm thesis_samples_eval_msa_pipeline > /dev/null 2>&1
 if [ $? -ne 0 ]; then
   echo "Network 'thesis_samples_eval_msa_pipeline' already exists, but could not be removed and re-instantiated, probably because it is still in use. If you want to update the network, you must manually remove the old network by first decoupling all connected containers and services from the network ('docker container rm <containerId>', 'docker service rm <serviceId>'), and then 'docker network rm thesis_samples_eval_msa_pipeline'."
   exit 1
 fi
fi
docker network create --attachable -d overlay thesis_samples_eval_msa_pipeline