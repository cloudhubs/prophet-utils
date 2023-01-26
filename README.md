# Prophet Utils

Library for source code analysis of JAVA enterprise applications. It detects semantic code clones and inconsistencies,
creates context map, bounded contexts and communication diagrams.

## 0.0.8
* created unified interface ProphetUtilsFacade

## Available Scripts

```bash
mvn clean install
mvn package
```

## ProphetUtilsTest
* Running Unit tests requires that you clone this repo to get the microservices (https://github.com/cloudhubs/tms)
* In src/test/resources/application-dev.properties change the root path and the microservices path to **tms** repo
* This will properly the JUnit tests with valid microservices