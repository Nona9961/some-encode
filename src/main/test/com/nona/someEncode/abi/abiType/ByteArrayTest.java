package com.nona.someEncode.abi.abiType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nona9961
 * @date 2021/10/8 11:38
 */
class ByteArrayTest {
    @Test
    void testInit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ByteArray(new byte[33]));
    }

    @Test
    void testAbi() {
        String rawString = "1234567890";
        byte[] rawStringBytes = rawString.getBytes();
        ByteArray byteArray = new ByteArray(rawStringBytes);
        String s = byteArray.abiHex();
        Assertions.assertEquals("3132333435363738393000000000000000000000000000000000000000000000", s);
    }
}