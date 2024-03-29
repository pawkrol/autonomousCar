package org.pawkrol.academic.ai.nncar.neurons.network;

import org.pawkrol.academic.ai.nncar.neurons.functions.ActivationFunction;

import java.io.*;

public class NetManager {

    private ActivationFunction function;
    private Layer[] layers;
    private int hiddenLayersNumber;
    private int netInputsNumber;
    private float eta;

    public NetManager(String file, float eta, ActivationFunction function){
        this.eta = eta;
        this.function = function;

        try {
            loadNetwork(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void loadNetwork(String location) throws IOException {
        File weightsFile = new File(location);

        FileReader fileReader = new FileReader(weightsFile);
        BufferedReader reader = new BufferedReader(fileReader);

        //read inputs number
        String line = reader.readLine();
        netInputsNumber = Integer.parseInt(line);

        //read layers amount
        line = reader.readLine();
        int layersNumber = Integer.parseInt(line);
        hiddenLayersNumber = layersNumber - 1;

        //create layers array
        layers = new Layer[layersNumber];

        int perceptronInputs = netInputsNumber;

        for (int i = 0; i < layersNumber; i++){
            //read perceptrons in layer amount
            line = reader.readLine();
            int perceptronsNumber = Integer.parseInt(line);

            //create layer itself
            layers[i] = new Layer(perceptronsNumber);
            layers[i].createPerceptrons(perceptronInputs, eta, function);
            perceptronInputs = layers[i].getSize();

            //get created perceptrons containers
            PerceptronContainer[] containers = layers[i].getPerceptronContainers();

            for (int j = 0; j < perceptronsNumber; j++){
                //read weights and split them
                line = reader.readLine();
                String weightsString[] = line.split("\\s+");

                //parse weights
                float[] weights = new float[weightsString.length - 1];
                for (int w = 0; w < weightsString.length - 1; w++){
                    weights[w] = Float.parseFloat(weightsString[w]);
                }

                containers[j].getPerceptron().setWeights(weights);

                float bias = Float.parseFloat(weightsString[weightsString.length - 1]);
                containers[j].getPerceptron().setBias(bias);

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

        //write network input number
        bufferedWriter.write("" + netInputsNumber);
        bufferedWriter.newLine();

        //write layers amount
        bufferedWriter.write("" + (hiddenLayersNumber + 1));
        bufferedWriter.newLine();

        for (Layer l : layers){
            PerceptronContainer[] containers = l.getPerceptronContainers();

            //write perceptrons in layer amount
            bufferedWriter.write("" + (containers.length));
            bufferedWriter.newLine();

            String line;
            for (PerceptronContainer c : containers){
                line = "";

                float[] ws = c.getPerceptron().getWeights();
                for (float w : ws){
                    line += w + " ";
                }
                line += c.getPerceptron().getBias();

                //write line with perceptron weights separated by space
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
