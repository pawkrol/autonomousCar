package org.pawkrol.academic.ai.nncar.graphic.render;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.pawkrol.academic.ai.nncar.graphic.render.loaders.OBJLoader;
import org.pawkrol.academic.ai.nncar.graphic.render.math.Matrices;
import org.pawkrol.academic.ai.nncar.graphic.render.renderables.Mesh;
import org.pawkrol.academic.ai.nncar.graphic.render.renderables.RenderObject;
import org.pawkrol.academic.ai.nncar.graphic.render.shaders.DefaultShader;
import org.pawkrol.academic.ai.nncar.graphic.utils.Image;
import org.pawkrol.academic.ai.nncar.graphic.utils.MouseListener;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Pawel on 2016-10-19.
 */
public class RenderManager implements GLFWKeyCallbackI{

    private static final float FOV = (float) Math.toRadians(60.f);
    private static final float NEAR_PLANE = .01f;
    private static final float FAR_PLANE = 1000.f;

    private float width;
    private float height;

    private DefaultShader defaultShader;
    private Camera camera;

    private boolean[] keys = new boolean[1024];

    private RenderObject renderObject;

    public void init(long windowHandle){
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        camera = new Camera(new MouseListener(windowHandle));

        defaultShader = new DefaultShader();
        defaultShader.start();
        defaultShader.loadProjectionMatrix(
                Matrices.createProjectionMatrix(width, height, FOV, NEAR_PLANE, FAR_PLANE)
        );
        defaultShader.stop();

        Mesh mesh = OBJLoader.load("models/bucket.obj");
        mesh.setTexture(TextureLoader.loadTexture("textures/rust.jpg", Image.Type.TYPE_NOALPHA));
        renderObject = new RenderObject(
                mesh,
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }

    public void update(){
        camera.update();
        checkMove();
    }

    public void render(){
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(.3f, .7f, .8f, 0.f);

        defaultShader.start();
        defaultShader.loadViewMatrix(Matrices.crateViewMatrix(camera));
        defaultShader.loadTransformationMatrix(
                Matrices.createTransformationMatrix(renderObject.getPosition(),
                        renderObject.getRotation(), renderObject.getScale())
        );
        renderObject.getMesh().render();
        defaultShader.stop();
    }

    public void clean(){
        defaultShader.clean();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
            glfwSetWindowShouldClose(window, true);
        }

        if (action == GLFW_PRESS){
            keys[key] = true;
        } else if (action == GLFW_RELEASE){
            keys[key] = false;
        }
    }

    private void checkMove(){
        if (keys[GLFW_KEY_W]){
            camera.move(Camera.MoveDir.FRONT);
        } else if (keys[GLFW_KEY_S]){
            camera.move(Camera.MoveDir.BACK);
        }

        if (keys[GLFW_KEY_A]){
            camera.move(Camera.MoveDir.RIGHT);
        } else if (keys[GLFW_KEY_D]){
            camera.move(Camera.MoveDir.LEFT);
        }
    }
}
