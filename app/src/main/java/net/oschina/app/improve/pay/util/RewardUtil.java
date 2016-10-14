package net.oschina.app.improve.pay.util;

import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import java.security.SecureRandom;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by thanatos on 16/10/12.
 */

public class RewardUtil {

    public static final String REWARD_SECURITY_KEY = "19328_etanod_cso_4_yek_sed_a_si_sihT";


    public static String encrypt(String data, String key){
        return byte2hex(encrypt(data.getBytes(), key.getBytes()));
    }

    /**
     * 加密函数
     * @param data 加密数据
     * @param key  密钥
     * @return 返回加密后的数据
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String data, String key){
        return decrypt(data.getBytes(), key.getBytes());
    }

    /**
     * 解密函数
     * @param data 解密数据
     * @param key 密钥
     * @return 返回解密后的数据
     */
    public static String decrypt(byte[] data, byte[] key) {
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 二行制转字符串
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    public static String sign(List<Pair<String, String>> pairs){
        String sign = "";
        for (Pair<String, String> pair : pairs){
            if (TextUtils.isEmpty(pair.second)) continue;
            sign += "&" + pair.first + "=" + pair.second;
        }
        sign = sign.substring(1, sign.length() - 1);
        Log.d("oschina", "params: " + sign);
        return encrypt(sign, REWARD_SECURITY_KEY);
    }

}
