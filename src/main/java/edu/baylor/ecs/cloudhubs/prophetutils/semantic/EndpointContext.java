package edu.baylor.ecs.cloudhubs.prophetutils.semantic;

import java.util.List;

public class EndpointContext {
    private String ms;
    private String msClass;
    private String method;
    private String httpMethod;
    private String returnType;
    private List<String> arguments;

    public String getMs() {
        return ms;
    }

    public void setMs(String ms) {
        this.ms = ms;
    }

    public String getMsClass() {
        return msClass;
    }

    public void setMsClass(String msClass) {
        this.msClass = msClass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
