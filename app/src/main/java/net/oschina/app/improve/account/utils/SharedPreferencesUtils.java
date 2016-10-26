package net.oschina.app.improve.account.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Created by fei
 * on 2016/10/25.
 * desc:
 */

public class SharedPreferencesUtils {

    /**
     * init editor
     *
     * @param sp sp
     * @return editor
     */
    public static SharedPreferences.Editor getEditor(SharedPreferences sp) {
        return sp.edit();
    }

    /**
     * init sp
     *
     * @param context context
     * @return sp
     */
    public static SharedPreferences createSp(String fileName,Context context) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 官方写法 更加安全,可以参考
     *
     * @param edit editor
     */
    public static void commit(SharedPreferences.Editor edit) {
        //官方安全commit写法,值得学习里面的源代码
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }


}
