package com.nona.someEncode.abi.abiType;

import cn.hutool.core.util.StrUtil;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author nona9961
 * @date 2021/10/8 9:50
 */
public class Address extends AbiParamType<String> {
    private static final String PREFIX = "0x";
    private static final int ADDRESS_LENGTH = 40;

    public Address(String value) {
        super(32, value);
    }

    @Override
    protected String regularValue(int length, String value) {
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException("未传入地址");
        }
        String realAddress = value;
        if (value.startsWith(PREFIX)) {
            realAddress = value.substring(PREFIX.length());
        }
        if (realAddress.length() != ADDRESS_LENGTH) {
            throw new IllegalArgumentException("地址长度应该为" + ADDRESS_LENGTH + "位Hex String，当前（去掉0x）为" + realAddress.length() + "位");
        }
        return realAddress;
    }

    @Override
    public byte[] generateAbi() {
        byte[] bytes = Hex.decode(value);
        byte[] abiBytes = getEmptyArr();
        fillRevertBytes(abiBytes, bytes);
        return abiBytes;
    }
}
