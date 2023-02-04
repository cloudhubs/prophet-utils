package edu.baylor.ecs.cloudhubs.prophetutils.nativeimage;

public class MicroserviceInfo {

    private final String baseDir;
    private final String basePackage;
    private final String microserviceName;

    private final String microserviceEntities;

    public MicroserviceInfo(String baseDir, String basePackage, String microserviceName, String microserviceEntities) {
        this.baseDir = baseDir;
        this.basePackage = basePackage;
        this.microserviceName = microserviceName;
        this.microserviceEntities = microserviceEntities;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getMicroserviceName() {
        return microserviceName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getMicroserviceEntities() { return microserviceEntities; }
}
