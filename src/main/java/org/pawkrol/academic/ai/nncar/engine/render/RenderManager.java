package org.pawkrol.academic.ai.nncar.engine.render;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.pawkrol.academic.ai.nncar.engine.render.loaders.OBJLoader;
import org.pawkrol.academic.ai.nncar.engine.render.renderables.Mesh;
import org.pawkrol.academic.ai.nncar.engine.utils.Image;
import org.pawkrol.academic.ai.nncar.engine.render.math.Matrices;
import org.pawkrol.academic.ai.nncar.engine.render.renderables.RenderObject;
import org.pawkrol.academic.ai.nncar.engine.render.shaders.DefaultShader;
import org.pawkrol.academic.ai.nncar.engine.utils.ScreenGrabber;
import org.pawkrol.academic.ai.nncar.engine.utils.input.KeyboardListener;
import org.pawkrol.academic.ai.nncar.engine.utils.input.MouseListener;

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

    int lastKey = GLFW_KEY_W;

    private FrameBuffer frameBuffer;
    private DefaultShader defaultShader;
    private Camera camera;

    private ScreenGrabber grabber;

    private List<RenderObject> renderObjects;
    private RenderObject carObject;

    public void init(long windowHandle, KeyboardListener keyboardListener){
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        this.keyboardListener = keyboardListener;

        camera = new Camera(new MouseListener(windowHandle));

        frameBuffer = new FrameBuffer(1280, 720);

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

        keyboardListener.registerOnReleaseAction(this::keyReleaseListener);

        grabber = new ScreenGrabber(40, 1f);
        grabber.init();
    }

    public void update(float interval){
        camera.update();
        checkMove(interval);
//        grabScreen(interval);
    }

    public void render(){
        frameBuffer.bind();
        scene();
        frameBuffer.unbind();

        scene();
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

    private void scene(){
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

    private void checkMove(float interval){
        if (keyboardListener.isPressed(GLFW_KEY_W)){
            camera.move(Camera.MoveDir.FRONT, interval);
        } else if (keyboardListener.isPressed(GLFW_KEY_S)){
            camera.move(Camera.MoveDir.BACK, interval);
        }

        if (keyboardListener.isPressed(GLFW_KEY_A)){
            camera.move(Camera.MoveDir.RIGHT, interval);
        } else if (keyboardListener.isPressed(GLFW_KEY_D)){
            camera.move(Camera.MoveDir.LEFT, interval);
        }
    }

    private void grabScreen(float interval){
        if (keyboardListener.isPressed(GLFW_KEY_W)){
            lastKey = GLFW_KEY_W;
        } else if (keyboardListener.isPressed(GLFW_KEY_S)){
            lastKey = GLFW_KEY_S;
        }

        if (keyboardListener.isPressed(GLFW_KEY_A)){
            lastKey = GLFW_KEY_A;
        } else if (keyboardListener.isPressed(GLFW_KEY_D)){
            lastKey = GLFW_KEY_D;
        }

        grabber.invoke(interval, lastKey);
    }

    private RenderObject createCarObject(){
        Mesh carMesh = OBJLoader.load("objects/random.obj");
        carMesh.setTexture(TextureLoader.loadTexture("textures/arrow.png", Image.Type.TYPE_NOALPHA));
        return new RenderObject(
                carMesh,
                new Vector3f(0, 0, 0),
                new Vector3f(.2f, .2f, .2f),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }

    private RenderObject createRoadObject(){
        Mesh carMesh = OBJLoader.load("objects/road.obj");
        carMesh.setTexture(TextureLoader.loadTexture("textures/road.png", Image.Type.TYPE_ALPHA));
        return new RenderObject(
                carMesh,
                new Vector3f(0, 0, 0),
                new Vector3f(1f, 1f, 1f),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }

    private void keyReleaseListener(int key){
        if (key == GLFW_KEY_SPACE){
            camera.switchMode();
        }
    }
}
