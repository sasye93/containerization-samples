#!/bin/sh
docker network disconnect timeservice_gateway thesis_listings_apigateway_timeservice_formatter
               |docker network disconnect thesis_listings_apigateway_timeservice_multitierapi thesis_listings_apigateway_timeservice_formatterdocker stop thesis_listings_apigateway_timeservice_formatter
docker container rm -f thesis_listings_apigateway_timeservice_formatter