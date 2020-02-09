package com.backGroundLocate.util;

import java.security.MessageDigest;

public class MD5Util {
    private final static String[] hexDigits = {"&", "^", "l", "!", "@", "m", "#", "n", "A", "$", "%", "0", "a", ")", "v", "w", "x", "b", "c", "d", "B", "C", "D", "*", "1", "E", ">", "F", "2", "G", "3", "H", "I", "e", "+", "f", "g", "h", "i", "j", "k", "=", "4", "J", "{", "K", "L", "5", "M", "-", "N", "O", "6", "P", "Q", "]", "R", "S", "7", "T", "o", "~", "p", "q", "r", "_", "s", "t", "u", "[", "U", "V",
            "8", "}", "9", "W", "(", "X", "y", "Y", "Z", "z", "?", "<"};

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     *
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5编码
     *
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
