package org.pawkrol.academic.ai.nncar.engine.render.shaders;

import org.joml.Matrix4f;

import java.nio.file.Path;

import static org.pawkrol.academic.ai.nncar.engine.utils.PathObtainer.getProperPath;

/**
 * Created by Pawel on 2016-10-19.
 */
public class DefaultShader extends ShaderProgram{

    private static final Path VERTEX_SHADER = getProperPath("shaders/vertex_shader.vert");
    private static final Path FRAGMENT_SHADER = getProperPath("shaders/fragment_shader.frag");

    private int transformationMatrixLocation;
    private int projectionMatrixLocation;
    private int viewMatrixLocation;

    public DefaultShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix4f(transformationMatrixLocation, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix4f(projectionMatrixLocation, matrix);
    }

    public void loadViewMatrix(Matrix4f matrix){
        super.loadMatrix4f(viewMatrixLocation, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        transformationMatrixLocation = super.getUniformLocation("transformationMatrix");
        projectionMatrixLocation = super.getUniformLocation("projectionMatrix");
        viewMatrixLocation = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "texCoords");
    }
}
