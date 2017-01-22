package org.pawkrol.academic.ai.nncar.engine.utils;

import com.sun.istack.internal.Nullable;
import org.pawkrol.academic.ai.nncar.engine.WindowCreator;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ExecutorService executorService;

    public ScreenGrabber(int scale, float dt) {
        this.scale = scale;
        this.dt = dt;
    }

    public void init(){
        iterator = 0;
        executorService = Executors.newFixedThreadPool(4);
    }

    public void clean(){
        executorService.shutdown();
    }

    @Nullable
    public ByteBuffer invoke(float interval){
        return invoke(interval, null, false);
    }

    @Nullable
    public ByteBuffer invoke(float interval, int[] keys, boolean save){
        elapsedTime += interval;
        ByteBuffer pixels = null;

        if (elapsedTime >= dt * 10){
            pixels = makeScreenshot();

            if (save) {
                saveScreenshot(pixels, iterator + "_" + parseKeys(keys) + ".png");
                iterator++;
            }

            elapsedTime = 0;
        }

        return pixels;
    }

    public void setDt(float dt) {
        this.dt = dt;
    }

    private ByteBuffer makeScreenshot(){
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        ByteBuffer pixels = ByteBuffer.allocateDirect(WindowCreator.WIDTH * WindowCreator.HEIGHT * 3);

        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glReadPixels(0, 0, WindowCreator.WIDTH, WindowCreator.HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, pixels);

        return pixels;
    }

    private void saveScreenshot(ByteBuffer pixels, String name){
        executorService.execute(() -> {
            java.awt.image.BufferedImage img = Image.encode(pixels, WindowCreator.WIDTH, WindowCreator.HEIGHT, scale);
            Image.save(img, name);
        });
    }

    private String parseKeys(int[] keys){
        String keyString = "";

        for (int i = 0; i < keys.length; i++) {
            switch (keys[i]) {
                case GLFW_KEY_W:
                    keyString += "FRONT";
                    break;
                case GLFW_KEY_S:
                    keyString += "BACK";
                    break;
                case GLFW_KEY_D:
                    keyString += "RIGHT";
                    break;
                case GLFW_KEY_A:
                    keyString += "LEFT";
                    break;
                case -1:
                    keyString += "";
                    break;
            }

            keyString += "_";
        }

        return keyString;
    }
}
