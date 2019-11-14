FROM loci-loci.containerize-library-base
LABEL com.loci.containerize.module: "thesis.listings.APIGateway.gateway.api.ConsumerApi"
LABEL com.loci.containerize.description: "No description available for this service."
WORKDIR /app
COPY ./run.sh ./*.jar ./
EXPOSE 8080 8081
ENTRYPOINT ./run.sh 
