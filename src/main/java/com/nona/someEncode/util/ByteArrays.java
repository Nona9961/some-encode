package com.nona.someEncode.util;

import cn.hutool.core.util.ArrayUtil;

/**
 * 字节数组工具类
 *
 * @author nona9961
 * @date 2021/10/8 14:28
 */
public class ByteArrays {
    private ByteArrays() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * 将fillArray倒序填充进original中<br/>
     * eg:{1,2}填充{0,0,0,0}为{0,0,1,2}
     *
     * @param original  待填充数组
     * @param fillArray 填充数组
     */
    public static void fillRevertBytes(byte[] original, byte[] fillArray) {
        if (ArrayUtil.isEmpty(original) || ArrayUtil.isEmpty(fillArray)) {
            return;
        }
        for (int i = fillArray.length - 1, j = original.length - 1; i >= 0; i--, j--) {
            original[j] = fillArray[i];
        }
    }

    /**
     * 将fillArray正序填充进original中<br/>
     *
     * @param original  待填充数组
     * @param fillArray 填充数组
     */
    public static void fillBytes(byte[] original, byte[] fillArray) {
        if (ArrayUtil.isEmpty(original) || ArrayUtil.isEmpty(fillArray)) {
            return;
        }
        System.arraycopy(fillArray, 0, original, 0, fillArray.length);
    }
}
