package org.pawkrol.academic.ai.nncar.engine.render.shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created by Pawel on 2016-07-01.
 */
public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public ShaderProgram(Path vertexShader, Path fragmentShader){
        compileShaders(vertexShader, fragmentShader);
        getAllUniformLocations();
    }

    public void start(){
        glUseProgram(programID);
    }

    public void stop(){
        glUseProgram(0);
    }

    public void clean(){
        stop();

        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);

        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }

    protected void bindAttribute(int attribute, String variableName){
        glBindAttribLocation(programID, attribute, variableName);
    }

    protected int getUniformLocation(String uniform){
        return glGetUniformLocation(programID, uniform);
    }

    protected void loadVector3f(int location, Vector3f vector){
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadVector2f(int location, Vector2f vector){
        glUniform2f(location, vector.x, vector.y);
    }

    protected void loadMatrix4f(int location, Matrix4f matrix){
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);

        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    protected abstract void getAllUniformLocations();

    protected abstract void bindAttributes();

    private void compileShaders(Path vertexShader, Path fragmentShader){
        try {
            vertexShaderID = loadShader(vertexShader, GL_VERTEX_SHADER);
            fragmentShaderID = loadShader(fragmentShader, GL_FRAGMENT_SHADER);
            createProgram();
            setUpProgram();

        } catch (FragmentException e) {
            e.printStackTrace();
        }
    }

    private void createProgram(){
        programID = glCreateProgram();
    }

    private void setUpProgram(){
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);
    }

    private int loadShader(Path file, int type) throws FragmentException {
        StringBuilder shaderCode = new StringBuilder();

        try {
            Files.lines(file).forEach( s ->
                    shaderCode.append(s).append(System.getProperty("line.separator"))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
            throw new FragmentException(glGetShaderInfoLog(shaderID));
        }

        return shaderID;
    }

}
