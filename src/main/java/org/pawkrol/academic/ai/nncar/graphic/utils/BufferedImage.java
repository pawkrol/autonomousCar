package org.pawkrol.academic.ai.nncar.graphic.utils;

import java.nio.ByteBuffer;

/**
 * Created by Pawel on 2016-07-19.
 */
public class BufferedImage {

    private Image image;

    public BufferedImage(Image image){
        this.image = image;
    }

    public byte[] getPixel(int x, int y){
        if (! (x <= image.getWidth() && x >= 0
                && y <= image.getHeight() && y >= 0)){
            throw new ArrayIndexOutOfBoundsException();
        }

        byte[] pixel;
        ByteBuffer buffer = image.getImage();

        if (image.getComp() == 3) {
            int dis = 3;
            int pos = x * dis + y * image.getWidth() * dis;

            pixel = new byte[dis];
            pixel[0] = buffer.get(pos);
            pixel[1] = buffer.get(pos + 1);
            pixel[2] = buffer.get(pos + 2);
        } else { //assuming there is no other option
            int dis = 4;
            int pos = x * dis + y * image.getWidth() * dis;

            pixel = new byte[dis];
            pixel[0] = buffer.get(pos);
            pixel[1] = buffer.get(pos + 1);
            pixel[2] = buffer.get(pos + 2);
            pixel[3] = buffer.get(pos + 3);
        }

        return pixel;
    }

    public float[] getFloatPixel(int x, int y){
        byte[] pixel = getPixel(x, y);
        int comp = pixel.length;

        float[] floatPixel = new float[comp];
        for (int i = 0; i < floatPixel.length; i++){
            floatPixel[i] = (pixel[i] & 0xFF) / 255.f;
        }

        return floatPixel;
    }

    public int getIntRGBPixel(int x, int y){
        byte[] pixel = getPixel(x, y);

        int intPixel = 0;
        intPixel |= (pixel[0] & 0xFF) << 16;
        intPixel |= (pixel[1] & 0xFF) << 8;
        intPixel |= (pixel[2] & 0xFF);

//        if (comp == 4) {
//            intPixel |= (pixel[3] & 0xFF);
//        }

        return intPixel;
    }

    public Image getImage() {
        return image;
    }
}
