package org.pawkrol.academic.ai.nncar.graphic.render.loaders;

/**
 * Created by Pawel on 2016-07-09.
 */
public class IndexGroup {
    public static final int NO_VAL = -1;

    public int pos;
    public int texCoord;
    public int vecNormal;

    IndexGroup(){
        pos = NO_VAL;
        texCoord = NO_VAL;
        vecNormal = NO_VAL;
    }
}
