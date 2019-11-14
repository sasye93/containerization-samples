#!/bin/sh
docker network disconnect chat thesis_samples_chat_client
               |docker network disconnect thesis_samples_chat_multitierapi thesis_samples_chat_clientdocker stop thesis_samples_chat_client
docker container rm -f thesis_samples_chat_client