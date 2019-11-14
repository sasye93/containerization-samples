#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_eval_msa_output -f container/thesis_samples_eval_msa_pipeline/output/thesis_samples_eval_msa_output/Dockerfile container/thesis_samples_eval_msa_pipeline/output/thesis_samples_eval_msa_output/ 
