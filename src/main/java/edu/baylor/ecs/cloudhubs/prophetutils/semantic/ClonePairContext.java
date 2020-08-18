package edu.baylor.ecs.cloudhubs.prophetutils.semantic;

public class ClonePairContext {
    private EndpointContext first;
    private EndpointContext second;
    private double sameness;

    public ClonePairContext(EndpointContext first, EndpointContext second, double sameness) {
        this.first = first;
        this.second = second;
        this.sameness = sameness;
    }

    public EndpointContext getFirst() {
        return first;
    }

    public EndpointContext getSecond() {
        return second;
    }

    public double getSameness() {
        return sameness;
    }
}
