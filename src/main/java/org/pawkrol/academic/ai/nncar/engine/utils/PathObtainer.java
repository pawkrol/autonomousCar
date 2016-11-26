package org.pawkrol.academic.ai.nncar.engine.utils;

import com.sun.istack.internal.NotNull;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Pawel on 2016-07-03.
 */
public class PathObtainer {

    public static Path getProperPath(@NotNull String res){
        URL url = PathObtainer.class.getClassLoader().getResource(res);
        if (url != null){
            return Paths.get(url.getPath()/*.replaceFirst("/", "")*/);
        } else {
            return null;
        }
    }

    public static String getProperPathString(@NotNull String res){
        URL url = PathObtainer.class.getClassLoader().getResource(res);
        if (url != null){
            return url.getPath()/*.replaceFirst("/", "")*/;
        } else {
            return null;
        }
    }

}
