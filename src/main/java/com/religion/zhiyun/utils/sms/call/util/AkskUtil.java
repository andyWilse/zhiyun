package com.religion.zhiyun.utils.sms.call.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AkskUtil {
    public AkskUtil() {
    }

    public static boolean strIsNullOrEmpty(String s) {
        return s == null || s.trim().length() < 1;
    }

    public static String buildWsseHeader(String appKey, String appSecret) {
        if (!strIsNullOrEmpty(appKey) && !strIsNullOrEmpty(appSecret)) {
            String created = null;
            String nonce = null;
            String passwordDigestBase64Str = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                created = sdf.format(new Date());
                nonce = UUID.randomUUID().toString().replace("-", "");

               /* byte[] passwordDigest = DigestUtils.sha256(appSecret+nonce + created);
                String hexDigest = Hex.encodeHexString(passwordDigest);
                passwordDigestBase64Str = Base64.getEncoder().encodeToString(hexDigest.getBytes());*/
/*
                String message = nonce + created + "<req><orderId>91303183862452</orderId><notifyUrl></notifyUrl><refundTime>2013-03-22 16:25:26</refundTime><refundFee>24.5</refundFee><refundNo>14252414245</refundNo></req>";
                Mac hmacSha256 = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
                hmacSha256.init(secret_key);
                byte[] bytes = hmacSha256.doFinal(message.getBytes());
                String passwordDigestBase64Strtt = byteArrayToHexString(bytes);*/

                String message = nonce+created;
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
                passwordDigestBase64Str = Base64.encodeBase64String(bytes);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }


            return String.format("UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"", appKey, passwordDigestBase64Str, nonce, created);
        } else {
            return null;
        }
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    /**
     * HmacSHA256算法,返回的结果始终是32位
     * @param key 加密的键，可以是任何数据
     * @param content 待加密的内容
     * @return 加密后的内容
     * @throws Exception
     */
    public static byte[] hmacSHA256(byte[] key,byte[] content) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(key, 0, key.length, "HmacSHA256"));
        byte[] hmacSha256Bytes = hmacSha256.doFinal(content);
        return hmacSha256Bytes;
    }
}
