#!/bin/sh
docker rm --volumes -f thesis_samples_eval_msa_output
               |docker volume create thesis_samples_eval_msa_output|docker run -id  --name thesis_samples_eval_msa_output --network=thesis_samples_eval_msa_output --volume thesis_samples_eval_msa_output:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_eval_msa_output
                |docker network connect --alias thesis_samples_eval_msa_output thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_outputdocker container inspect -f "Container 'thesis_samples_eval_msa_output' connected to pipeline and thesis_samples_eval_msa_pipeline with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_eval_msa_output
