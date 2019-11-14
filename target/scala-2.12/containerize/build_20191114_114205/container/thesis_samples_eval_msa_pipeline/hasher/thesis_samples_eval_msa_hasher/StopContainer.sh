#!/bin/sh
docker network disconnect pipeline thesis_samples_eval_msa_hasher
               |docker network disconnect thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_hasherdocker stop thesis_samples_eval_msa_hasher
docker container rm -f thesis_samples_eval_msa_hasher