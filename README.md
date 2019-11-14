# containerization-samples
Samples for the ScalaLoci Containerization Extension. This should work out of the box. Make sure you have the containerize.jar in /lib, and your build.sbt correctly, sbt should automatically set it up. See https://github.com/sasye93/containerization-extension on how to do so and for the prerequisites. In short, make sure you have bash support on your machine and run sbt (v1.3.0 tested, adjust _project/build.properties_ for another version) inside the project dir to install dependencies.
By default, images are pushed to scalalocicontainerize:thesis @ DockerHub.

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

Useful commands to connect to a running container (not service, only to containers):
* docker attach <id> => connects std/stdout of the console to the container.
* docker exec -it <id> bash => opens a new bash inside the container.
* docker logs <id> => shows the stdout output produced by the container.

For everything else, see thesis.

For troubleshooting, see respective chapter in the thesis, and:
- Sometimes, Docker seems to have an issue with dangling networks, images, containers, etc.: They don't exist anymore, but are still listed. This prevents the extension or Docker itself from re-creating it. Then, for example the extension throws an error like "network xxx not found.". Under _docker network ls_, the network is listed, but if you try to remove it with _docker network rm <id>_, you also get something like "network xxx doesn't exist.". In this case, restarting the docker machine (_docker-machine restart_) or totally resetting Docker to its default state might help in cleaning up.
-----------
### links
_extension_: https://github.com/sasye93/containerization-extension

_thesis_: https://github.com/sasye93/containerization-thesis
