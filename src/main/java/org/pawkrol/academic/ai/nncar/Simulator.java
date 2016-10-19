package org.pawkrol.academic.ai.nncar;

import org.pawkrol.academic.ai.nncar.graphic.WindowCreator;
import org.pawkrol.academic.ai.nncar.graphic.render.RenderManager;

/**
 * Created by Pawel on 2016-10-18.
 */
public class Simulator {

    public static void main(String[] args){
        RenderManager renderManager = new RenderManager();
        new WindowCreator("Autonomous car simulator", 1280, 720, renderManager).create();
    }

}
