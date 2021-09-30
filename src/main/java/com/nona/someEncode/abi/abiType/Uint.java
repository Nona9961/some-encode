package com.nona.someEncode.abi.abiType;

import cn.hutool.core.util.ArrayUtil;
import org.bouncycastle.util.encoders.Hex;

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
    private static final int SIZE = 32;

    public Uint(T value) {
        super(SIZE, value);
    }

    public String abiHex() {
        byte[] bytes = generateAbi();
        return Hex.toHexString(bytes);
    }


    /**
     * 检查数字长度并且转为字节数组
     *
     * @param t 数字
     * @return 字节数组
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

    protected byte[] transferByteArray(Number value, int maxBit) {
        int length = maxBit / Byte.SIZE;
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
            return transferByteArray(integer, MAX_BIT);
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
            return transferByteArray(integer, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint32 extends Uint<Integer> {

        private final int MAX_BIT = 32;

        public Uint32(Integer value) {
            super(value);
        }

        /**
         * int 就是32位的
         *
         * @param integer value
         * @return byte array
         */
        @Override
        protected byte[] checkAndTransBytes(Integer integer) {
            int primitive = integer;
            return transferByteArray(integer, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint40 extends Uint<Long> {

        private final int MAX_BIT = 40;

        public Uint40(Long value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(Long value) {
            long primitive = value;
            if (primitive >> MAX_BIT != 0) {
                throw new IllegalArgumentException("超过Uint40最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint48 extends Uint<Long> {

        private final int MAX_BIT = 48;

        public Uint48(Long value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(Long value) {
            long primitive = value;
            if (primitive >> MAX_BIT != 0) {
                throw new IllegalArgumentException("超过Uint48最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint56 extends Uint<Long> {

        private final int MAX_BIT = 56;

        public Uint56(Long value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(Long value) {
            long primitive = value;
            if (primitive >> MAX_BIT != 0) {
                throw new IllegalArgumentException("超过Uint56最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint64 extends Uint<Long> {

        private final int MAX_BIT = 64;

        public Uint64(Long value) {
            super(value);
        }

        /**
         * long就是64位的
         *
         * @param value long
         * @return byte array
         */
        @Override
        protected byte[] checkAndTransBytes(Long value) {
            long primitive = value;
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint72 extends Uint<BigInteger> {

        private final int MAX_BIT = 72;

        public Uint72(BigInteger value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(BigInteger value) {
            BigInteger bigInteger = value.shiftRight(MAX_BIT);
            if (bigInteger.compareTo(BigInteger.ZERO) != 0) {
                throw new IllegalArgumentException("超过Uint72最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint80 extends Uint<BigInteger> {

        private final int MAX_BIT = 80;

        public Uint80(BigInteger value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(BigInteger value) {
            BigInteger bigInteger = value.shiftRight(MAX_BIT);
            if (bigInteger.compareTo(BigInteger.ZERO) != 0) {
                throw new IllegalArgumentException("超过Uint80最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint88 extends Uint<BigInteger> {

        private final int MAX_BIT = 88;

        public Uint88(BigInteger value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(BigInteger value) {
            BigInteger bigInteger = value.shiftRight(MAX_BIT);
            if (bigInteger.compareTo(BigInteger.ZERO) != 0) {
                throw new IllegalArgumentException("超过Uint88最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint96 extends Uint<BigInteger> {

        private final int MAX_BIT = 96;

        public Uint96(BigInteger value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(BigInteger value) {
            BigInteger bigInteger = value.shiftRight(MAX_BIT);
            if (bigInteger.compareTo(BigInteger.ZERO) != 0) {
                throw new IllegalArgumentException("超过Uint96最大值");
            }
            return transferByteArray(value, MAX_BIT);
        }

        @Override
        public byte[] generateAbi() {
            byte[] abiByteArr = getEmptyArr();
            byte[] valueBytes = checkAndTransBytes(value);
            fillRevertBytes(abiByteArr, valueBytes);
            return abiByteArr;
        }
    }

    public static class Uint256 extends Uint<BigInteger> {

        private final int MAX_BIT = 256;

        public Uint256(BigInteger value) {
            super(value);
        }

        @Override
        protected byte[] checkAndTransBytes(BigInteger value) {
            BigInteger bigInteger = value.shiftRight(MAX_BIT);
            if (bigInteger.compareTo(BigInteger.ZERO) != 0) {
                throw new IllegalArgumentException("超过Uint256最大值");
            }
            return transferByteArray(value, MAX_BIT);
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
