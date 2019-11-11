#!/bin/sh
docker network disconnect pipeline thesis_samples_eval_msa_archiver
               |docker network disconnect thesis_samples_eval_msa_pipeline thesis_samples_eval_msa_archiverdocker network disconnect thesis_samples_eval_msa_archiver_localdb thesis_samples_eval_msa_archiver
docker stop thesis_samples_eval_msa_archiver_localdb
docker container rm -f thesis_samples_eval_msa_archiver_localdb
docker stop thesis_samples_eval_msa_archiver
docker container rm -f thesis_samples_eval_msa_archiver