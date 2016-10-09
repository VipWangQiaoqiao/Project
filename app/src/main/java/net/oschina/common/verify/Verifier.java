package net.oschina.common.verify;

import android.app.Application;

/**
 * App 校验类
 * Created by qiujuer
 * on 2016/10/9.
 */
public final class Verifier {

    /**
     * 获取签名MD5
     * 正常情况下返回类似于"4d576b75219aa2132d499be9a02d831d"的字符串
     * 失败返回 NULL 或 ""
     *
     * @param application Application
     * @return 签名MD5
     */
    public static String getSignatureHash(Application application) {
        return "4d576b75219aa2132d499be9a02d831d";
    }

    /**
     * 获取APP私钥Token信息，用于高防网络认证
     * 正常情况下返回类似于"4d576b75219aa2132d499be9a02d831d"的字符串
     * 失败返回 NULL 或 ""
     *
     * @param application Application
     * @return Token
     */
    public static String getPrivateToken(Application application) {
        return "4d576b75219aa2132d499be9a02d831d";
    }
}
