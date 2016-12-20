package net.oschina.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import net.oschina.app.util.TDevice;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public final class Setting {
    public static final String KEY_VERSION_CODE = "versionCode";
    public static final String KEY_APP_UNIQUE_ID = "appUniqueID";

    public static SharedPreferences getSettingPreferences(Context context) {
        return context.getSharedPreferences(Setting.class.getName(), Context.MODE_PRIVATE);
    }

    public static boolean checkIsNewVersion(Context context) {
        int saveVersionCode = getSaveVersionCode(context);
        int currentVersionCode = TDevice.getVersionCode();
        if (saveVersionCode < currentVersionCode) {
            updateSaveVersionCode(context, currentVersionCode);
            return true;
        }
        return false;
    }

    public static int getSaveVersionCode(Context context) {
        SharedPreferences sp = getSettingPreferences(context);
        return sp.getInt(KEY_VERSION_CODE, 0);
    }

    private static int updateSaveVersionCode(Context context, int version) {
        SharedPreferences sp = getSettingPreferences(context);
        SharedPreferences.Editor editor = sp.edit().putInt(KEY_VERSION_CODE, version);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        return version;
    }
}
