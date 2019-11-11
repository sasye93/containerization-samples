#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_eval_msa_hasher -f container/thesis_samples_eval_msa_pipeline/hasher/thesis_samples_eval_msa_hasher/Dockerfile container/thesis_samples_eval_msa_pipeline/hasher/thesis_samples_eval_msa_hasher/ 
