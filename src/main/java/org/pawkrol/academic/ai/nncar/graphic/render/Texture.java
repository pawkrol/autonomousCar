package org.pawkrol.academic.ai.nncar.graphic.render;

import org.pawkrol.academic.ai.nncar.graphic.utils.Image;

/**
 * Created by Pawel on 2016-10-19.
 */
public class Texture extends Image {

    private final int id;

    public Texture(String source, int id, Type type){
        super(source, type);

        this.id = id;
    }

    public int getId() {
        return id;
    }
}
