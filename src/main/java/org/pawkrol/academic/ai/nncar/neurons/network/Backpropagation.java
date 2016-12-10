package org.pawkrol.academic.ai.nncar.neurons.network;

public class Backpropagation {

    private final NetManager manager;

    public Backpropagation(NetManager manager) {
        this.manager = manager;
    }

    public void learn(float[][] inputValues, float[][] outputValues, double minError, int maxInterations){
        float error;

        for (int j = 0; j <= maxInterations; j++) {
            error = 0;

            for (int i = 0; i < inputValues.length; i++) {
                manager.propagateInput(inputValues[i]);
                manager.calculateOutputDeltas(outputValues[i]);
                manager.calculateHiddenDeltas();
                manager.updateWeights(inputValues[i]);

                error += manager.getError() * manager.getError();
            }

            if (error <= minError) break;
        }

    }

}
