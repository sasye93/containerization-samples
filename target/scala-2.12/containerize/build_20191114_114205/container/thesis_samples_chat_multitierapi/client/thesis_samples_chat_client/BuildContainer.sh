#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_chat_client -f container/thesis_samples_chat_multitierapi/client/thesis_samples_chat_client/Dockerfile container/thesis_samples_chat_multitierapi/client/thesis_samples_chat_client/ 
