#!/bin/sh
docker network disconnect pipeline thesis_samples_eval_msa_input
               |docker network disconnect thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_inputdocker stop thesis_samples_eval_msa_input
docker container rm -f thesis_samples_eval_msa_input