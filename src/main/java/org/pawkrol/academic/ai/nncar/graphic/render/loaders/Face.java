package org.pawkrol.academic.ai.nncar.graphic.render.loaders;

/**
 * Created by Pawel on 2016-07-09.
 */
public class Face {

    private IndexGroup[] indexGroups;

    public Face(String v1, String v2, String v3){
        indexGroups = new IndexGroup[3];

        indexGroups[0] = parse(v1);
        indexGroups[1] = parse(v2);
        indexGroups[2] = parse(v3);
    }

    public IndexGroup[] getFaceGroups(){
        return indexGroups;
    }

    private IndexGroup parse(String v){
        IndexGroup indexGroup = new IndexGroup();

        String[] tokens = v.split("/");
        int length = tokens.length;

        indexGroup.pos = Integer.parseInt(tokens[0]) - 1;
        if (length > 1){
            indexGroup.texCoord = (!tokens[1].isEmpty() ? (Integer.parseInt(tokens[1]) - 1) : IndexGroup.NO_VAL);
            indexGroup.vecNormal = Integer.parseInt(tokens[2]) - 1;
        }

        return indexGroup;
    }
}
