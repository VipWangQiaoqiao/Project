package net.oschina.app.api;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;

import java.util.UUID;

class ApiClientHelper {

    /**
     * 获得请求的服务端数据的userAgent
     * 客户端唯一标识
     *
     * @param appContext
     * @return
     */
    static String getUserAgent(AppContext appContext) {
        PackageInfo packageInfo = getPackageInfo(appContext);
        return "OSChina.NET" + '/' + packageInfo.versionName + '_' + packageInfo.versionCode +// app版本信息
                "/Android" +// 手机系统平台
                "/" + android.os.Build.VERSION.RELEASE +// 手机系统版本
                "/" + android.os.Build.MODEL + // 手机型号
                "/" + getAppId(appContext);
    }

    private static String getAppId(AppContext context) {
        String uniqueID = context.getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            context.setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    private static PackageInfo getPackageInfo(AppContext context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }
}
