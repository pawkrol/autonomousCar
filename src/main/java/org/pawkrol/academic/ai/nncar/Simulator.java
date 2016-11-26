package org.pawkrol.academic.ai.nncar;

import org.pawkrol.academic.ai.nncar.engine.WindowCreator;
import org.pawkrol.academic.ai.nncar.engine.render.RenderManager;

/**
 * Created by pawkrol on 11/14/16.
 */
public class Simulator {

    public void init(){
        RenderManager renderManager = new RenderManager();
        new WindowCreator("Autonomous car simulator", 1280, 720, renderManager).create();
    }

}
