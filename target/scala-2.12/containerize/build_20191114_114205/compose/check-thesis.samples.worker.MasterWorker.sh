#!/bin/sh
echo "Status for stack thesis.samples.worker.MasterWorker:"
echo "--------------------------------------------------------------------------"
echo "Status of service worker:"
echo "----------------- start of service error output --------------------------"
ID="$(docker service ps --filter "desired-state=running" --format "{{.ID}}" thesis_samples_worker_masterworker_worker)"
if [ -z "${ID}" ]; then
  echo "Service not running."
  else
    OUT="$(docker service logs --raw --details --timestamps "$ID") $(docker ps --no-trunc --format "{{.Error}}" --filter "id=$ID")"
    ([ "${#OUT}" -lt 2 ] || echo "Everything ok.") &&  echo "$OUT"
fi
echo "----------------- end of service error output --------------------------"
echo "--------------------------------------------------------------------------"
echo "Status of service master:"
echo "----------------- start of service error output --------------------------"
ID="$(docker service ps --filter "desired-state=running" --format "{{.ID}}" thesis_samples_worker_masterworker_master)"
if [ -z "${ID}" ]; then
  echo "Service not running."
  else
    OUT="$(docker service logs --raw --details --timestamps "$ID") $(docker ps --no-trunc --format "{{.Error}}" --filter "id=$ID")"
    ([ "${#OUT}" -lt 2 ] || echo "Everything ok.") &&  echo "$OUT"
fi
echo "----------------- end of service error output --------------------------"
echo "Note: This script will only show running services, to show the error logs of failed services, use docker service/container ls -a to get the respective ip and then docker service/container logs <ID> and docker ps --filter 'id=<ID>'".