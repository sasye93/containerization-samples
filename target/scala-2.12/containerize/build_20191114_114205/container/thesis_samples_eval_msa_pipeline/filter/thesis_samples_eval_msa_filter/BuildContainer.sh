#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_eval_msa_filter -f container/thesis_samples_eval_msa_pipeline/filter/thesis_samples_eval_msa_filter/Dockerfile container/thesis_samples_eval_msa_pipeline/filter/thesis_samples_eval_msa_filter/ 
