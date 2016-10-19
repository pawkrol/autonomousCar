package org.pawkrol.academic.ai.nncar.graphic.render.loaders;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.pawkrol.academic.ai.nncar.graphic.render.renderables.Mesh;
import org.pawkrol.academic.ai.nncar.graphic.utils.PathObtainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel on 2016-07-09.
 */
public class OBJLoader {

    public static Mesh load(String filename){
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        try {
            filename = PathObtainer.getProperPathString(filename);
            File file = new File(filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null){
                String tokens[] = line.split("\\s+");

                switch (tokens[0]){
                    case "v":
                        Vector3f v = new Vector3f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3])
                        );
                        vertices.add(v);
                        break;

                    case "vt":
                        Vector2f vt = new Vector2f(
                                Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2])
                        );
                        textures.add(vt);
                        break;

                    case "vn":
                        Vector3f vn;
//                        if (fakeLight){
//                            vn = new Vector3f(0, 1, 0);
//                        } else {
                            vn = new Vector3f(
                                    Float.parseFloat(tokens[1]),
                                    Float.parseFloat(tokens[2]),
                                    Float.parseFloat(tokens[3])
                            );
//                        }
                        normals.add(vn);
                        break;

                    case "f":
                        Face f = new Face(
                                tokens[1],
                                tokens[2],
                                tokens[3]
                        );
                        faces.add(f);
                        break;

                    default:
                        //may be comment or something
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return reorder(vertices, textures, normals, faces);
    }

    private static Mesh reorder(List<Vector3f> vertices, List<Vector2f> textures,
                           List<Vector3f> normals, List<Face> faces){

        float[] vs = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f v: vertices){
            vs[i * 3] = v.x;
            vs[i * 3 + 1] = v.y;
            vs[i * 3 + 2] = v.z;
            i++;
        }

        float[] ts = new float[vertices.size() * 2];
        float[] ns = new float[vertices.size() * 3];
        List<Integer> indices = new ArrayList<>();

        for (Face f: faces){
            IndexGroup[] indexGroups = f.getFaceGroups();
            for (IndexGroup group: indexGroups){
                processFaces(group, textures, normals, indices, ts, ns);
            }
        }

        int[] is = indices.stream().mapToInt((Integer v) -> v).toArray();

        return new Mesh(vs, ts, ns, is);
    }

    private static void processFaces(IndexGroup group, List<Vector2f> textures, List<Vector3f> normals,
                                     List<Integer> indices, float[] ts, float[] ns){
        int pos = group.pos;
        indices.add(pos);

        if (group.texCoord >= 0){
            Vector2f texCoord = textures.get(group.texCoord);
            ts[pos * 2] = texCoord.x;
            ts[pos * 2 + 1] = 1 - texCoord.y;
        }

        if (group.vecNormal >= 0){
            Vector3f norm = normals.get(group.vecNormal);
            ns[pos * 3] = norm.x;
            ns[pos * 3 + 1] = norm.y;
            ns[pos * 3 + 2] = norm.z;
        }
    }
}
