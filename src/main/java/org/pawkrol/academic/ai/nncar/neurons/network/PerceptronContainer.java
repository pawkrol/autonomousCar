package org.pawkrol.academic.ai.nncar.neurons.network;

public class PerceptronContainer{

    private Perceptron perceptron;
    private float delta;
    private float eta;

    PerceptronContainer(Perceptron perceptron, float eta){
        this.perceptron = perceptron;
        this.eta = eta;
    }

    public Perceptron getPerceptron() {
        return perceptron;
    }

    public void setPerceptron(Perceptron perceptron) {
        this.perceptron = perceptron;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public float getEta() {
        return eta;
    }

    public void setEta(float eta) {
        this.eta = eta;
    }
}
