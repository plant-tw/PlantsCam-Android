package plantscam.android.prada.lab.imageutils;

/**
 * Created by prada on 01/05/2018.
 */

public class ImageUtils {
    static {
        System.loadLibrary("imageutils");
    }

    public static native void yuv2rgb(int[] argb, byte[] yuv, int width, int height);
    public static native void yuv2rgb(int[] argb, byte[] yuv, int x, int y, int width, int height);
}
