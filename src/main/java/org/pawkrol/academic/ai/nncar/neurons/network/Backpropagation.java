package org.pawkrol.academic.ai.nncar.neurons.network;

public class Backpropagation {

    private final NetManager manager;

    private long learnTime;

    public Backpropagation(NetManager manager) {
        this.manager = manager;
    }

    public void learn(float[][] inputValues, float[][] outputValues, double minError, int maxInterations) {
        float error;
        int j;
        learnTime = System.currentTimeMillis();

        for (j = 0; j <= maxInterations; j++) {
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

        learnTime = System.currentTimeMillis() - learnTime;
        System.out.println("Learning time for " + j + " iterations and "
                + inputValues.length + " of learning data: " + learnTime);
    }

}
