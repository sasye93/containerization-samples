#!/bin/sh
(docker node ls | grep "Leader") > /dev/null 2>&1
if [ $? -ne 0 ]; then
  docker swarm init --advertise-addr eth0
fi
SKIP_NET_INIT=0
docker network inspect pipeline > /dev/null 2>&1
if [ $? -eq 0 ]; then
  docker network rm pipeline > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Could not remove network pipeline. Continuing with the old network. Remove network manually to update it next time."
    SKIP_NET_INIT=1
  fi
fi
if [ $SKIP_NET_INIT -eq 0 ]; then
  docker network create -d overlay --attachable=true pipeline
fi
SKIP_NET_INIT=0
docker network inspect timeservice > /dev/null 2>&1
if [ $? -eq 0 ]; then
  docker network rm timeservice > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Could not remove network timeservice. Continuing with the old network. Remove network manually to update it next time."
    SKIP_NET_INIT=1
  fi
fi
if [ $SKIP_NET_INIT -eq 0 ]; then
  docker network create -d overlay --attachable=true timeservice
fi
echo "---------------------------------------------"
echo ">>> Creating stacks from compose files... <<<"
echo "---------------------------------------------"
bash stack-thesis.samples.eval.msa.Pipeline.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
bash stack-thesis.samples.timeservice.MultitierApi.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
echo "-----------------------"
echo ">>> Nodes in Swarm: <<<"
echo "-----------------------"
docker node ls
docker swarm join-token manager
docker swarm join-token worker
echo "------------------------"
echo ">>> Stacks in Swarm: <<<"
echo "------------------------"
docker stack ls
echo "------------------------"
echo "-----------------"
echo "Services in stack 'thesis_samples_eval_msa_pipeline':"
docker stack services thesis_samples_eval_msa_pipeline
echo "-----------------"
echo "Services in stack 'thesis_samples_timeservice_multitierapi':"
docker stack services thesis_samples_timeservice_multitierapi

echo "----------------------------------"
echo ">>> All services in the Swarm: <<<"
echo "----------------------------------"
docker service ls
echo "--------------------------"
echo "Swarm initialization done. Note that you might have to forward ports to your machine to make them accessable if you run Docker inside a VM (e.g. toolbox)."
echo ">> PRESS ANY KEY TO CONTINUE / CLOSE <<"
read -n 1 -s
exit 0
