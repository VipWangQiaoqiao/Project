package net.oschina.app.improve.tweet.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Created by JuQiu
 * on 16/7/21.
 */
@SuppressWarnings("WeakerAccess")
public class TweetPublishCache {
    private final static String TAG = TweetPublishCache.class.getName();

    private TweetPublishCache() {

    }

    static String getImageCachePath(Context context, String id) {
        return String.format("%s/TweetPictures/%s", context.getCacheDir().getAbsolutePath(), id);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static String getFileCachePath(Context context, String id) {
        String dir = context.getFilesDir().getAbsolutePath() + "/TweetQueue";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (id != null) {
            return String.format("%s/%s.tweet", dir, id);
        }
        return dir;
    }

    static void removeImages(Context context, String id) {
        String dir = getImageCachePath(context, id);
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            deleteDir(file);
        }
    }

    public static boolean have(Context context, String id) {
        File data = new File(getFileCachePath(context, id));
        return data.exists();
    }

    public static Map<String, TweetPublishModel> list(Context context) {
        File file = new File(getFileCachePath(context, null));
        if (file.isDirectory()) {
            File[] files = file.listFiles();
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean save(Context context, String id, TweetPublishModel model) {
        final String path = getFileCachePath(context, id);
        log("save", path);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(path);
            if (file.exists())
                file.delete();
            file.createNewFile();
            fos = new FileOutputStream(path);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(model);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(oos, fos);
        }
    }

    public static TweetPublishModel get(Context context, String id) {
        if (!have(context, id))
            return null;

        final String path = getFileCachePath(context, id);
        log("get", path);
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(path);
            ois = new ObjectInputStream(fis);
            return (TweetPublishModel) ois.readObject();
        } catch (FileNotFoundException ignored) {
        } catch (InvalidClassException e) {
            e.printStackTrace();
            remove(context, id);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close(ois, fis);
        }
        return null;
    }

    public static boolean remove(Context context, String id) {
        // To clear the images cache
        removeImages(context, id);

        File data = new File(getFileCachePath(context, id));
        log("remove", data.getAbsolutePath());
        return !data.exists() || data.delete();
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String c : children) {
                deleteDir(new File(dir, c));
            }
        }
        log("delete", dir.getAbsolutePath());
        dir.delete();
    }


    static boolean compressImage(final String srcPath, final String savePath, final long maxSize,
                                 final int minQuality, final int maxWidth, final int maxHeight) {
        // build source file
        final File sourceFile = new File(srcPath);
        if (!sourceFile.exists())
            return false;

        // build save file
        final File saveFile = new File(savePath);
        File saveDir = saveFile.getParentFile();
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs())
                return false;
        }

        // if the in file size <= maxSize, we can copy to savePath
        if (sourceFile.length() <= maxSize) {
            return copyFile(sourceFile, saveFile);
        }

        // create new temp file
        final File tempFile = new File(saveDir, "temp.img");
        if (tempFile.exists()) {
            if (!tempFile.delete())
                return false;
        }
        try {
            if (!tempFile.createNewFile())
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);

        // Calculate inSampleSize
        options.inSampleSize = computeSampleSize(options, -1, maxWidth * maxHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // build to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        if (bitmap == null)
            return false;

        log("compressImage", "bitmap: width:" + bitmap.getWidth() + "  height:" + bitmap.getHeight());

        // write to out put file
        BufferedOutputStream outputStream = null;
        try {
            int quality = 100;
            boolean isOk = false;
            for (; ; ) {
                outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                close(outputStream);
                final long outSize = tempFile.length();
                if (outSize <= maxSize) {
                    isOk = true;
                    break;
                }
                if (quality < minQuality)
                    break;
                log("compressImage", " outSize:" + outSize + " maxSize:" + maxSize + " quality:" + quality);
                quality--;
            }

            log("compressImage", " isOk:" + isOk + " quality:" + quality);
        } catch (IOException e) {
            close(outputStream);
            return false;
        } finally {
            bitmap.recycle();
        }

        // End clear the out file data
        if (saveFile.exists()) {
            if (!saveFile.delete())
                return false;
        }
        // Rename to out file
        return tempFile.renameTo(saveFile);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static boolean copyFile(final File srcFile, final File saveFile) {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
            byte[] buffer = new byte[1024 * 16];

            while (inputStream.read(buffer) != -1) {
                outputStream.write(buffer);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(inputStream, outputStream);
        }
        return true;
    }

    private static void close(Closeable... closeables) {
        if (closeables == null || closeables.length == 0)
            return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void log(String action, String msg) {
        Log.e(TAG, String.format("%s:%s", action, msg));
    }
}
