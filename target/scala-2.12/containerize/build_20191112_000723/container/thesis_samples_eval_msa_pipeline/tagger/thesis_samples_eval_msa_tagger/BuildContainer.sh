#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_eval_msa_tagger -f container/thesis_samples_eval_msa_pipeline/tagger/thesis_samples_eval_msa_tagger/Dockerfile container/thesis_samples_eval_msa_pipeline/tagger/thesis_samples_eval_msa_tagger/ 
