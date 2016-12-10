package org.pawkrol.academic.ai.nncar.neurons.network;

import org.pawkrol.academic.ai.nncar.neurons.functions.ActivationFunction;

import java.util.Random;

public class Perceptron {

    private int numberOfInputs;

    private ActivationFunction function;

    private float[] weights;
    private float bias;
    private float output;

    public Perceptron(int numberOfInputs, ActivationFunction function){
        this.numberOfInputs = numberOfInputs;
        this.function = function;

        Random random = new Random();
        initWeights(random);
        initBias(random);

      //  System.out.println(Arrays.toString(weights) + " + " + bias);
    }

    public Perceptron calculateOutput(float[] inputs){
        float sum = 0;

        for (int i = 0; i < numberOfInputs; i++){
            sum += inputs[i] * weights[i];
        }
        sum += bias;

        output = function.f(sum);

        return this;
    }

    public float getOutput() {
        return output;
    }

    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }

    private void initWeights(Random random){
        weights = new float[numberOfInputs];

        for (int i = 0; i < weights.length; i++){
            weights[i] = random.nextFloat() * 0.3f;
        }
    }

    private void initBias(Random random){
        bias = random.nextFloat() * 0.3f;
    }
}
