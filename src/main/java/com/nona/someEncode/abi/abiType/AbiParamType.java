package com.nona.someEncode.abi.abiType;

import lombok.Getter;

/**
 * abi参数类型
 *
 * @author nona9961
 * @date 2021/9/29 14:47
 */
@Getter
public abstract class AbiParamType<T> {
    protected int length;
    protected T value;

    public AbiParamType(int length, T value) {
        this.length = length;
        this.value = value;
    }

    /**
     * 将value转为abi编码
     *
     * @return 编码数组
     */
    public abstract byte[] generateAbi();

}