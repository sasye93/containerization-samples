#!/bin/sh
docker stack rm thesis.samples.eval.msa.Pipeline
docker network rm thesis.samples.eval.msa.Pipeline
docker stack rm thesis.samples.timeservice.MultitierApi
docker network rm thesis.samples.timeservice.MultitierApi
docker network rm pipeline
docker network rm timeservice
docker swarm leave -f