package org.pawkrol.academic.ai.nncar.engine.utils.input;

import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * Created by Pawel on 2016-10-20.
 */
public class KeyboardListener implements GLFWKeyCallbackI {

    private List<OnKeyReleaseAction> releaseActions = new ArrayList<>();
    private boolean[] keys = new boolean[1024];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
            glfwSetWindowShouldClose(window, true);
        }

        if (action == GLFW_PRESS){
            keys[key] = true;
        } else if (action == GLFW_RELEASE){
            keys[key] = false;

            for (OnKeyReleaseAction r : releaseActions){
                r.onRelease(key);
            }
        }
    }

    public boolean isPressed(int key){
        return keys[key];
    }

    public void registerOnReleaseAction(OnKeyReleaseAction action){
        releaseActions.add(action);
    }
}
