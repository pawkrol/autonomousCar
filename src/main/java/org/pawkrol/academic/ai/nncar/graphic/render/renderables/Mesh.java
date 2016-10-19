package org.pawkrol.academic.ai.nncar.graphic.render.renderables;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.pawkrol.academic.ai.nncar.graphic.render.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by Pawel on 2016-10-19.
 */
public class Mesh {

    private final int vertexCount;

    private int vaoID;
    private List<Integer> vbos;

    private Texture texture;

    public Mesh(float[] positions, float[] texCoords, float[] normals, int[] indices){
        vertexCount = indices.length;

        vaoID = createVAO();
        vbos = new ArrayList<>();
        vbos.add( bindIndiciesBuffer(indices) );
        vbos.add( storeAttributes(0, 3, positions) );
        vbos.add( storeAttributes(1, 2, texCoords) );
        vbos.add( storeAttributes(2, 3, normals) );
        unbindVAO();
    }

    public void render(){
        if (isTextured()){
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        if (isTextured()){
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    private void clean(){
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vbos.forEach(GL15::glDeleteBuffers);

        texture.clean();

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoID);
    }

    private int createVAO(){
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        return vaoID;
    }

    private void unbindVAO(){
        glBindVertexArray(0);
    }

    private int bindIndiciesBuffer(int[] indices){
        int vboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer intBuffer = storeDataInBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);
        return vboID;
    }

    private int storeAttributes(int attributeNumber, int coordinatesNumber, float[] data){
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer floatBuffer = storeDataInBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, coordinatesNumber, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0); //unbind
        return vboID;
    }

    private FloatBuffer storeDataInBuffer(float[] data){
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
        floatBuffer.put(data);
        floatBuffer.flip();
        return floatBuffer;
    }

    private IntBuffer storeDataInBuffer(int[] data){
        IntBuffer intBuffer = BufferUtils.createIntBuffer(data.length);
        intBuffer.put(data);
        intBuffer.flip();
        return intBuffer;
    }

    public boolean isTextured() {
        return texture != null;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }
}
