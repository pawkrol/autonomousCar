package org.pawkrol.academic.ai.nncar;

import org.pawkrol.academic.ai.nncar.engine.WindowCreator;
import org.pawkrol.academic.ai.nncar.engine.render.RenderManager;
import org.pawkrol.academic.ai.nncar.engine.utils.Image;
import org.pawkrol.academic.ai.nncar.neurons.functions.SigmoidFunction;
import org.pawkrol.academic.ai.nncar.neurons.network.Backpropagation;
import org.pawkrol.academic.ai.nncar.neurons.network.NetManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by pawkrol on 11/14/16.
 */
public class Simulator {

    private static final String OUTPUT_CATALOGUE = "output/";
    private static final String SCREENS_CATALOGUE = "screens/";
    private static final String WEIGHTS_CATALOGUE = "weights/";
    private static final String WEIGHTS_MATRIX_FILE = "weights_20H_005.txt";

    public void init(){
        checkAndMakeOutputCatalogs();

        NetManager netManager = null;
        Backpropagation backpropagation = null;
        RenderManager renderManager = null;

        if (isWeightMatrixAvaliable()){
            System.out.println("Loading weighs into net");
            netManager = new NetManager(OUTPUT_CATALOGUE + WEIGHTS_CATALOGUE + WEIGHTS_MATRIX_FILE,
                                            .6f, new SigmoidFunction());
            System.out.println("Loading weighs finished");

            renderManager = new RenderManager(netManager);

            renderManager.setAutonomusMode(true);
        } else {
            if (areScreensAvaliable()){
                netManager = new NetManager(576, new int[]{20, 4}, .6f, new SigmoidFunction());
                backpropagation = new Backpropagation(netManager);

                renderManager = new RenderManager(netManager);

                System.out.println("Obtaining learning data");
                float[][] input = createLearningInputData();
                float[][] output = createLearningOutputData();

                System.out.println("Learning");
                backpropagation.learn(input, output, 0.000001, 100000);
                System.out.println("Learned");

                System.out.println("Saving weights");
                try {
                    netManager.saveWeights(OUTPUT_CATALOGUE + WEIGHTS_CATALOGUE + WEIGHTS_MATRIX_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                renderManager.setAutonomusMode(true);
            } else {
                renderManager = new RenderManager(null);
                System.out.println("Switching to normal mode - press <space> to create learning images");
            }
        }

        new WindowCreator("Autonomous car simulator", 1280, 720, renderManager).create();
    }

    private boolean isWeightMatrixAvaliable(){
        File weights = new File(OUTPUT_CATALOGUE + WEIGHTS_CATALOGUE + WEIGHTS_MATRIX_FILE);
        return weights.exists();
    }

    private boolean areScreensAvaliable(){
        File screens = new File(OUTPUT_CATALOGUE + SCREENS_CATALOGUE);
        if (screens.isDirectory()){
            String[] files = screens.list();

            if (files != null && files.length > 0){
                return true;
            }
        }

        return false;
    }

    private void checkAndMakeOutputCatalogs(){
        File outputs = new File(OUTPUT_CATALOGUE);
        if (!outputs.exists()){
            outputs.mkdir();
        }

        File screens = new File(OUTPUT_CATALOGUE + SCREENS_CATALOGUE);
        if (!screens.exists() && outputs.exists()){
            screens.mkdir();
        }

        File weights = new File(OUTPUT_CATALOGUE + WEIGHTS_CATALOGUE);
        if (!weights.exists() && outputs.exists()){
            weights.mkdir();
        }
    }

    private float[][] createLearningInputData(){
        File screens = new File(OUTPUT_CATALOGUE + SCREENS_CATALOGUE);

        String[] files = screens.list();
        if (files == null) return null;

        float[][] inputData = new float[files.length][];
        for (int i = 0; i < files.length; i++){
            Image image = new Image();
            image.load(OUTPUT_CATALOGUE + SCREENS_CATALOGUE + files[i], Image.Type.TYPE_GREY);

            inputData[i] = new float[image.getImage().capacity()];
            for (int j = 0; j < image.getImage().capacity(); j++){
                inputData[i][j] = (image.getImage().get(j) & 0xFF) / 255.f;
            }
        }

        return inputData;
    }

    private float[][] createLearningOutputData(){
        File screens = new File(OUTPUT_CATALOGUE + SCREENS_CATALOGUE);

        String[] files = screens.list();
        if (files == null) return null;

        HashMap<String, Integer[]> dirs = new HashMap<>();
        dirs.put("FRONT", new Integer[]{1, 0});
        dirs.put("BACK", new Integer[]{0, 1});
        dirs.put("LEFT", new Integer[]{1, 0});
        dirs.put("RIGHT", new Integer[]{0, 1});
        dirs.put("", new Integer[]{0, 0});

        float[][] outputData = new float[files.length][];
        for (int i = 0; i < files.length; i++){
            String[] tokens = files[i].split("_");
            outputData[i] = new float[4];
            outputData[i][0] = dirs.get(tokens[1])[0];
            outputData[i][1] = dirs.get(tokens[1])[1];
            outputData[i][2] = dirs.get(tokens[2])[0];
            outputData[i][3] = dirs.get(tokens[2])[1];
        }

        return outputData;
    }
}
