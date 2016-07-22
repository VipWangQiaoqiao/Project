package net.oschina.app.improve.tweet.service;

import android.content.Context;
import android.util.Log;

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
