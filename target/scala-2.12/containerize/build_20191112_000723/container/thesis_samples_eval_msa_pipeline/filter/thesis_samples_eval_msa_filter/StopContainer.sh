#!/bin/sh
docker network disconnect pipeline thesis_samples_eval_msa_filter
               |docker network disconnect thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_filterdocker stop thesis_samples_eval_msa_filter
docker container rm -f thesis_samples_eval_msa_filter