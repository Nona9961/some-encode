package com.nona.someEncode.abi.abiType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;


/**
 * @author nona9961
 * @date 2021/9/29 17:39
 */
class UintTest {

    private final String PREFIX = "0x";


    @Test
    void testInit() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Uint.Uint16(0x1FFFF));
    }

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

        Uint.Uint32 uint32 = new Uint.Uint32(0x99882);
        String s3 = uint32.abiHex();
        Assertions.assertEquals("0x0000000000000000000000000000000000000000000000000000000000099882", (PREFIX + s3));

        Uint.Uint72 uint72 = new Uint.Uint72(new BigInteger("1111111111111111111111"));
        String s4 = uint72.abiHex();
        System.out.println(s4);

        BigInteger bigInteger = new BigInteger("11111111111111111111111111111111111111111111111111111111111111111111111111111");
        Uint.Uint256 uint256 = new Uint.Uint256(bigInteger);
        System.out.println(Arrays.toString(bigInteger.toByteArray()));
        String s5 = uint256.abiHex();
        System.out.println(s5);
    }

}