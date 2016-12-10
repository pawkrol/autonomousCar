package org.pawkrol.academic.ai.nncar.engine.render;

import org.pawkrol.academic.ai.nncar.engine.WindowCreator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * Created by Pawel on 2016-12-07.
 */
public class FrameBuffer {

    private int width;
    private int height;

    private int buffer;
    private int texture;
    private int depth;

    public FrameBuffer(int width, int height){
        this.width = width;
        this.height = height;

        buffer = create();
        texture = createTextureAttachment(width, height);
        depth = createDepthBufferAttachment(width, height);
        unbind();
    }

    public void bind(){
        bind(buffer, width, height);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WindowCreator.WIDTH, WindowCreator.HEIGHT);
    }

    public int getTexture(){
        return texture;
    }

    private void bind(int frameBuffer, int width, int height){
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
    }

    private int create(){
        int frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        return frameBuffer;
    }

    private int createTextureAttachment(int width, int height){
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, texture, 0);

        return texture;
    }

    private int createDepthBufferAttachment(int width, int height){
        int depthBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width,
                height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_RENDERBUFFER, depthBuffer);

        return depthBuffer;
    }

}
