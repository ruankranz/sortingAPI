#!/usr/bin/env bash
mvn clean package -Djava.util.logging.config.file=resources/logging.properties; java -jar target/sortingapi-1.0-SNAPSHOT-fat.jar