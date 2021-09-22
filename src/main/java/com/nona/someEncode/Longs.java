package com.nona.someEncode;

/**
 * @author nona9961
 * @date 2021/9/22 10:04
 */
public class Longs {

    public static final long ALL_TRUE_LONG = 0xFFL;

    public static long longOf8Byte(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
        return (b0 & ALL_TRUE_LONG) << 56
                | (b1 & ALL_TRUE_LONG) << 48
                | (b2 & ALL_TRUE_LONG) << 40
                | (b3 & ALL_TRUE_LONG) << 32
                | (b4 & ALL_TRUE_LONG) << 24
                | (b5 & ALL_TRUE_LONG) << 16
                | (b6 & ALL_TRUE_LONG) << 8
                | (b7 & ALL_TRUE_LONG);
    }

}
