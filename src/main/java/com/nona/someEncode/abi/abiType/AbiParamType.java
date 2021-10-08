package com.nona.someEncode.abi.abiType;

import cn.hutool.core.util.ArrayUtil;
import lombok.Getter;
import org.bouncycastle.util.encoders.Hex;

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
        T checkedValue = regularValue(value);
        this.length = length;
        this.value = checkedValue;
    }

    /**
     * 用于对value规范或者修改
     *
     * @param value 传入的值
     * @return 修改后的值
     */
    protected abstract T regularValue(T value);

    /**
     * 将value转为abi编码
     *
     * @return 编码数组
     */
    public abstract byte[] generateAbi();

    public String abiHex() {
        byte[] bytes = generateAbi();
        return Hex.toHexString(bytes);
    }

    /**
     * 将fillArray倒叙填充进original中
     * eg:
     * {1,2}填充{0,0,0,0}为{0,0,1,2}
     *
     * @param original  待填充数组
     * @param fillArray 填充数组
     */
    protected void fillRevertBytes(byte[] original, byte[] fillArray) {
        if (ArrayUtil.isEmpty(original) || ArrayUtil.isEmpty(fillArray)) {
            return;
        }
        for (int i = fillArray.length - 1, j = original.length - 1; i >= 0; i--, j--) {
            original[j] = fillArray[i];
        }
    }

}