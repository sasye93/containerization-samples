FROM loci-loci.containerize-library-base
LABEL com.loci.containerize.module: "thesis.samples.timeservice.MultitierApi"
LABEL com.loci.containerize.description: "-"
WORKDIR /app
COPY ./run.sh ./*.jar ./

ENTRYPOINT ./run.sh 
