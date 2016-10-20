package net.oschina.open.factory;

import android.app.Activity;
import android.content.Context;

import net.oschina.open.bean.Share;


/**
 * Created by fei
 * on 2016/10/15.
 * desc:
 */

public abstract class ShareFactory<T> extends OpenFactory {

    public static final int SCENE_SESSION = 1;
    public static final int SCENE_CIRCLE = 0;

    public abstract ShareFactory<T> toShare(Context context, Activity activity, int shareType, Share share);
}
