# containerization-samples
Samples for the ScalaLoci Containerization Extension

## Structure

### Evaluation
The results of the evaluation (chapter 8 in thesis).

### src
Contains the thesis' listings and sample code.

#### thesis/listings
* **APIGateway**: Corresponds to thesis' listing02. The gateway sample code is in ./gateway/api, the service code in ./timeservice
* **Interfaces**: Corresponds to thesis' listing01.

### thesis/samples
Contains usage examples.

* **common**: Contains commonly used code (db).
* **eval**: Contains the code of the thesis' evaluation (chapter 8 there). This is a heavily altered and extended version of the Pipeline example. PipelineTrad is the pure ScalaLoci way, Pipeline the containerized version.
* **examples**: Contains some basic simple examples of the extension's usage: _MasterWorker_, _SimpleChat_, _TimeService_.

-----------
### links
_extension_: https://github.com/sasye93/containerization-extension

_thesis_: https://github.com/sasye93/containerization-thesis
