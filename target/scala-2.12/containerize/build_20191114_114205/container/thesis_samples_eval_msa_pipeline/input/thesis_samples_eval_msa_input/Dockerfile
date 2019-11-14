FROM loci-loci.containerize-library-base
LABEL com.loci.containerize.module: "thesis.samples.eval.msa.Pipeline"
LABEL com.loci.containerize.description: "No description available for this service."
WORKDIR /app
COPY ./run.sh ./*.jar ./
EXPOSE 8424
ENTRYPOINT ./run.sh 
