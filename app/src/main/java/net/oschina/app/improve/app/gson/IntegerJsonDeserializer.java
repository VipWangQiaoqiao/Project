package net.oschina.app.improve.app.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.oschina.app.util.TLog;

import java.lang.reflect.Type;

/**
 * Created by qiujuer
 * on 2016/11/22.
 */
public class IntegerJsonDeserializer implements JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return json.getAsInt();
        } catch (Exception e) {
            TLog.log("IntegerJsonDeserializer-deserialize-error:" + (json != null ? json.toString() : ""));
            return 0;
        }
    }
}
