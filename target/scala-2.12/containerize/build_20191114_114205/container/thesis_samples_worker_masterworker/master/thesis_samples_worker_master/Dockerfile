FROM loci-loci.containerize-library-base
LABEL com.loci.containerize.module: "thesis.samples.worker.MasterWorker"
LABEL com.loci.containerize.description: "No description available for this service."
WORKDIR /app
COPY ./run.sh ./*.jar ./

ENTRYPOINT ./run.sh 
