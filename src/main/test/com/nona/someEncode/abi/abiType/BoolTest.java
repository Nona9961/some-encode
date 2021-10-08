package com.nona.someEncode.abi.abiType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nona9961
 * @date 2021/10/8 11:31
 */
class BoolTest {

    @Test
    void testNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Bool(null));
    }

    @Test
    void test() {
        Bool bool = new Bool(true);
        String s = bool.abiHex();
        Assertions.assertEquals("0000000000000000000000000000000000000000000000000000000000000001", s);
    }

}