package com.nona.someEncode.abi.abiType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nona9961
 * @date 2021/10/8 11:09
 */
class AddressTest {

    private final String realAddress = "0xA439eb632980750e0E6f8552f4B1b93BCB841f14";

    @Test
    void testInit() {
        String address = "fake address";
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Address(address));
    }

    @Test
    void testAddress() {
        Address address = new Address(realAddress);
        String s = address.abiHex();
        Assertions.assertEquals("000000000000000000000000a439eb632980750e0e6f8552f4b1b93bcb841f14", s);
    }

}