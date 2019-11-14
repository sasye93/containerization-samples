#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_chat_server -f container/thesis_samples_chat_multitierapi/server/thesis_samples_chat_server/Dockerfile container/thesis_samples_chat_multitierapi/server/thesis_samples_chat_server/ 
