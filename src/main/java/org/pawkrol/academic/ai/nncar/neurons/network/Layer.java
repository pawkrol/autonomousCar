package org.pawkrol.academic.ai.nncar.neurons.network;

import org.pawkrol.academic.ai.nncar.neurons.functions.ActivationFunction;

public class Layer {

    private PerceptronContainer[] perceptronContainers;
    private int size;
    private float[] outputs;

    public Layer(int size){
        this.size = size;
        this.perceptronContainers = new PerceptronContainer[size];
    }

    public void createPerceptrons(int numberOfInputs, float eta, ActivationFunction function){
        for (int i = 0; i < size; i++){
            perceptronContainers[i]= new PerceptronContainer(
                    new Perceptron(numberOfInputs, function),
                    eta
            );
        }
    }

    public void passInput(float[] input){
        int i = 0;
        outputs = new float[size];

        for (PerceptronContainer pc : perceptronContainers){
            Perceptron p = pc.getPerceptron();
            outputs[i++] = p.calculateOutput(input).getOutput();
        }
    }

    public void updateWeights(float[] input){
        float dw, db, etaTimesDelta;
        float[] weights;
        float bias;

        for (PerceptronContainer pc : perceptronContainers){
            etaTimesDelta = pc.getEta() * pc.getDelta();

            weights = pc.getPerceptron().getWeights().clone();
            bias = pc.getPerceptron().getBias();

            for (int i = 0; i < weights.length; i++) {
                dw = etaTimesDelta * input[i];
                weights[i] += dw;

                pc.getPerceptron().setWeights(weights);
            }

            db = etaTimesDelta;
            bias += db;

            pc.getPerceptron().setBias(bias);

        }
    }

    public float getError(){
        float error = 0;
        for (PerceptronContainer pc : perceptronContainers){
            error += pc.getDelta();
        }

        return error;
    }

    public float[] getOutputs(){
        return outputs;
    }

    public int getSize() {
        return size;
    }

    public PerceptronContainer[] getPerceptronContainers() {
        return perceptronContainers;
    }
}
