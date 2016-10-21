package org.pawkrol.academic.ai.nncar.engine.render;

import org.joml.Vector3f;
import org.pawkrol.academic.ai.nncar.engine.utils.MouseListener;

/**
 * Created by Pawel on 2016-07-04.
 */
public class Camera {

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f front;
    private Vector3f up;

    private float yaw;
    private float pitch;

    private float pitchLimit;
    private float tiltFactor;
    private float speed;

    private MouseListener mouseListener;

    public enum MoveDir{
        FRONT,
        BACK,
        LEFT,
        RIGHT
    }

    public Camera(MouseListener mouseListener){
        this.mouseListener = mouseListener;

        mouseListener.init();

        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        front = new Vector3f(0, 0, 0);
        up = new Vector3f(0, 1, 0);

        pitchLimit = 1.5f;
        tiltFactor = .8f;
        speed = .1f;
    }

    public void update(){
        mouseListener.update();

        yaw = mouseListener.getHorizontalAngle();
        pitch = mouseListener.getVerticalAngle();

        if (pitch > pitchLimit) {
            pitch = pitchLimit;
            mouseListener.setVerticalAngle(pitch);
        }

        if (pitch < -pitchLimit) {
            pitch = -pitchLimit;
            mouseListener.setVerticalAngle(pitch);
        }

        yaw *= tiltFactor;
        pitch *= tiltFactor;

        front.x = (float) (Math.cos(pitch) * Math.sin(yaw));
        front.y = (float) (Math.sin(pitch));
        front.z = (float) (Math.cos(pitch) * Math.cos(yaw));
        front.normalize();
    }

    public void move(MoveDir dir){
        if (dir == MoveDir.FRONT){
            position.add(front.x * speed, front.y * speed, front.z * speed); //front.y => 0.f -> for game walk
        } else if (dir == MoveDir.BACK){
            position.sub(front.x * speed, front.y * speed, front.z * speed);
        }

        if (dir == MoveDir.RIGHT){
            position.sub(new Vector3f(front).cross(up).normalize().mul(speed));
        } else if (dir == MoveDir.LEFT){
            position.add(new Vector3f(front).cross(up).normalize().mul(speed));
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getFront() {
        return front;
    }

    public Vector3f getUp() {
        return up;
    }

    public Vector3f getDirection(){
        return new Vector3f(position.x + front.x, position.y + front.y, position.z + front.z);
    }
}
