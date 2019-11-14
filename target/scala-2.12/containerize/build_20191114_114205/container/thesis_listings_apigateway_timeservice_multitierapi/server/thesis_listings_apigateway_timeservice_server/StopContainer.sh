#!/bin/sh
docker network disconnect timeservice_gateway thesis_listings_apigateway_timeservice_server
               |docker network disconnect thesis_listings_apigateway_timeservice_multitierapi thesis_listings_apigateway_timeservice_serverdocker stop thesis_listings_apigateway_timeservice_server
docker container rm -f thesis_listings_apigateway_timeservice_server