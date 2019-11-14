FROM loci-loci.containerize-library-base
LABEL com.loci.containerize.module: "thesis.samples.chat.MultitierApi"
LABEL com.loci.containerize.description: "No description available for this service."
WORKDIR /app
COPY ./run.sh ./*.jar ./

COPY ./preRunSpecific.sh /app/preRunSpecific.sh 
RUN /app/preRunSpecific.sh 
ENTRYPOINT ./run.sh 
