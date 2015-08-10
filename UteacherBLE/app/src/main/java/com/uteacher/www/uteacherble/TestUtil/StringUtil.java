package com.uteacher.www.uteacherble.TestUtil;

/**
 * Created by cartman on 15/6/9.
 */
public class StringUtil {

    public static String byte2String(byte[] data){
        StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%02X ", byteChar));

        return stringBuilder.toString();
    }
}
