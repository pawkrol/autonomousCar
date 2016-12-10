package org.pawkrol.academic.ai.nncar.engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.pawkrol.academic.ai.nncar.engine.render.RenderManager;
import org.pawkrol.academic.ai.nncar.engine.utils.Timer;
import org.pawkrol.academic.ai.nncar.engine.utils.input.KeyboardListener;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by Pawel on 2016-10-18.
 */
public class WindowCreator {

    private static final float TARGET_FPS = 75;
    private static final float TARGET_UPS = 30;

    public static int WIDTH;
    public static int HEIGHT;

    private final RenderManager renderManager;
    private final KeyboardListener keyboardListener;

    private Timer timer;

    private String title;
    private long window;

    public WindowCreator(String title, int width, int height, RenderManager renderManager){
        WIDTH = width;
        HEIGHT = height;

        keyboardListener = new KeyboardListener();

        this.title = title;
        this.renderManager = renderManager;
        renderManager.setWidth(width);
        renderManager.setHeight(height);

        timer = new Timer();
    }

    public void create(){
        try {
            init();
            loop();

            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            renderManager.clean();

            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }

    }

    private void init(){
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);


        window = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
        if (window == NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, keyboardListener);

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);

        glfwShowWindow(window);

        GL.createCapabilities();

        renderManager.init(window, keyboardListener);

        timer.init();
    }

    private void loop(){
        float elapsedTime;
        float accumulator = 0;
        float interval = 1.f / TARGET_UPS;

        while (!glfwWindowShouldClose(window)){
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            while (accumulator >= interval){
                renderManager.update(interval);
                accumulator -= interval;
            }

            renderManager.render();

            sync();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void sync(){
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;

        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
