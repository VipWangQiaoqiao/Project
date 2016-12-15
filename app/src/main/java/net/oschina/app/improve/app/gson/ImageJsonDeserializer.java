package net.oschina.app.improve.app.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.util.TLog;

import java.lang.reflect.Type;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class ImageJsonDeserializer implements JsonDeserializer<Tweet.Image> {
    @Override
    public Tweet.Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            Tweet.Image image = new Tweet.Image();
            if (json.isJsonPrimitive()) {
                // Only the ID is available
                final JsonPrimitive primitive = json.getAsJsonPrimitive();
                String thumb = primitive.getAsString();
                image.setThumb(thumb);
                image.setHref(thumb);
                image.setW(100);
                image.setH(100);
            } else if (json.isJsonObject()) {
                // The whole object is available
                final JsonObject jsonObject = json.getAsJsonObject();
                image.setThumb(jsonObject.get("thumb").getAsString());
                image.setHref(jsonObject.get("href").getAsString());
                image.setH(jsonObject.get("h").getAsInt());
                image.setW(jsonObject.get("w").getAsInt());
            }
            return image;
        } catch (Exception e) {
            TLog.error("ImageJsonDeserializer-deserialize-error:" + json.toString());
            return null;
        }
    }
}