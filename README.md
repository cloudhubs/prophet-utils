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

## ProphetUtilsTest Setup
* Running Unit tests requires that you clone this repo to get the microservices (https://github.com/cloudhubs/tms)
* create a file called **application-dev.properties** in src/test/resources/
* In src/test/resources/application-dev.properties change the `user.rootPath` property to the path to **tms** repo
    * Example:
    * user.rootPath=/Users/austinblanchard/Documents/CSI_43C9/microservices/tms/
* Then add the `user.umsPath`, `user.qmsPath`, `user.cmsPath`, `user.emsPath` properties to each microservice
  * Example:
  * user.umsPath=ums/
  * user.qmsPath=qms/
  * user.cmsPath=cms/
  * user.emsPath=ems/
* This will properly point the JUnit tests with the proper microservices