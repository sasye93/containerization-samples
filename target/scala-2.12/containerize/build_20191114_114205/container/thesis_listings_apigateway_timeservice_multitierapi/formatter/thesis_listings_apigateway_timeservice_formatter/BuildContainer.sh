#!/bin/sh
cd ../../../../ 
docker build -t thesis_listings_apigateway_timeservice_formatter -f container/thesis_listings_apigateway_timeservice_multitierapi/formatter/thesis_listings_apigateway_timeservice_formatter/Dockerfile container/thesis_listings_apigateway_timeservice_multitierapi/formatter/thesis_listings_apigateway_timeservice_formatter/ 
