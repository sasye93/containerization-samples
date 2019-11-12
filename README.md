# containerization-samples
Samples for the ScalaLoci Containerization Extension. This should work out of the box. Make sure you have the containerize.jar in /lib, sbt should automatically set it up. If it doesn't work, you might need to manually add the plugin and the macros as library, see thesis, appendix/installation.

## Structure

### ./Evaluation
The results of the evaluation (chapter 8 in thesis).

### ./src
Contains the thesis' listings and sample code.

#### package thesis/listings
* **APIGateway**: Corresponds to thesis' listing02. The gateway sample code is in ./gateway/api, the service code in ./timeservice.
* **Interfaces**: Corresponds to thesis' listing01.

#### package thesis/samples
Contains usage examples.

* **./common**: Contains commonly used code (db).
* **./eval**: Contains the code of the thesis' evaluation (chapter 8 there). This is a heavily altered and extended version of the Pipeline example. PipelineTrad is the pure ScalaLoci way, Pipeline the containerized version.
* **./examples**: Contains some basic simple examples of the extension's usage: _MasterWorker_, _SimpleChat_, _TimeService_.

#### target
Prepared output of running the containerization extension on this project.

### Note

Note that this project comprises a lot of services and different apps. Starting them all together (the whole swarm via _swarm-init.sh_) could outpower your machine, resulting in service shutdowns.
By default, this project is run with the stage compiler option set to run, so the whole swarm is started after the build. You might want to remove this in case.
You can either exclude subprojects from the build so that only a certain sample is build, or you start the subprojects individually by using _stack-XXX.sh_.

-----------
### links
_extension_: https://github.com/sasye93/containerization-extension

_thesis_: https://github.com/sasye93/containerization-thesis
