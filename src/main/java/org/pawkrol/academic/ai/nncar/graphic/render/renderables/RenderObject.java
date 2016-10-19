package org.pawkrol.academic.ai.nncar.graphic.render.renderables;

import org.joml.AxisAngle4f;
import org.joml.Vector3f;

/**
 * Created by Pawel on 2016-10-19.
 */
public class RenderObject {

    private Mesh mesh;
    private Vector3f position;
    private Vector3f scale;
    private AxisAngle4f rotation;

    public RenderObject(Mesh mesh, Vector3f position, Vector3f scale, AxisAngle4f rotation) {
        this.mesh = mesh;
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public AxisAngle4f getRotation() {
        return rotation;
    }

    public void setRotation(AxisAngle4f rotation) {
        this.rotation = rotation;
    }
}
