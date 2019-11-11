#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_eval_msa_input -f container/thesis_samples_eval_msa_pipeline/input/thesis_samples_eval_msa_input/Dockerfile container/thesis_samples_eval_msa_pipeline/input/thesis_samples_eval_msa_input/ 
