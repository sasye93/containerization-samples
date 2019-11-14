#!/bin/sh
(docker node ls | grep "Leader") > /dev/null 2>&1
if [ $? -ne 0 ]; then
  docker swarm init --advertise-addr eth0
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
SKIP_NET_INIT=0
docker network inspect containerized_scalaloci_project > /dev/null 2>&1
if [ $? -eq 0 ]; then
  docker network rm containerized_scalaloci_project > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Could not remove network containerized_scalaloci_project. Continuing with the old network. Remove network manually to update it next time."
    SKIP_NET_INIT=1
  fi
fi
if [ $SKIP_NET_INIT -eq 0 ]; then
  docker network create -d overlay --attachable=true containerized_scalaloci_project
fi
SKIP_NET_INIT=0
docker network inspect timeservice_gateway > /dev/null 2>&1
if [ $? -eq 0 ]; then
  docker network rm timeservice_gateway > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Could not remove network timeservice_gateway. Continuing with the old network. Remove network manually to update it next time."
    SKIP_NET_INIT=1
  fi
fi
if [ $SKIP_NET_INIT -eq 0 ]; then
  docker network create -d overlay --attachable=true timeservice_gateway
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
docker network inspect chat > /dev/null 2>&1
if [ $? -eq 0 ]; then
  docker network rm chat > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Could not remove network chat. Continuing with the old network. Remove network manually to update it next time."
    SKIP_NET_INIT=1
  fi
fi
if [ $SKIP_NET_INIT -eq 0 ]; then
  docker network create -d overlay --attachable=true chat
fi
SKIP_NET_INIT=0
docker network inspect masterworker > /dev/null 2>&1
if [ $? -eq 0 ]; then
  docker network rm masterworker > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo "Could not remove network masterworker. Continuing with the old network. Remove network manually to update it next time."
    SKIP_NET_INIT=1
  fi
fi
if [ $SKIP_NET_INIT -eq 0 ]; then
  docker network create -d overlay --attachable=true masterworker
fi
echo "---------------------------------------------"
echo ">>> Creating stacks from compose files... <<<"
echo "---------------------------------------------"
bash stack-thesis.samples.worker.MasterWorker.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
bash stack-thesis.samples.chat.MultitierApi.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
bash stack-thesis.listings.APIGateway.timeservice.MultitierApi.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
bash stack-thesis.samples.timeservice.MultitierApi.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
bash stack-thesis.listings.APIGateway.gateway.api.ConsumerApi.sh
if [ $? -ne 0 ]; then
  exit 1;
fi
bash stack-thesis.samples.eval.msa.Pipeline.sh
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
echo "Services in stack 'thesis_samples_worker_masterworker':"
docker stack services thesis_samples_worker_masterworker
echo "-----------------"
echo "Services in stack 'thesis_samples_chat_multitierapi':"
docker stack services thesis_samples_chat_multitierapi
echo "-----------------"
echo "Services in stack 'thesis_listings_apigateway_timeservice_multitierapi':"
docker stack services thesis_listings_apigateway_timeservice_multitierapi
echo "-----------------"
echo "Services in stack 'thesis_samples_timeservice_multitierapi':"
docker stack services thesis_samples_timeservice_multitierapi
echo "-----------------"
echo "Services in stack 'thesis_listings_apigateway_gateway_api_consumerapi':"
docker stack services thesis_listings_apigateway_gateway_api_consumerapi
echo "-----------------"
echo "Services in stack 'thesis_samples_eval_msa_pipeline':"
docker stack services thesis_samples_eval_msa_pipeline

echo "----------------------------------"
echo ">>> All services in the Swarm: <<<"
echo "----------------------------------"
docker service ls
echo "--------------------------"
echo "Swarm initialization done. Note that you might have to forward ports to your machine to make them accessable if you run Docker inside a VM (e.g. toolbox)."
echo ">> PRESS ANY KEY TO CONTINUE / CLOSE <<"
read -n 1 -s
exit 0
