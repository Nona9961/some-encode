package com.nona.someEncode.key;

import com.nona.someEncode.base.Base58;
import com.nona.someEncode.crypto.SECP256K1Support;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.util.encoders.Hex;

/**
 * trx公私钥和地址的类
 *
 * @author nona9961
 * @date 2021/8/25 11:42
 */
public class TrxWallet extends SECP256K1KeyWallet {

    private final static byte ADDRESS_PREFIX = 0x41;


    private String privateKey;
    private String address;

    /**
     * 根据十六进制私钥字符串生成对应的地址
     *
     * @param hexPrivateStr 私钥字符串（十六进制）
     * @return 对应的Address
     */
    public static String addressFromPrivateKey(String hexPrivateStr) {
        byte[] pubFromPrivate = SECP256K1Support.getPubUncompressedFromPrivate(hexPrivateStr);
        byte[] pub = removeThePrefixOfThePublicKey(pubFromPrivate);
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
        if (null != this.address) {
            return this.address;
        }
        byte[] raw = hashForAddress(this.pub);
        byte[] addressAndCheck = baseCheck(raw);
        this.address = Base58.encode(addressAndCheck);
        return this.address;
    }

    /**
     * 获取私钥（16进制）的方法
     *
     * @return 私钥(16进制)
     */
    public String getPrivateHex() {
        if (this.privateKey != null) {
            return this.privateKey;
        }
        this.privateKey = Hex.toHexString(this.pri);
        return this.privateKey;
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
     * 直接看{@link #removeThePrefixOfThePublicKey(byte[])}
     *
     * @param pubByteWithPrefix public key with 0x04
     * @return public key removed 0x04
     */
    @Override
    protected byte[] normalizePublicKey(byte[] pubByteWithPrefix) {
        return removeThePrefixOfThePublicKey(pubByteWithPrefix);
    }

    /**
     * 生成的public key 第一个byte一定是 0x04 ，这个要去掉
     *
     * @param pubByteWithPrefix public key with 0x04
     * @return public key removed 0x04
     */
    private static byte[] removeThePrefixOfThePublicKey(byte[] pubByteWithPrefix) {
        byte[] pubWithoutPrefix = new byte[PUB_LENGTH];
        System.arraycopy(pubByteWithPrefix, 1, pubWithoutPrefix, 0, PUB_LENGTH);
        return pubWithoutPrefix;
    }
}
