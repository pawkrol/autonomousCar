package org.pawkrol.academic.ai.nncar.neurons.functions;

/**
 * Created by pawkrol on 10/22/16.
 */
public class SigmoidFunction implements ActivationFunction{

    public float f(float x) {
        return (float)(1 / (1 + Math.exp(-x)));
    }

}
