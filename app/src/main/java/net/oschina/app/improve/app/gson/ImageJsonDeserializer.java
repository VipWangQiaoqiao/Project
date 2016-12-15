package net.oschina.app.improve.app.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.oschina.app.improve.bean.Tweet;

import java.lang.reflect.Type;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class ImageJsonDeserializer implements JsonDeserializer<Tweet.Image> {
    @Override
    public Tweet.Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Tweet.Image image = new Tweet.Image();
        image.setW(100);
        image.setH(100);

        try {
            if (json.isJsonPrimitive()) {
                // Only the ID is available
                final JsonPrimitive primitive = json.getAsJsonPrimitive();
                String thumb = primitive.getAsString();
                image.setThumb(thumb);
                image.setHref(thumb);
            } else if (json.isJsonObject()) {
                // The whole object is available
                final JsonObject jsonObject = json.getAsJsonObject();
                image.setThumb(jsonObject.get("thumb").getAsString());
                image.setHref(jsonObject.get("href").getAsString());
                image.setH(jsonObject.get("h").getAsInt());
                image.setW(jsonObject.get("w").getAsInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
            image.setThumb("http://d.hiphotos.baidu.com/image/pic/item/0eb30f2442a7d93323582d72af4bd11373f0013b.jpg");
            image.setHref("http://img5.duitang.com/uploads/item/201404/27/20140427211305_FJHmU.jpeg");
        }

        return image;
    }
}