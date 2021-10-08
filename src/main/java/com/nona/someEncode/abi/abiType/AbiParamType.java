package com.nona.someEncode.abi.abiType;

import lombok.Getter;

/**
 * abi参数类型
 *
 * @author nona9961
 * @date 2021/9/29 14:47
 */
@Getter
public abstract class AbiParamType<T> implements AbiType<T> {
    public static final int FIXED_LENGTH = 32;
    protected int length;
    protected T value;

    public AbiParamType(int length, T value) {
        T checkedValue = regularValue(length, value);
        this.length = length;
        this.value = checkedValue;
    }

    /**
     * 用于对value规范或者修改
     *
     * @param length 传入的length
     * @param value  传入的value
     * @return 修改后的value
     */
    protected T regularValue(int length, T value) {
        return value;
    }


    /**
     * 因为最大不超过32字节，而abi静态参数类型的编码都要补齐至32字节
     * 所以所有的uint都用一个byte[32]装
     *
     * @return 32字节的零数组
     */
    protected byte[] getEmptyArr() {
        return new byte[length];
    }

}