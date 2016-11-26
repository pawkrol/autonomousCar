package org.pawkrol.academic.ai.nncar.engine.render;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.pawkrol.academic.ai.nncar.engine.render.loaders.OBJLoader;
import org.pawkrol.academic.ai.nncar.engine.render.renderables.Mesh;
import org.pawkrol.academic.ai.nncar.engine.utils.Image;
import org.pawkrol.academic.ai.nncar.engine.render.math.Matrices;
import org.pawkrol.academic.ai.nncar.engine.render.renderables.RenderObject;
import org.pawkrol.academic.ai.nncar.engine.render.shaders.DefaultShader;
import org.pawkrol.academic.ai.nncar.engine.utils.KeyboardListener;
import org.pawkrol.academic.ai.nncar.engine.utils.MouseListener;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Pawel on 2016-10-19.
 */
public class RenderManager{

    private static final float FOV = (float) Math.toRadians(60.f);
    private static final float NEAR_PLANE = .01f;
    private static final float FAR_PLANE = 1000.f;

    private KeyboardListener keyboardListener;

    private float width;
    private float height;

    private DefaultShader defaultShader;
    private Camera camera;

    private List<RenderObject> renderObjects;
    private RenderObject carObject;

    public void init(long windowHandle, KeyboardListener keyboardListener){
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        this.keyboardListener = keyboardListener;

        camera = new Camera(new MouseListener(windowHandle));

        defaultShader = new DefaultShader();
        defaultShader.start();
        defaultShader.loadProjectionMatrix(
                Matrices.createProjectionMatrix(width, height, FOV, NEAR_PLANE, FAR_PLANE)
        );
        defaultShader.stop();

        carObject = createCarObject();

        renderObjects = new ArrayList<>();
        renderObjects.add(carObject);
        renderObjects.add(createRoadObject());
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

        for (RenderObject ro : renderObjects) {
            defaultShader.loadTransformationMatrix(
                    Matrices.createTransformationMatrix(ro.getPosition(),
                            ro.getRotation(), ro.getScale())
            );
            ro.getMesh().render();
        }

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

    private void checkMove(){
        if (keyboardListener.isPressed(GLFW_KEY_W)){
            camera.move(Camera.MoveDir.FRONT);
        } else if (keyboardListener.isPressed(GLFW_KEY_S)){
            camera.move(Camera.MoveDir.BACK);
        }

        if (keyboardListener.isPressed(GLFW_KEY_A)){
            camera.move(Camera.MoveDir.RIGHT);
        } else if (keyboardListener.isPressed(GLFW_KEY_D)){
            camera.move(Camera.MoveDir.LEFT);
        }
    }

    private RenderObject createCarObject(){
        Mesh carMesh = OBJLoader.load("objects/random.obj");
        carMesh.setTexture(TextureLoader.loadTexture("textures/rust.jpg", Image.Type.TYPE_NOALPHA));
        return new RenderObject(
                carMesh,
                new Vector3f(0, 0, 0),
                new Vector3f(.2f, .2f, .2f),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }

    private RenderObject createRoadObject(){
        Mesh carMesh = OBJLoader.load("objects/road.obj");
        carMesh.setTexture(TextureLoader.loadTexture("textures/road.jpg", Image.Type.TYPE_ALPHA));
        return new RenderObject(
                carMesh,
                new Vector3f(0, 0, 0),
                new Vector3f(1f, 1f, 1f),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }
}
