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

## Prophet-Utils Test Setup
* Running Unit tests requires that you clone this repo to get the microservices (https://github.com/cloudhubs/tms)
* create a directory called 'resources' in src/test/
* create a file called **test-config.properties** in src/test/resources/
* In src/test/resources/test-config.properties change the `tms.rootPath` property to the path to **tms** repo
    * Example:
      * user.rootPath=/Users/austinblanchard/Documents/CSI_43C9/microservices/tms/
* Then add the `tms.umsPath`, `tms.qmsPath`, `tms.cmsPath`, `tms.emsPath` properties to each microservice
  * Example:
    * tms.umsPath=ums/
    * tms.qmsPath=qms/
    * tms.cmsPath=cms/
    * tms.emsPath=ems/
  * This will properly point the JUnit tests to the proper microservices
