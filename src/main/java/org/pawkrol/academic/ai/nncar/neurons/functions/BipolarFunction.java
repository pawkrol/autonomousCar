package org.pawkrol.academic.ai.nncar.neurons.functions;

/**
 * Created by pawkrol on 10/22/16.
 */
public class BipolarFunction implements ActivationFunction{

    public float f(float x) {
        if (x >= 0)
            return 1;
        else
            return -1;
    }
}
