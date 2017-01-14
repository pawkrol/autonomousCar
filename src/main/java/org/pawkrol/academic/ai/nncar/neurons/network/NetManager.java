package org.pawkrol.academic.ai.nncar.neurons.network;

import org.pawkrol.academic.ai.nncar.neurons.functions.ActivationFunction;

import java.io.*;

public class NetManager {

    private ActivationFunction function;
    private Layer[] layers;
    private int hiddenLayersNumber;
    private int netInputsNumber;
    private float eta;

    // structure ex. {3, 2, 1} where 3 and 2 are numbers of perceptrons in hidden layers
    // and 1 is a number of perceptrons in output layer
    public NetManager(int netInputsNumber, int[] structure, float eta, ActivationFunction function){
        this.hiddenLayersNumber = structure.length - 1;
        this.netInputsNumber = netInputsNumber;
        this.eta = eta;
        this.function = function;

        createLayers(structure);
        createPerceptrons();
    }

    public NetManager propagateInput(float[] inputValue){
        float[] input = inputValue.clone();

        for (Layer l : layers){
            l.passInput(input);
            input = l.getOutputs().clone();
        }

        return this;
    }

    public Layer getOutputLayer(){
        return layers[layers.length - 1];
    }

    public float[] getOutputs(){
        return getOutputLayer().getOutputs();
    }

    public void calculateOutputDeltas(float[] expectedOutput){
        Layer outputLayer = getOutputLayer();
        PerceptronContainer[] containers = outputLayer.getPerceptronContainers();

        Perceptron p;
        float delta, y;
        for (int i = 0; i < containers.length; i++){
            p = containers[i].getPerceptron();
            y = p.getOutput();

            delta = y * (1 - y) * (expectedOutput[i] - y);

            containers[i].setDelta(delta);
        }
    }

    public void calculateHiddenDeltas(){
        Layer layer, prevLayer = getOutputLayer();
        PerceptronContainer[] containers, prevContainer;
        float delta, y, sum = 0;

        for (int i = layers.length - 2; i >= 0; i--){
            layer = layers[i];
            containers = layer.getPerceptronContainers();
            prevContainer = prevLayer.getPerceptronContainers();

            for (int j = 0; j < containers.length; j++){
                for (PerceptronContainer pc : prevContainer){
                    sum += pc.getDelta() * pc.getPerceptron().getWeights()[j];
                }

                y = containers[i].getPerceptron().getOutput();
                delta = y * (1 - y) * sum;
                containers[i].setDelta(delta);
            }

            prevLayer = layer;
        }
    }

    public void updateWeights(float[] inputValue){
        float[] input = inputValue.clone();

        for (Layer l : layers){
            l.updateWeights(input);
            input = l.getOutputs().clone();
        }
    }

    public void loadWeights(File weights) throws IOException {
        FileReader fileReader = new FileReader(weights);
        BufferedReader reader = new BufferedReader(fileReader);

        String line;
        for (Layer l : layers){
            float[][] ws = new float[l.getSize()][];

            for (int i = 0; i < l.getSize(); i++){
                line = reader.readLine();
                String tokens[] = line.split("\\s+");

                ws[i] = new float[tokens.length];
                for (int j = 0; j < tokens.length; j++) {
                    ws[i][j] = Float.parseFloat(tokens[j]);
                }

                l.setWeights(ws);
            }

        }
    }

    public void saveWeights(String location) throws IOException {
        File weightsFile = new File(location);
        if (weightsFile.exists()){
            weightsFile.delete();
        }

        FileWriter writer = new FileWriter(weightsFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for (Layer l : layers){
            PerceptronContainer[] containers = l.getPerceptronContainers();
            for (PerceptronContainer c : containers){
                float[] ws = c.getPerceptron().getWeights();

                String line = "";
                for (float w : ws){
                    line += w + " ";
                }

                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }

        bufferedWriter.flush();
    }

    public float getError(){
        return getOutputLayer().getError();
    }

    private void createLayers(int[] structure){
        layers = new Layer[hiddenLayersNumber + 1]; //plus output layer

        for (int i = 0; i < layers.length; i++){
            layers[i] = new Layer(structure[i]);
        }
    }

    private void createPerceptrons(){
        int perceptronInputs = netInputsNumber;

        for (Layer layer : layers) {
            layer.createPerceptrons(perceptronInputs, eta, function);
            perceptronInputs = layer.getSize();
        }
    }
}
