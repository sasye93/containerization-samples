#!/bin/sh
(docker node ls --filter "role=manager" --format "{{.Self}}" | grep "true") > /dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "It appears that this node is not a Swarm Manager. You can only deploy a Stack to a Swarm from one of its manager nodes (use swarm-init.sh, 'docker swarm init' or 'docker swarm join')."
  exit 1
fi
docker network inspect masterworker > /dev/null 2>&1
if [ $? -ne 0 ]; then
  docker network create -d overlay --attachable=true masterworker
fi
docker network rm thesis_samples_worker_masterworker > /dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "Network thesis_samples_worker_masterworker removed."
fi
ERR=0
docker images -q scalalocicontainerize/thesis:thesis_samples_worker_worker > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image scalalocicontainerize/thesis:thesis_samples_worker_worker, which is needed by service worker. This service will not start up."
fi
docker images -q scalalocicontainerize/thesis:thesis_samples_worker_master > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image scalalocicontainerize/thesis:thesis_samples_worker_master, which is needed by service master. This service will not start up."
fi
if [ $ERR -ne 0 ]; then
  echo "Error: At least one service did not found its base image. Remember that you need to publish images to a repository (enable publishing option) in order to deploy a Swarm on multiple nodes."
fi
docker stack deploy -c files/thesis.samples.worker.MasterWorker.yml thesis_samples_worker_masterworker
if [ $? -eq 0 ]; then
  echo "Successfully deployed stack 'thesis_samples_worker_masterworker'."
  else
    echo "Error while deploying stack 'thesis_samples_worker_masterworker', aborting now. Please fix before retrying."
    exit 1
fi
