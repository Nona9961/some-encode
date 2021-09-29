package com.nona.someEncode.abi.abiType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author hu mingcheng
 * @date 2021/9/29 17:39
 */
class UintTest {

    private final String PREFIX = "0x";

    @Test
    void testUint() {
        Uint.Uint8 uint8 = new Uint.Uint8((byte) 1);
        String s = uint8.abiHex();
        Assertions.assertEquals("0x0000000000000000000000000000000000000000000000000000000000000001", (PREFIX + s));

        Uint.Uint16 uint16 = new Uint.Uint16(69);
        String s1 = uint16.abiHex();
        Assertions.assertEquals("0x0000000000000000000000000000000000000000000000000000000000000045", (PREFIX + s1));

        Uint.Uint24 uint24 = new Uint.Uint24(0xFFFF6);
        String s2 = uint24.abiHex();
        Assertions.assertEquals("0x00000000000000000000000000000000000000000000000000000000000ffff6", (PREFIX + s2));

    }

}