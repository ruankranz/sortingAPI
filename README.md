# Vert.x Demo

This project is a very simple Vert.x 3 application and contains some explanation on how this application is built
and tested.

## Project structure
    .
    ├── src                     # Source files
        ├── main                # Java classes and verticle
        ├── test                # Unit and automated tests
    ├── build_and_run.sh        # Build and run project on local host    
    ├── Dockerfile              # Self explanatory
    ├── docker-compose.yml      # Docker but cooler
    └── README.md               # You are here

## Building

You build the project using (assuming you already have Maven installed):

```
mvn clean package
```

To build and run locally just execute the `build_and_run.sh` shell script

```
sh build_and_run.sh
```
NB: the tests will also be executed when you run this script. If the tests fail, the server won't startup.

## Testing

The application is tested using [vertx-unit](http://vertx.io/docs/vertx-unit/java/).

## Packaging

The application is packaged as a _fat jar_, using the
[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/).

Used to compile the sources
[maven-compiler-plugin](https://maven.apache.org/plugins/maven-compiler-plugin/).


## Code checks
Check for bugs
[Findbugs Maven Plugin](https://maven.apache.org/plugins/findbugs-maven-plugin/).

Check code style
[maven-checkstyle-plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/).

## Running

Once packaged, just launch the _fat jar_ as follows:

```
java -jar target/bubblesort-1.0-SNAPSHOT-fat.jar
```

## Docker (Local)
To build and run the docker image:
```
$ docker build -t demo/krankit-vertx .
$ docker-compose up
```

## ToDo
- Add and improve logging
- Create 2 separate verticles (sender receiver) and cluster the event bus
- Add zookeeper for cluster management
- Deploy with docker stack/swarm