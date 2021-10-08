package com.nona.someEncode.abi.abiType;

import java.math.BigInteger;

/**
 * 动态长度的编码：<br/>
 * 动态长度除了被编译的内容以外还有动态长度（偏移量由得在编译的时候知晓）
 *
 * @author nona9961
 * @date 2021/10/8 11:54
 */
public abstract class AbiDynamicType<T> {
    public AbiDynamicType(T value) {
    }


    public abstract BigInteger dynamicLength();
}
