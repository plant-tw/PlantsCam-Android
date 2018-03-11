package com.piccollage.util;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by prada on 10/5/15.
 */
public class FileUtils {

    private static final int IO_BUFFER_SIZE = 1024 * 4;

    public static boolean isAnimatedFile(String path) {
        return TextUtils.isEmpty(path) ? false : path.endsWith(".gif") || path.endsWith(".mp4");
    }

    /**
     * In order to speed up the examination, it refers to the extension name only.
     */
    public static boolean isGifFile(String url) {
        return TextUtils.isEmpty(url) ? false : url.endsWith(".gif");
    }

    public static String getCaptureType(@NonNull String path) {
        if (path.endsWith(".gif")) {
            return "Gif";
        }
        if (path.endsWith(".mp4")) {
            return "Video";
        }
        return "Image";
    }

    public static String appendPath(final String originalUrl, String path) {
        if (TextUtils.isEmpty(originalUrl) || TextUtils.isEmpty(path)) {
            return originalUrl;
        }
        if (originalUrl.endsWith("/") && path.startsWith("/")) {
            return originalUrl + path.substring(1);
        } else if (!originalUrl.endsWith("/") && !path.startsWith("/")) {
            return originalUrl + "/" + path;
        }
        return originalUrl + path;
    }

    /**
     * An observer detecting the screenshots.
     *
     * @param context Activity context.
     * @param looper  Activity looper.
     */
    public static ScreenshotsObserver createScreenshotsObserver(final Context context,
                                                                final Looper looper,
                                                                final IScreenshotsListener listener) {
        return new ScreenshotsObserver(context, looper, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////

    public interface IScreenshotsListener {

        void onScreenshotsDetected(String path);
    }

    public static class ScreenshotsObserver {

        private static final String SCREENSHOTS_REGEX = String.format(
            ".*[Ss]creenshots?%s.*", File.separator);
        private static final long THROTTLE_DURATION = 1000;

        final Handler mObservingHandler;
        final Handler mReceivingHandler;

        final private Context mContext;
        //        final FileObserver mFileObserver;
        final ContentObserver mContentObserver;
        final IScreenshotsListener mListener;

        String mLatestScreenshotsPath;
        long mLatestScreenshotsTime;

        private ScreenshotsObserver(final Context context,
                                    final Looper receiveLooper,
                                    final IScreenshotsListener listener) {
            mContext = context;

            // Will dispatch the update in the message-queue of the given
            // looper.
            mReceivingHandler = new Handler(receiveLooper);

//            final String screenshotsPath = Environment.getExternalStorageDirectory() +
//                                           File.separator + Environment.DIRECTORY_PICTURES +
//                                           File.separator + "Screenshots" + File.separator;
//            mFileObserver = new FileObserver(screenshotsPath, FileObserver.CLOSE_WRITE) {
//                @Override
//                public void onEvent(final int event,
//                                    final String path) {
//                    if (TextUtils.isEmpty(path)) return;
//
//                    Log.d("xyz", "Detect screenshots in the FileObserver.");
//
//                    if (path.matches(SCREENSHOTS_REGEX)) {
//                        mReceivingHandler.post(mOnScreenshotsDetected);
//                    }
//                }
//            };
            // Create a worker thread.
            final HandlerThread workerThread = new HandlerThread(
                ScreenshotsObserver.class.getSimpleName());
            workerThread.start();
            mObservingHandler = new Handler(workerThread.getLooper());
            // Create the observer running on the worker thread.
            mContentObserver = new ContentObserver(mObservingHandler) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);

                    Cursor cursor = null;
                    try {
                        cursor = mContext
                            .getContentResolver()
                            .query(
                                // URI
                                uri,
                                // Projection
                                new String[]{
                                    MediaStore.Images.Media.DISPLAY_NAME,
                                    MediaStore.Images.Media.DATA
                                },
                                // Selection
                                null,
                                // Selection arguments
                                null,
                                // Selection order
                                MediaStore.Images.Media.DATE_ADDED + " DESC");
                        if (cursor != null && cursor.moveToFirst()) {
                            final String fileName = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                            final String path = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            // TODO: apply filter on the file name to ensure it's screen shot event

                            final long current = System.currentTimeMillis();
                            if (path.matches(SCREENSHOTS_REGEX) &&
                                (current - mLatestScreenshotsTime) > THROTTLE_DURATION) {
                                Log.d("xyz", "Detect screenshot filename=" + fileName +
                                    ", path=" + path +
                                    ", in ContentObserver");
                                mLatestScreenshotsTime = System.currentTimeMillis();
                                mLatestScreenshotsPath = path;
                                mReceivingHandler.post(mOnScreenshotsDetected);
                            }
                        }
                    } catch (IllegalStateException ignored) {
                        // DO NOTHING.
                    } finally {
                        if (cursor != null)  {
                            cursor.close();
                        }
                    }
                }
            };
            mListener = listener;
        }

        public void startWatching() {
//            mFileObserver.startWatching();
            mContext.getContentResolver()
                .registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
                    mContentObserver);
        }

        public void stopWatching() {
//            mFileObserver.stopWatching();
            mContext.getContentResolver()
                .unregisterContentObserver(mContentObserver);
        }

        public void recycle() {
            // Recycle the looper.
            mObservingHandler.getLooper().quit();
        }

        /**
         * Runnable for dispatching the screenshots detected;
         */
        private Runnable mOnScreenshotsDetected = new Runnable() {
            @Override
            public void run() {
                if (mListener == null) return;

                mListener.onScreenshotsDetected(mLatestScreenshotsPath);
            }
        };
    }

    public static byte[] read(File file) throws IOException {
        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                throw new IOException(
                    "EOF reached while trying to read the whole file");
            }
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException ignored) {}
        }
        return buffer;
    }

    public static void write(File file, byte[] data) throws IOException {
        if(!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

    // File handle
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                if (files == null) {
                    // No files in the directory, let's delete the directory.
                    return file.delete();
                }

                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        deleteFile(subFile);
                    } else {
                        subFile.delete();
                    }
                }
            }
            // else, `file` is a File
        }
        return file.delete();
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.exists() && !destFile.createNewFile())
            throw new IOException("Could not create " + destFile.toString());

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static class DecryptedFileException extends IOException {
        public DecryptedFileException(Exception e) {
            super(e);
        }
    }

    public static void encrypt(String key, File in, File out) throws Throwable {
        CipherInputStream cis = new CipherInputStream(new FileInputStream(in),
            getCipher(key, Cipher.ENCRYPT_MODE));
        doCopy(cis, new FileOutputStream(out));
    }

    public static CipherInputStream decrypt(String key, File in) throws DecryptedFileException {
        try {
            return new CipherInputStream(new FileInputStream(in), getCipher(key, Cipher.DECRYPT_MODE));
        } catch (FileNotFoundException | GeneralSecurityException e) {
            throw new DecryptedFileException(e);
        }
    }

    private static Cipher getCipher(String key, int mode) throws GeneralSecurityException {
        SecretKey desKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes()));
        Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE
        cipher.init(mode, desKey);
        return cipher;
    }

    private static void doCopy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
        os.close();
        is.close();
    }

    public static void closeStream(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception ignored) {}
    }

    public static byte[] toBytes(InputStream is){
        byte[] result = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            copy(is, baos);
            result = baos.toByteArray();
        } catch (IOException ignored){}

        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, 0);
    }

    public static void copy(InputStream in, OutputStream out, int max) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while((read = in.read(b)) != -1){
            out.write(b, 0, read);
        }
    }
}