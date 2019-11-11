#!/bin/sh
docker rm --volumes -f thesis_samples_eval_msa_archiver
               |docker volume create thesis_samples_eval_msa_archiverdocker volume create thesis_samples_eval_msa_archiver_localdb
                 |docker network create --attachable -d overlay thesis_samples_eval_msa_archiver
                 |docker run -d --name thesis_samples_eval_msa_archiver_localdb --network thesis_samples_eval_msa_archiver --volume thesis_samples_eval_msa_archiver_localdb:/data -t Some(mongo:latest)|docker run -id  --name thesis_samples_eval_msa_archiver --network=thesis_samples_eval_msa_archiver --volume thesis_samples_eval_msa_archiver:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_eval_msa_archiver
                |docker network connect --alias thesis_samples_eval_msa_archiver thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_archiverdocker network connect --alias thesis_samples_eval_msa_archiver thesis_samples_eval_msa_archiver_localdb thesis_samples_eval_msa_archiver
docker container inspect -f "Container 'thesis_samples_eval_msa_archiver' connected to pipeline and thesis_samples_eval_msa_pipeline with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_eval_msa_archiver
