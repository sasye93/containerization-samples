#!/bin/sh
docker stack rm thesis.samples.worker.MasterWorker
docker network rm thesis.samples.worker.MasterWorker
docker stack rm thesis.samples.chat.MultitierApi
docker network rm thesis.samples.chat.MultitierApi
docker stack rm thesis.listings.APIGateway.timeservice.MultitierApi
docker network rm thesis.listings.APIGateway.timeservice.MultitierApi
docker stack rm thesis.samples.timeservice.MultitierApi
docker network rm thesis.samples.timeservice.MultitierApi
docker stack rm thesis.listings.APIGateway.gateway.api.ConsumerApi
docker network rm thesis.listings.APIGateway.gateway.api.ConsumerApi
docker stack rm thesis.samples.eval.msa.Pipeline
docker network rm thesis.samples.eval.msa.Pipeline
docker network rm timeservice
docker network rm containerized_scalaloci_project
docker network rm timeservice_gateway
docker network rm pipeline
docker network rm chat
docker network rm masterworker
docker swarm leave -f