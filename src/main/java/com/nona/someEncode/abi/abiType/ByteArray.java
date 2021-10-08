package com.nona.someEncode.abi.abiType;

import com.nona.someEncode.util.ByteArrays;

/**
 * @author nona9961
 * @date 2021/10/8 11:35
 */
public class ByteArray extends AbiParamType<byte[]> {
    public ByteArray(byte[] value) {
        super(32, value);
    }

    @Override
    protected byte[] regularValue(int length, byte[] value) {
        if (value.length > length) {
            throw new IllegalArgumentException("数组超过最大32字节");
        }
        return value;
    }

    @Override
    public byte[] generateAbi() {
        byte[] abiArray = getEmptyArr();
        ByteArrays.fillBytes(abiArray, value);
        return abiArray;
    }
}
