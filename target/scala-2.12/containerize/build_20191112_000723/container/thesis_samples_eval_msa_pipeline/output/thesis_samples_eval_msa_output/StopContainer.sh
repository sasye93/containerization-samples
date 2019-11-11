#!/bin/sh
docker network disconnect pipeline thesis_samples_eval_msa_output
               |docker network disconnect thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_outputdocker stop thesis_samples_eval_msa_output
docker container rm -f thesis_samples_eval_msa_output