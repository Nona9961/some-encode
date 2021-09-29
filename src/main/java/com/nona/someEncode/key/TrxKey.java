package com.nona.someEncode.key;

import com.nona.someEncode.base.Base58;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyPair;

/**
 * trx公私钥和地址的类
 *
 * @author nona9961
 * @date 2021/8/25 11:42
 */
public class TrxKey {

    private final static int PRI_LENGTH = 32;
    private final static int PUB_LENGTH = 64;
    private final static byte ADDRESS_PREFIX = 0x41;


    private final byte[] pri;
    private final byte[] pub;

    public TrxKey() {
        KeyPair keyPair = SECP256K1KeyPair.generateKeyPair();
        BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
        this.pri = normalizePrivateKey(privateKey.getD());
        BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();
        byte[] q = publicKey.getQ().getEncoded(false);
        this.pub = normalizePublicKey(q);
    }

    /**
     * 根据十六进制私钥字符串生成对应的地址
     *
     * @param hexPrivateStr 私钥字符串（十六进制）
     * @return 对应的Address
     */
    public static String addressFromPrivateKey(String hexPrivateStr) {
        byte[] pubFromPrivate = SECP256K1KeyPair.getPubFromPrivate(hexPrivateStr);
        byte[] pub = normalizePublicKey(pubFromPrivate);
        byte[] raw = hashForAddress(pub);
        byte[] addressAndCheck = baseCheck(raw);
        return Base58.encode(addressAndCheck);
    }

    /**
     * 将地址转回没有加0x41和baseCheck的样子。长度是160bit
     *
     * @param address trx address
     * @return middle address in hex
     */
    public static String revertMiddleAddress(String address) {
        byte[] addressBytes = Base58.decode(address);
        // 去掉前面第一个0x41和最后的4个byte
        if (addressBytes.length != 25) {
            throw new IllegalArgumentException("invalid trx address");
        }
        byte[] middleAddress = new byte[20];
        System.arraycopy(addressBytes, 1, middleAddress, 0, 20);
        return Hex.toHexString(middleAddress);
    }


    public static String revertMiddleAddressWithPrefix(String address) {
        byte[] addressBytes = Base58.decode(address);
        // 去掉最后的4个byte
        if (addressBytes.length != 25) {
            throw new IllegalArgumentException("invalid trx address");
        }
        byte[] middleAddress = new byte[21];
        System.arraycopy(addressBytes, 0, middleAddress, 0, 21);
        return Hex.toHexString(middleAddress);
    }

    /*================================ non static method =======================================*/

    /**
     * 生成地址
     *
     * @return base58格式的address
     */
    public String getAddress() {
        byte[] raw = hashForAddress(this.pub);
        byte[] addressAndCheck = baseCheck(raw);
        return Base58.encode(addressAndCheck);
    }

    /**
     * 获取私钥（16进制）的方法
     *
     * @return 私钥(16进制)
     */
    public String getPrivateHex() {
        return Hex.toHexString(this.pri);
    }


    /*============================= private method =====================================*/


    /**
     * 计算中间地址
     * <p>
     * 根据公钥数组(64byte)第一次sha3得到h
     * 0x41与h的后20位拼接为addressRaw
     *
     * @param pub 64byte公钥数组
     * @return addressRaw
     */
    private static byte[] hashForAddress(byte[] pub) {
        Keccak.Digest256 digest = new Keccak.Digest256();
        byte[] h = digest.digest(pub);
        byte[] raw = new byte[21];
        raw[0] = ADDRESS_PREFIX;
        System.arraycopy(h, h.length - 20, raw, 1, 20);
        return raw;
    }

    /**
     * baseCheck计算最终地址
     * <p>
     * 对{@link #hashForAddress(byte[])}结果的addressRaw进行sha256计算，
     * 取结果的前4位拼接到addressRaw的最后得到数组格式的address
     *
     * @param addressRaw 中间地址
     * @return 数组格式的address
     */
    private static byte[] baseCheck(byte[] addressRaw) {
        SHA256.Digest digest = new SHA256.Digest();
        byte[] hashFirst = digest.digest(addressRaw);
        digest.reset();
        byte[] hashResult = digest.digest(hashFirst);
        byte[] addressAndCheck = new byte[25];
        System.arraycopy(addressRaw, 0, addressAndCheck, 0, addressRaw.length);
        System.arraycopy(hashResult, 0, addressAndCheck, addressRaw.length, 4);
        return addressAndCheck;
    }


    /**
     * 生成私钥的bigInteger可能有33byte，第一个是0x00，是符号占位直接去掉
     *
     * @param d private key in bigInteger form
     * @return private key in byte array form
     */
    private static byte[] normalizePrivateKey(BigInteger d) {
        byte[] rawBytes = d.toByteArray();
        if (rawBytes.length == 32) {
            return rawBytes;
        }
        byte[] pri = new byte[PRI_LENGTH];
        System.arraycopy(rawBytes, 1, pri, 0, PRI_LENGTH);
        return pri;
    }

    /**
     * 生成的public key 第一个byte一定是 0x04 ，这个要去掉
     *
     * @param pubByteWithPrefix public key with 0x04
     * @return public key removed 0x04
     */
    private static byte[] normalizePublicKey(byte[] pubByteWithPrefix) {
        byte[] pubWithoutPrefix = new byte[PUB_LENGTH];
        System.arraycopy(pubByteWithPrefix, 1, pubWithoutPrefix, 0, PUB_LENGTH);
        return pubWithoutPrefix;
    }

}
