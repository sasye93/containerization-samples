#!/bin/sh
docker network disconnect pipeline thesis_samples_eval_msa_tagger
               |docker network disconnect thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_taggerdocker stop thesis_samples_eval_msa_tagger
docker container rm -f thesis_samples_eval_msa_tagger