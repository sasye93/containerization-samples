#!/bin/sh
cd ../../../../ 
docker build -t thesis_listings_apigateway_timeservice_manipulator -f container/thesis_listings_apigateway_timeservice_multitierapi/manipulator/thesis_listings_apigateway_timeservice_manipulator/Dockerfile container/thesis_listings_apigateway_timeservice_multitierapi/manipulator/thesis_listings_apigateway_timeservice_manipulator/ 
