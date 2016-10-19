package org.pawkrol.academic.ai.nncar.graphic.render;

import org.pawkrol.academic.ai.nncar.graphic.utils.Image;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL21.GL_SRGB;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * Created by Pawel on 2016-07-03.
 */
public class TextureLoader {

    private static List<Texture> textures = new ArrayList<>();

    public static Texture loadTexture(String source, Image.Type type){
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        Texture texture = new Texture(source, textureID, type);
        uploadTextureToGPU(texture);
        textures.add(texture);

        return texture;
    }

    public static void clean(){
        for (Texture t: textures){
            t.clean();
            glDeleteTextures(t.getId());
        }
    }

    private static void uploadTextureToGPU(Texture texture){
        if (texture.getComp() == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB, texture.getWidth(),
                    texture.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, texture.getImage());
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB_ALPHA, texture.getWidth(),
                    texture.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.getImage());
        }

        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -.4f);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

}
