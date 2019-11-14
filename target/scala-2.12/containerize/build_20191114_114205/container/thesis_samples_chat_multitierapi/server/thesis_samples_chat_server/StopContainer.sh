#!/bin/sh
docker network disconnect chat thesis_samples_chat_server
               |docker network disconnect thesis_samples_chat_multitierapi thesis_samples_chat_serverdocker network disconnect thesis_samples_chat_server_localdb thesis_samples_chat_server
docker stop thesis_samples_chat_server_localdb
docker container rm -f thesis_samples_chat_server_localdb
docker stop thesis_samples_chat_server
docker container rm -f thesis_samples_chat_server