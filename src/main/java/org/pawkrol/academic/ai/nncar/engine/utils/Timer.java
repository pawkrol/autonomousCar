package org.pawkrol.academic.ai.nncar.engine.utils;

/**
 * Created by Pawel on 2016-12-09.
 */
public class Timer {

    private double lastLoopTime;

    public void init(){
        lastLoopTime = getTime();
    }

    //milis
    public double getTime(){
        return System.nanoTime() / 100_000_000.0;
    }

    public float getElapsedTime(){
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;

        return elapsedTime;
    }

    public double getLastLoopTime(){
        return lastLoopTime;
    }
}
