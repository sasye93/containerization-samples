#!/bin/sh
cd ../../../../ 
docker build -t thesis_listings_apigateway_gateway_api_apigateway -f container/thesis_listings_apigateway_gateway_api_consumerapi/gateway/thesis_listings_apigateway_gateway_api_apigateway/Dockerfile container/thesis_listings_apigateway_gateway_api_consumerapi/gateway/thesis_listings_apigateway_gateway_api_apigateway/ 
