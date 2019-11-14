#!/bin/sh
cd ../../../../ 
docker build -t thesis_samples_eval_msa_archiver -f container/thesis_samples_eval_msa_pipeline/archiver/thesis_samples_eval_msa_archiver/Dockerfile container/thesis_samples_eval_msa_pipeline/archiver/thesis_samples_eval_msa_archiver/ 
