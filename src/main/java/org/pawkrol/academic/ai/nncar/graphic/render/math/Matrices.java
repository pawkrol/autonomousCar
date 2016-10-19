package org.pawkrol.academic.ai.nncar.graphic.render.math;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.pawkrol.academic.ai.nncar.graphic.render.Camera;

/**
 * Created by Pawel on 2016-07-03.
 */
public class Matrices {

    public static Matrix4f createTransformationMatrix(Vector3f translation, AxisAngle4f rotation, Vector3f scale){
        return new Matrix4f()
                .identity()
                .translate(translation)
                .rotate(rotation)
                .scale(scale);
    }

    public static Matrix4f createProjectionMatrix(float wwidth, float wheight, float FOV, float NEAR_PLANE, float FAR_PLANE){
        float aspectRatio = wwidth / wheight;
        return new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
    }

    public static Matrix4f crateViewMatrix(Camera camera){
        return new Matrix4f().lookAt(
                camera.getPosition(),
                camera.getDirection(),
                camera.getUp()
        );
    }
}
