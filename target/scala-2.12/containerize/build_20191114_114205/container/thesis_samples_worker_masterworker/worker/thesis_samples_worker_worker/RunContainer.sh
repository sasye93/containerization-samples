#!/bin/sh
docker rm --volumes -f thesis_samples_worker_worker
               |docker volume create thesis_samples_worker_worker|docker run -id  --name thesis_samples_worker_worker --network=thesis_samples_worker_worker --volume thesis_samples_worker_worker:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_worker_worker
                |docker network connect --alias thesis_samples_worker_worker thesis_samples_worker_masterworker thesis_samples_worker_workerdocker container inspect -f "Container 'thesis_samples_worker_worker' connected to masterworker and thesis_samples_worker_masterworker with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_worker_worker
