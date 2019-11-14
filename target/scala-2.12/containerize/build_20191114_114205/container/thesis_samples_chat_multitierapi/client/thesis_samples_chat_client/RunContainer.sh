#!/bin/sh
docker rm --volumes -f thesis_samples_chat_client
               |docker volume create thesis_samples_chat_client|docker run -id  --name thesis_samples_chat_client --network=thesis_samples_chat_client --volume thesis_samples_chat_client:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_chat_client
                |docker network connect --alias thesis_samples_chat_client thesis_samples_chat_multitierapi thesis_samples_chat_clientdocker container inspect -f "Container 'thesis_samples_chat_client' connected to chat and thesis_samples_chat_multitierapi with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_chat_client
