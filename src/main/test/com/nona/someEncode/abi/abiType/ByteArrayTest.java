package com.nona.someEncode.abi.abiType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author hu mingcheng
 * @date 2021/10/8 11:38
 */
class ByteArrayTest {
    @Test
    void testInit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ByteArray(new byte[33]));
    }

    @Test
    void testAbi() {
        byte[] bytes = {1, 5, 9, 8, 7, 4, 63, 5, 8, 9, 3, 1, 8, 32, 96, 2, 21, 5, 7};
        ByteArray byteArray = new ByteArray(bytes);
        String s = byteArray.abiHex();
        Assertions.assertEquals("000000000000000000000000000105090807043f050809030108206002150507", s);
    }
}