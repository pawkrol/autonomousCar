package org.pawkrol.academic.ai.nncar.engine.render;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.pawkrol.academic.ai.nncar.engine.WindowCreator;
import org.pawkrol.academic.ai.nncar.engine.render.loaders.OBJLoader;
import org.pawkrol.academic.ai.nncar.engine.render.renderables.Mesh;
import org.pawkrol.academic.ai.nncar.engine.render.math.Matrices;
import org.pawkrol.academic.ai.nncar.engine.render.renderables.RenderObject;
import org.pawkrol.academic.ai.nncar.engine.render.shaders.DefaultShader;
import org.pawkrol.academic.ai.nncar.engine.utils.Image;
import org.pawkrol.academic.ai.nncar.engine.utils.ScreenGrabber;
import org.pawkrol.academic.ai.nncar.engine.utils.input.KeyboardListener;
import org.pawkrol.academic.ai.nncar.engine.utils.input.MouseListener;
import org.pawkrol.academic.ai.nncar.neurons.network.NetManager;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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

    private NetManager netManager;

    private KeyboardListener keyboardListener;

    private int lastKey[] = {GLFW_KEY_W, -1};

    private FrameBuffer frameBuffer;
    private DefaultShader defaultShader;
    private Camera camera;
    private RenderObject carObject;

    private ScreenGrabber grabber;
    private boolean grabbingMode = false;
    private boolean tutoringMode = false;
    private boolean autonomusMode = false;

    private List<RenderObject> renderObjects;

    public RenderManager(NetManager netManager){
        this.netManager = netManager;
    }

    public void init(long windowHandle, KeyboardListener keyboardListener){
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        this.keyboardListener = keyboardListener;

        camera = new Camera(new MouseListener(windowHandle));

        frameBuffer = new FrameBuffer(WindowCreator.WIDTH, WindowCreator.HEIGHT);

        defaultShader = new DefaultShader();
        defaultShader.start();
        defaultShader.loadProjectionMatrix(
                Matrices.createProjectionMatrix(WindowCreator.WIDTH, WindowCreator.HEIGHT, FOV, NEAR_PLANE, FAR_PLANE)
        );
        defaultShader.stop();

        carObject = createCarObject();

        renderObjects = new ArrayList<>();
        renderObjects.add(carObject);
        renderObjects.add(createRoadObject());

        keyboardListener.registerOnReleaseAction(this::keyReleaseListener);

        if (autonomusMode){
            camera.switchMode();
        }

        grabber = new ScreenGrabber(40, 1f);
        grabber.init();
    }

    public void update(float interval){
        camera.update();
        checkMove(interval);

        if (tutoringMode) {
            grabAndSaveScreenshot(interval);
        }

        if (autonomusMode) {
            ByteBuffer pixels = grabber.invoke(interval);
            if (pixels != null) {
                float[] image = new float[(WindowCreator.WIDTH / 40) * (WindowCreator.HEIGHT / 40)];
                int j = 0;
                for(int x = 0; x < WindowCreator.WIDTH; x += 40) {
                    for (int y = 0; y < WindowCreator.HEIGHT; y += 40) {
                        int i = (x + (WindowCreator.WIDTH * y)) * 3;
                        int r = pixels.get(i) & 0xFF;
                        int g = pixels.get(i + 1) & 0xFF;
                        int b = pixels.get(i + 2) & 0xFF;
                        image[j++] = (((b | g << 8 | r << 16) / 3) & 0xFF) / 255.f ;
                    }
                }
                float[] output = netManager.propagateInput(image).getOutputs();
                processNetworkOutput(output, interval);
            }
        }
    }

    public void render(){
//        if (autonomusMode) {
//            frameBuffer.bind();
//            scene();
//            frameBuffer.unbind();
//        }

        scene();
    }

    public void clean(){
        defaultShader.clean();
        grabber.clean();
    }

    public void setGrabbingMode(boolean grabbingMode) {
        this.grabbingMode = grabbingMode;
    }

    public void setAutonomusMode(boolean autonomusMode) {
        this.autonomusMode = autonomusMode;
        this.grabbingMode = true;
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

    private void processNetworkOutput(float[] output, float interval){
        System.out.println(output[0] + ", " + output[1] + ", " + output[2] + ", " + output[3]);
//        carObject.getPosition().sub(0, 0, .1f);
//        carObject.getPosition().add(0, 0, .1f);
//        carObject.getRotation().set(carObject.getRotation().angle + .01f, 0, 1, 0);
//        carObject.getRotation().set(carObject.getRotation().angle - .01f, 0, 1, 0);
//        if (Math.round(output[0]) == 1){
//            camera.move(Camera.MoveDir.FRONT, interval * 30);
//        }
    }

    private void grabAndSaveScreenshot(float interval){
        lastKey[0] = -1;
        if (keyboardListener.isPressed(GLFW_KEY_W)){
            lastKey[0] = GLFW_KEY_W;
        } else if (keyboardListener.isPressed(GLFW_KEY_S)){
            lastKey[0] = GLFW_KEY_S;
        }

        lastKey[1] = -1;
        if (keyboardListener.isPressed(GLFW_KEY_A)){
            lastKey[1] = GLFW_KEY_A;
        } else if (keyboardListener.isPressed(GLFW_KEY_D)){
            lastKey[1] = GLFW_KEY_D;
        }

        grabber.invoke(interval, lastKey, true);
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
        Mesh roadMesh = OBJLoader.load("objects/road.obj");
        roadMesh.setTexture(TextureLoader.loadTexture("textures/road.png", Image.Type.TYPE_ALPHA));
        return new RenderObject(
                roadMesh,
                new Vector3f(0, 0, 0),
                new Vector3f(1f, 1f, 1f),
                new AxisAngle4f(0, 0, 0, 0)
        );
    }

    private void keyReleaseListener(int key){
        if (key == GLFW_KEY_SPACE){
            camera.switchMode();
            grabber.setDt(0.7f);

            grabbingMode = !grabbingMode;
            tutoringMode = !tutoringMode;
        }
    }
}
