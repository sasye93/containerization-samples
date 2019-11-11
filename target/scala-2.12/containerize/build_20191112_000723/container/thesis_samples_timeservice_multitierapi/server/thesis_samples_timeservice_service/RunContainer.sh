#!/bin/sh
docker rm --volumes -f thesis_samples_timeservice_service
               |docker volume create thesis_samples_timeservice_servicedocker volume create thesis_samples_timeservice_service_localdb
                 |docker network create --attachable -d overlay thesis_samples_timeservice_service
                 |docker run -d --name thesis_samples_timeservice_service_localdb --network thesis_samples_timeservice_service --volume thesis_samples_timeservice_service_localdb:/data -t Some(mongo:latest)|docker run -id  --name thesis_samples_timeservice_service --network=thesis_samples_timeservice_service --volume thesis_samples_timeservice_service:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_timeservice_service
                |docker network connect --alias thesis_samples_timeservice_service thesis_samples_timeservice_multitierapi thesis_samples_timeservice_servicedocker network connect --alias thesis_samples_timeservice_service thesis_samples_timeservice_service_localdb thesis_samples_timeservice_service
docker container inspect -f "Container 'thesis_samples_timeservice_service' connected to timeservice and thesis_samples_timeservice_multitierapi with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_timeservice_service
