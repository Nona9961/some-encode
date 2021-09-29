package com.nona.someEncode.abi.abiType;

import cn.hutool.core.util.ArrayUtil;
import org.bouncycastle.util.encoders.Hex;

/**
 * uint8-uint256的abi编码最大长度就是32byte
 *
 * @author nona9961
 * @date 2021/9/29 15:19
 */
public abstract class Uint<T extends Number> extends AbiParamType<T> {
    private static final int SIZE = 32;

    public Uint(T value) {
        super(SIZE, value);
    }

    public String abiHex() {
        byte[] bytes = generateAbi();
        return Hex.toHexString(bytes);
    }

    /**
     * 对数字长度的检查
     *
     * @param t 数字
     * @return 是否通过
     */
    protected abstract byte[] checkAndTransBytes(T t);

    /**
     * 因为最大不超过32字节，而abi静态参数类型的编码都要补齐至32字节
     * 所以所有的uint都用一个byte[32]装
     *
     * @return 32字节的零数组
     */
    protected byte[] getEmptyArr() {
        return new byte[length];
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


    public static class Uint8 extends Uint<Byte> {
        public Uint8(Byte value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(Byte aByte) {
            return new byte[]{aByte};
        }

        /**
         * Uint8和byte都是8位，不会有长度超出的情况
         *
         * @return abi
         */
        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            abiByteArr[length - 1] = value;
            return abiByteArr;
        }
    }

    public static class Uint16 extends Uint<Integer> {
        private final int MAX_BIT = 16;

        public Uint16(Integer value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(Integer integer) {
            int primitive = integer;
            if (primitive >> MAX_BIT != 0) {
                throw new IllegalArgumentException("大小超过Uint16最大值");
            }
            return new byte[]{(byte) (primitive >> Byte.SIZE), (byte) primitive};
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint24 extends Uint<Integer> {
        private final int MAX_BIT = 24;

        public Uint24(Integer value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(Integer integer) {
            int primitive = integer;
            if (primitive >> MAX_BIT != 0) {
                throw new IllegalArgumentException("大小超过Uint24最大值");
            }
            return new byte[]{(byte) (primitive >> (2 * Byte.SIZE)), (byte) (primitive >> Byte.SIZE), (byte) primitive};
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

}
