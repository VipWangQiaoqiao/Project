package net.oschina.open.factory;

import android.content.Context;


/**
 * Created by fei
 * on 2016/10/15.
 * desc:
 */

public abstract class OpenFactory<T> {

    public abstract T createOpen(Context context, int openType) throws ClassNotFoundException;

    public abstract T addAppKey(String appKey);

    public abstract T addAppId(String appId);

}
