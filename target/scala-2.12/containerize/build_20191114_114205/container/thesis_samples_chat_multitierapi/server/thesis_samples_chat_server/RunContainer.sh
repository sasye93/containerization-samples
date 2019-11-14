#!/bin/sh
docker rm --volumes -f thesis_samples_chat_server
               |docker volume create thesis_samples_chat_serverdocker volume create thesis_samples_chat_server_localdb
                 |docker network create --attachable -d overlay thesis_samples_chat_server
                 |docker run -d --name thesis_samples_chat_server_localdb --network thesis_samples_chat_server --volume thesis_samples_chat_server_localdb:/data -t Some(mongo:latest)|docker run -id  --name thesis_samples_chat_server --network=thesis_samples_chat_server --volume thesis_samples_chat_server:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_chat_server
                |docker network connect --alias thesis_samples_chat_server thesis_samples_chat_multitierapi thesis_samples_chat_serverdocker network connect --alias thesis_samples_chat_server thesis_samples_chat_server_localdb thesis_samples_chat_server
docker container inspect -f "Container 'thesis_samples_chat_server' connected to chat and thesis_samples_chat_multitierapi with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_chat_server
