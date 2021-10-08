package com.nona.someEncode.abi.abiType;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * uint8-uint256的abi编码最大长度就是32byte
 * 最后的abi码都要补齐32byte……只需要Uint256即可
 * 写到一半发现不用写那么多，中间部分的Uint如果以后遇到了再添加
 *
 * @author nona9961
 * @date 2021/9/29 15:19
 */
public abstract class Uint<T extends Number> extends AbiParamType<T> {

    public Uint(T value) {
        super(32, value);
    }

    /**
     * 获取最大的位数
     *
     * @return 最大的位数
     */
    protected abstract int getMaxBit();

    /**
     * 编码为abi
     *
     * @return 字节数组
     */
    @Override
    public byte[] generateAbi() {
        byte[] valueBytes = transferByteArray(value);
        byte[] abiByteArr = getEmptyArr();
        fillRevertBytes(abiByteArr, valueBytes);
        return abiByteArr;
    }


    /* ************************************************************************ */
    /* ****************************  only for subClass ************************ */
    /* ************************************************************************ */

    @Override
    protected T regularValue(int length, T value) {
        if (value instanceof BigInteger) {
            BigInteger bigInteger = ((BigInteger) value).shiftRight(getMaxBit());
            if (bigInteger.compareTo(BigInteger.ZERO) != 0) {
                throw new IllegalArgumentException("超过该Uint最大值");
            }
            return value;
        }
        if (value instanceof Byte || value instanceof Integer || value instanceof Long) {
            long primitive = value.longValue();
            if (primitive >> getMaxBit() != 0) {
                throw new IllegalArgumentException("超过该Uint最大值");
            }
            return value;
        }
        throw new UnsupportedOperationException("不支持的数值");
    }

    /**
     * 将value的值转为它的字节数组
     *
     * @param value 数字
     * @return 字节数组
     */
    protected byte[] transferByteArray(Number value) {
        int length = getMaxBit() / Byte.SIZE;
        if (value instanceof Byte || value instanceof Integer || value instanceof Long) {
            long along = value.longValue();
            byte[] bytes = new byte[length];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (along >> ((length - i - 1) * Byte.SIZE));
            }
            return bytes;
        }
        if (value instanceof BigInteger) {
            byte[] bytes = ((BigInteger) value).toByteArray();
            if (bytes[0] == 0) {
                // bigInteger 第一个数组元素可能是0，这只是一个标志，不是数值应该有的数组，去掉
                return Arrays.copyOfRange(bytes, 1, bytes.length);
            }
            return bytes;
        }
        throw new IllegalArgumentException("不支持的数值");
    }

    /* ************************************************************************ */
    /* ****************************  all subClass ***************************** */
    /* ************************************************************************ */
    public static class Uint8 extends Uint<Byte> {
        public Uint8(Byte value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return 8;
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
        protected int getMaxBit() {
            return MAX_BIT;
        }

    }

    public static class Uint24 extends Uint<Integer> {

        private final int MAX_BIT = 24;

        public Uint24(Integer value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }

    }

    public static class Uint32 extends Uint<Integer> {

        private final int MAX_BIT = 32;

        public Uint32(Integer value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

    public static class Uint40 extends Uint<Long> {

        private final int MAX_BIT = 40;

        public Uint40(Long value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

    public static class Uint48 extends Uint<Long> {

        private final int MAX_BIT = 48;

        public Uint48(Long value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

    public static class Uint56 extends Uint<Long> {

        private final int MAX_BIT = 56;

        public Uint56(Long value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }

    }

    public static class Uint64 extends Uint<Long> {

        private final int MAX_BIT = 64;

        public Uint64(Long value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

    public static class Uint72 extends Uint<BigInteger> {

        private final int MAX_BIT = 72;

        public Uint72(BigInteger value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

    public static class Uint80 extends Uint<BigInteger> {

        private final int MAX_BIT = 80;

        public Uint80(BigInteger value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }

    }

    public static class Uint88 extends Uint<BigInteger> {

        private final int MAX_BIT = 88;

        public Uint88(BigInteger value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

    public static class Uint96 extends Uint<BigInteger> {

        private final int MAX_BIT = 96;

        public Uint96(BigInteger value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }

    }

    public static class Uint256 extends Uint<BigInteger> {

        private final int MAX_BIT = 256;

        public Uint256(BigInteger value) {
            super(value);
        }

        @Override
        protected int getMaxBit() {
            return MAX_BIT;
        }
    }

}
