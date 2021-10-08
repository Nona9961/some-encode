package com.nona.someEncode.abi.abiType;

import java.math.BigInteger;

/**
 * 动态长度的编码：<br/>
 * 动态长度除了被编译的内容以外还有动态长度（偏移量得在编译的时候才能知晓）
 * todo: 先写这些吧
 *
 * @author nona9961
 * @date 2021/10/8 11:54
 */
public abstract class AbiDynamicType<T> implements AbiType<T> {
    protected final T value;

    public AbiDynamicType(T value) {
        this.value = value;
    }


    public abstract BigInteger dynamicLength();
}
