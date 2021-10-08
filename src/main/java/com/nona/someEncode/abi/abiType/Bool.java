package com.nona.someEncode.abi.abiType;

/**
 * @author nona9961
 * @date 2021/10/8 11:24
 */
public class Bool extends AbiParamType<Boolean> {
    public Bool(Boolean value) {
        super(32, value);
    }

    @Override
    protected Boolean regularValue(int length, Boolean value) {
        if (null == value) {
            throw new IllegalArgumentException("bool不能为null");
        }
        return value;
    }

    @Override
    public byte[] generateAbi() {
        byte[] abiArr = getEmptyArr();
        abiArr[abiArr.length - 1] = value ? (byte) 1 : 0;
        return abiArr;
    }
}
