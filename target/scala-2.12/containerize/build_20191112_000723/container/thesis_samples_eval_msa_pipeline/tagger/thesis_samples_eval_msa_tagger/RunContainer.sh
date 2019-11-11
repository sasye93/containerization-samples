#!/bin/sh
docker rm --volumes -f thesis_samples_eval_msa_tagger
               |docker volume create thesis_samples_eval_msa_tagger|docker run -id  --name thesis_samples_eval_msa_tagger --network=thesis_samples_eval_msa_tagger --volume thesis_samples_eval_msa_tagger:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_samples_eval_msa_tagger
                |docker network connect --alias thesis_samples_eval_msa_tagger thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_taggerdocker container inspect -f "Container 'thesis_samples_eval_msa_tagger' connected to pipeline and thesis_samples_eval_msa_pipeline with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_samples_eval_msa_tagger
