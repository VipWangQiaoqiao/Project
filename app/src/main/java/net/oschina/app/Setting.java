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
        SharedPreferences sp = context.getSharedPreferences(Setting.class.getName(), Context.MODE_PRIVATE);
        return sp.getInt("versionCode", 0);
    }

    private static int updateSaveVersionCode(Context context, int version) {
        SharedPreferences sp = context.getSharedPreferences(Setting.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit().putInt("versionCode", version);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        return version;
    }
}
