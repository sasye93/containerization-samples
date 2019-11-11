#!/bin/sh
docker rm --volumes -f thesis_samples_eval_msa_hasher
               |docker volume create thesis_samples_eval_msa_hasher|docker run -id  --name thesis_samples_eval_msa_hasher --network=thesis_samples_eval_msa_hasher --volume thesis_samples_eval_msa_hasher:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_eval_msa_hasher
                |docker network connect --alias thesis_samples_eval_msa_hasher thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_hasherdocker container inspect -f "Container 'thesis_samples_eval_msa_hasher' connected to pipeline and thesis_samples_eval_msa_pipeline with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_eval_msa_hasher
