#!/bin/sh
(docker node ls --filter "role=manager" --format "{{.Self}}" | grep "true") > /dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "It appears that this node is not a Swarm Manager. You can only deploy a Stack to a Swarm from one of its manager nodes (use swarm-init.sh, 'docker swarm init' or 'docker swarm join')."
  exit 1
fi
docker network inspect pipeline > /dev/null 2>&1
if [ $? -ne 0 ]; then
  docker network create -d overlay --attachable=true pipeline
fi
docker network rm thesis_samples_eval_msa_pipeline > /dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "Network thesis_samples_eval_msa_pipeline removed."
fi
ERR=0
docker images -q thesis_samples_eval_msa_output > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image thesis_samples_eval_msa_output, which is needed by service output. This service will not start up."
fi
docker images -q thesis_samples_eval_msa_archiver > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image thesis_samples_eval_msa_archiver, which is needed by service archiver. This service will not start up."
fi
docker images -q thesis_samples_eval_msa_hasher > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image thesis_samples_eval_msa_hasher, which is needed by service hasher. This service will not start up."
fi
docker images -q thesis_samples_eval_msa_filter > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image thesis_samples_eval_msa_filter, which is needed by service filter. This service will not start up."
fi
docker images -q thesis_samples_eval_msa_tagger > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image thesis_samples_eval_msa_tagger, which is needed by service tagger. This service will not start up."
fi
docker images -q thesis_samples_eval_msa_input > /dev/null 2>&1
if [ $? -ne 0 ]; then
  ERR=1
  echo "Could not find image thesis_samples_eval_msa_input, which is needed by service input. This service will not start up."
fi
if [ $ERR -ne 0 ]; then
  echo "Error: At least one service did not found its base image. Remember that you need to publish images to a repository (enable publishing option) in order to deploy a Swarm on multiple nodes."
fi
docker stack deploy -c files/thesis.samples.eval.msa.Pipeline.yml thesis_samples_eval_msa_pipeline
if [ $? -eq 0 ]; then
  echo "Successfully deployed stack 'thesis_samples_eval_msa_pipeline'."
  else
    echo "Error while deploying stack 'thesis_samples_eval_msa_pipeline', aborting now. Please fix before retrying."
    exit 1
fi
