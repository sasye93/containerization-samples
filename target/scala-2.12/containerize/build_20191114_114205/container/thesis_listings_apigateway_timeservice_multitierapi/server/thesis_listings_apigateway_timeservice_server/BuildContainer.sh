#!/bin/sh
cd ../../../../ 
docker build -t thesis_listings_apigateway_timeservice_server -f container/thesis_listings_apigateway_timeservice_multitierapi/server/thesis_listings_apigateway_timeservice_server/Dockerfile container/thesis_listings_apigateway_timeservice_multitierapi/server/thesis_listings_apigateway_timeservice_server/ 
