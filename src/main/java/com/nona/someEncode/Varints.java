package com.nona.someEncode;

import java.util.Arrays;

/**
 * @author nona9961
 * @date 2021/8/26 20:01
 */
public class Varints {

    private static final int GROUP_UNIT_SIZE = 7;
    private static final int VARINTS_SIZE = 7;
    private static final byte BIT7 = 0b01111111;
    private static final byte END = 0b00000000;
    private static final byte CONTINUE = (byte) 0b10000000;
    private static final long FULL_TRUE = 0xFFL;


    // 7*8=56
    public static byte[] encode(byte[] raw) {
        raw = paddingForRawLong(raw);
        byte[] result = new byte[raw.length];
        for (int i = raw.length - Byte.SIZE; i >= 0; i -= Byte.SIZE) {
            long aLong = Longs.longOf8Byte(raw[i], raw[i + 1], raw[i + 2], raw[i + 3], raw[i + 4], raw[i + 5], raw[i + 6], raw[i + 7]);
            fillVarintWithLong(result, raw.length - Byte.SIZE - i, aLong, i == 0);
        }
        return modifyLastZero(result);
    }


    /**
     * 将原数组对齐
     * <p>
     * varints是按照7位一组编码，之后通过long类型进行位运算，要求raw
     * 每7byte一组补齐0至8byte，最后一组没有7byte也要补全到8byte
     *
     * @param raw 原始数组
     * @return 补齐0的数组，并且长度是8的倍数
     */
    private static byte[] paddingForRawLong(byte[] raw) {
        int groupCount = groupCountBy7Bytes(raw);
        byte[] padded = new byte[groupCount * Byte.SIZE];
        int rawLength = raw.length;
        int padLength = padded.length;
        for (int i = 1, j = 1; i <= rawLength; i++, j++) {
            if (j % 8 == 0) {
                padded[padLength - j] = 0;
                j++;
            }
            padded[padLength - j] = raw[rawLength - i];
        }
        return padded;
    }

    /**
     * 进行varints编码，将原binary array按照7bit分组，倒置序；
     * 每组最高位除了最后一个都是1，最后一组是0
     *
     * @param result 编码好的result，未去除末尾的0
     * @param i      填充第i个位置
     * @param l      long
     * @param isLast 是否到最后8byte
     */
    private static void fillVarintWithLong(byte[] result, int i, long l, boolean isLast) {
        for (int j = 0; j <= VARINTS_SIZE; j++) {
            result[i * Byte.SIZE + j] =
                    (byte) (isLast && j == VARINTS_SIZE ? END : CONTINUE | (((l & FULL_TRUE) >> (j * VARINTS_SIZE)) & BIT7));
        }
    }

    /**
     * 上述填充方式可能会多好几个-128（0b10000000）和最后的一个0
     * 将这些内容去掉 ，并且将去掉的数组最后一byte的最高位置为0
     *
     * @param result varintRaw数组
     * @return 真正变换的varint数组
     */
    private static byte[] modifyLastZero(byte[] result) {
        if (result[result.length - 1] != 0) {
            return result;
        }
        int index = 0;
        for (int i = result.length - 2; i > 0; i--) {
            if (result[i] == CONTINUE) {
                continue;
            }
            index = i;
            break;
        }
        result[index] = (byte) (BIT7 & result[index]);
        return Arrays.copyOfRange(result, 0, index + 1);
    }


    /**
     * 将byte数组根据7byte长度分组，返回组数
     *
     * @param bytes 原数组
     * @return 组数
     */
    private static int groupCountBy7Bytes(byte[] bytes) {
        int totalLength = bytes.length;
        int groupCount = totalLength / GROUP_UNIT_SIZE;
        if (totalLength % GROUP_UNIT_SIZE != 0) {
            groupCount++;
        }
        return groupCount;
    }

}
