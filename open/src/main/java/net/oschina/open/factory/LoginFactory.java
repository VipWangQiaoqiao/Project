package net.oschina.open.factory;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by fei
 * on 2016/10/17.
 * desc:
 */

public abstract class LoginFactory extends OpenFactory {

    public abstract void toLogin(Context context, Activity activity, int loginType) throws NoSuchMethodException;

    public abstract void toLogin(Context context, Fragment fragment, int loginType) throws NoSuchMethodException;
}
