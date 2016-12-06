package net.oschina.app;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class ReadStateHelper {
    private final File file;
    private final Map<String, Long> cache = new HashMap<>();
    private final int maxPoolSize;

    public ReadStateHelper(File file, int maxPoolSize) {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            throw new NullPointerException("file not null.");
        }
        this.maxPoolSize = maxPoolSize;
        this.file = file;
        read();
    }

    public static ReadStateHelper create(Context context, String fileName, int maxPoolSize) {
        File file = new File(context.getDir("read_state", Context.MODE_PRIVATE), fileName + ".json");
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new RuntimeException("can't mkdirs by:" + parent.toString());
            }
            try {
                if (!file.createNewFile())
                    throw new IOException("can't createNewFile by:" + file.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ReadStateHelper(file, maxPoolSize);
    }

    /**
     * 添加已读状态
     *
     * @param key 一般为资讯等Id
     */
    public void put(long key) {
        put(String.valueOf(key));
    }

    /**
     * 添加已读状态
     *
     * @param key 一般为资讯等Id
     */
    public void put(String key) {
        if (cache.size() >= maxPoolSize) {
            clear();
        }
        cache.put(key, System.currentTimeMillis());
        save();
    }

    /**
     * 获取是否为已读
     *
     * @param key 一般为资讯等Id
     * @return True 已读
     */
    public boolean already(long key) {
        return already(String.valueOf(key));
    }

    /**
     * 获取是否为已读
     *
     * @param key 一般为资讯等Id
     * @return True 已读
     */
    public boolean already(String key) {
        return cache.containsKey(key);
    }

    private Map<String, Long> clear() {
        List<Map.Entry<String, Long>> info = new ArrayList<>(cache.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                long et = (o1.getValue() - o2.getValue());
                return et > 0 ? 1 : (et == 0 ? 0 : -1);
            }
        });

        int deleteSize = info.size() / 2;
        for (Map.Entry<String, Long> stringLongEntry : info) {
            // Remove
            cache.remove(stringLongEntry.getKey());
            if (--deleteSize <= 0)
                break;
        }

        return cache;
    }

    private void read() {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Map<String, Long> data = new Gson().fromJson(jsonReader,
                    new TypeToken<Map<String, Long>>() {
                    }.getType());
            if (data != null && data.size() > 0)
                cache.putAll(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
            new Gson().toJson(cache, new TypeToken<Map<String, Long>>() {
            }.getType(), jsonWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String text = read(file.getAbsolutePath());
        Log.e("TAG", text);

        String json = new Gson().toJson(cache);

        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = read(file.getAbsolutePath());
        Log.e("TAG", text);
    }


    public static String read(String filePath) {
        // 读取txt内容为字符串
        StringBuffer txtContent = new StringBuffer();
        // 每次读取的byte数
        byte[] b = new byte[1024];
        InputStream in = null;
        try {
            // 文件输入流
            in = new FileInputStream(filePath);
            int count;
            while ((count = in.read(b)) != -1) {
                // 字符串拼接
                txtContent.append(new String(b, 0, count));
            }
            // 关闭流
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return txtContent.toString();
    }

}
