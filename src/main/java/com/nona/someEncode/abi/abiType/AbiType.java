package com.nona.someEncode.abi.abiType;

import cn.hutool.core.util.ArrayUtil;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author nona9961
 * @date 2021/10/8 14:25
 */
public interface AbiType<T> {
    /**
     * 将value转为abi编码
     *
     * @return 编码数组
     */
    byte[] generateAbi();


    default String abiHex() {
        byte[] bytes = generateAbi();
        return Hex.toHexString(bytes);
    }

}
