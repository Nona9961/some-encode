package com.nona.someEncode;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 提供base58的编码、解码
 * <p>
 * 编码过程：
 * <tr>1. 原字符串视为256进制的数字（ascii码）</tr>
 * <tr>aaa -> 97*256^2+97*256^1+97*256^0 = 6_356_992 +24_832 + 97 = 6381921(10进制)</tr>
 * <tr>2. 将数字转为58进制 </tr>
 * <tr> 6381921 = 32*58^3+41*58^2+7*58^1+7*58^0 </tr>
 * <tr>3. 对应字母表来表示</tr>
 * <tr> 32->Z 41->i 7->8 7->8 => Base58.encode("aaa") = "Zi88" </tr>
 * <br/>
 * 解码反着来
 *
 * @author nona9961
 * @date 2021/8/25 17:25
 */
public class Base58 {

    public static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    public static final BigInteger ALPHABET_SIZE = BigInteger.valueOf(BASE58_ALPHABET.length());

    public static String encode(byte[] rawBytes) {
        BigInteger rawInteger = new BigInteger(1, rawBytes);
        StringBuilder sb = new StringBuilder();
        while (rawInteger.compareTo(BigInteger.ZERO) != 0) {
            BigInteger[] quotientAndRemainder = rawInteger.divideAndRemainder(ALPHABET_SIZE);
            sb.append(BASE58_ALPHABET.charAt(quotientAndRemainder[1].intValue()));
            rawInteger = quotientAndRemainder[0];
        }
        // if byte array starts with 0,replace them to "1"
        // because new BigInteger(1,{0,1}) is same as new BigInteger(1,{1})
        for (int i = 0; i < rawBytes.length && rawBytes[i] == 0; i++) sb.append(BASE58_ALPHABET.charAt(0));
        return sb.reverse().toString();
    }

    /**
     * 将base58的字符串解码为字节数组
     *
     * @param base58Str base58字符串
     * @return 原字节数组
     */
    public static byte[] decode(String base58Str) {
        BigInteger num = BigInteger.ZERO;
        for (int i = 0; i < base58Str.length(); i++) {
            num = num.multiply(ALPHABET_SIZE);
            int digit = BASE58_ALPHABET.indexOf(base58Str.charAt(i));
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character for Base58Check: " + base58Str.charAt(i));
            }
            num = num.add(BigInteger.valueOf(digit));
        }
        byte[] numBytes = num.toByteArray();
        // remove "1" which is from encode() and add 0 to bytes
        int count = 0;
        for (int i = 0; i < base58Str.length() && base58Str.charAt(i) == BASE58_ALPHABET.charAt(0); i++) {
            count++;
        }
        // 去除bigInteger符号占位
        if (numBytes[0] == 0) {
            count--;
        }
        if (count == -1) {
            numBytes = Arrays.copyOfRange(numBytes, 1, numBytes.length);
        }
        if (count > 0) {
            byte[] originBytes = new byte[numBytes.length + count];
            System.arraycopy(numBytes, 0, originBytes, count, numBytes.length);
            numBytes = originBytes;
        }
        return numBytes;
    }

}
