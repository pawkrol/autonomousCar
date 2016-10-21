package org.pawkrol.academic.ai.nncar.engine.utils;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Pawel on 2016-10-19.
 */
public class MouseListener {

    private float horizontalAngle;
    private float verticalAngle;

    private Vector2d prevPos;
    private Vector2d currPos;

    private GLFWCursorPosCallback cursorPosCallback; //must stay, otherwise reference would be lost thanks to GC
    private GLFWCursorEnterCallback cursorEnterCallback;

    private long windowHandle;
    private boolean inWindow = false;

    public MouseListener(long windowHandle){
        this.windowHandle = windowHandle;

        prevPos = new Vector2d(0, 0);
        currPos = new Vector2d(0, 0);
    }

    public void init(){
        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(windowHandle, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                currPos.x = xpos;
                currPos.y = ypos;
            }
        });

        glfwSetCursorEnterCallback(windowHandle, cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                inWindow = entered;
            }
        });
    }

    public void update(){
        horizontalAngle += Math.toRadians(0.1f * (prevPos.x - currPos.x));
        verticalAngle += Math.toRadians(0.1f * (prevPos.y - currPos.y));

        prevPos.x = currPos.x;
        prevPos.y = currPos.y;
    }

    public float getVerticalAngle() {
        return verticalAngle;
    }

    public void setVerticalAngle(float verticalAngle) {
        this.verticalAngle = verticalAngle;
    }

    public float getHorizontalAngle() {
        return horizontalAngle;
    }

    public void setHorizontalAngle(float horizontalAngle) {
        this.horizontalAngle = horizontalAngle;
    }
}
