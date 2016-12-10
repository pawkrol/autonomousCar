package org.pawkrol.academic.ai.nncar.engine.utils;

import org.lwjgl.glfw.GLFW;
import org.pawkrol.academic.ai.nncar.engine.WindowCreator;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

/**
 * Created by Pawel on 2016-12-09.
 */
public class ScreenGrabber {

    private int scale;
    private float dt;
    private float elapsedTime;

    private int iterator;

    public ScreenGrabber(int scale, float dt) {
        this.scale = scale;
        this.dt = dt;
    }

    public void init(){
        iterator = 0;
    }

    public void invoke(float interval, int key){
        elapsedTime += interval;

        if (elapsedTime >= dt * 10){
            makeScreenshot(iterator + "_" + parseKey(key) + ".png");
            iterator++;

            elapsedTime = 0;
        }
    }

    private void makeScreenshot(String name){
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        ByteBuffer pixels = ByteBuffer.allocateDirect(WindowCreator.WIDTH * WindowCreator.HEIGHT * 3);

        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glReadPixels(0, 0, WindowCreator.WIDTH, WindowCreator.HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, pixels);

        Image.encodePNG(pixels, WindowCreator.WIDTH, WindowCreator.HEIGHT, scale, name);
    }

    private String parseKey(int key){
        switch (key){
            case GLFW_KEY_W: return "FRONT";
            case GLFW_KEY_S: return "BACK";
            case GLFW_KEY_D: return "RIGHT";
            case GLFW_KEY_A: return "LEFT";
        }

        return "";
    }
}
