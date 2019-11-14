#!/bin/sh
docker rm --volumes -f thesis_samples_worker_master
               |docker volume create thesis_samples_worker_master|docker run -id  --name thesis_samples_worker_master --network=thesis_samples_worker_master --volume thesis_samples_worker_master:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_worker_master
                |docker network connect --alias thesis_samples_worker_master thesis_samples_worker_masterworker thesis_samples_worker_masterdocker container inspect -f "Container 'thesis_samples_worker_master' connected to masterworker and thesis_samples_worker_masterworker with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_worker_master
