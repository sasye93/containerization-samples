#!/bin/sh
docker rm --volumes -f thesis_listings_apigateway_timeservice_formatter
               |docker volume create thesis_listings_apigateway_timeservice_formatter|docker run -id  --name thesis_listings_apigateway_timeservice_formatter --network=thesis_listings_apigateway_timeservice_formatter --volume thesis_listings_apigateway_timeservice_formatter:/data --cap-add=NET_ADMIN --cap-add=NET_RAW --sysctl net.ipv4.conf.eth0.route_localnet=1 -t thesis_listings_apigateway_timeservice_formatter
                |docker network connect --alias thesis_listings_apigateway_timeservice_formatter thesis_listings_apigateway_timeservice_multitierapi thesis_listings_apigateway_timeservice_formatterdocker container inspect -f "Container 'thesis_listings_apigateway_timeservice_formatter' connected to timeservice_gateway and thesis_listings_apigateway_timeservice_multitierapi with ip={{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}." thesis_listings_apigateway_timeservice_formatter
