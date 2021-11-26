package com.nona.someEncode.key;

import cn.hutool.core.codec.Base32;
import lombok.Getter;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.jcajce.provider.digest.Blake2b;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author nona9961
 * @date 2021/11/26 10:34
 */
public class FilWallet extends SECP256K1KeyWallet {

    /**
     * 我们只创建Protocol=1的secp256k1地址
     */
    private final byte PROTOCOL = (byte) 0x01;

    private String privateKeyHex;
    private String address;
    private final FilAddressType filAddressType;

    public FilWallet(FilAddressType filAddressType) {
        super();
        this.filAddressType = filAddressType;
    }

    @Override
    public String getPrivateHex() {
        if (this.privateKeyHex != null) {
            return this.privateKeyHex;
        }
        this.privateKeyHex = Hex.toHexString(this.pri);
        return this.privateKeyHex;
    }

    /**
     * fil的address有4种，这里只生成SECP256K1的地址(protocol = 1)
     * <p>
     * 组成为
     * main net or test net + protocol + blake160(pub) + sum check
     *
     * @return 地址
     */
    @Override
    public String getAddress() {
        if (null != this.address) {
            return this.address;
        }
        byte[] middleAddress = blake160HashPub(this.pub);
        byte[] sumCheck = getSumCheckWithProtocol(middleAddress);
        byte[] rawAddress = new byte[middleAddress.length + sumCheck.length];
        System.arraycopy(middleAddress, 0, rawAddress, 0, middleAddress.length);
        System.arraycopy(sumCheck, 0, rawAddress, middleAddress.length, sumCheck.length);
        String addressPayload = Base32.encode(rawAddress);
        this.address = (filAddressType.getAddressPrefix() + addressPayload).toLowerCase();
        return this.address;
    }

    /**
     * 获取对公钥进行一次blake160 hash的结果
     *
     * @param pub 公钥
     * @return hash
     */
    private byte[] blake160HashPub(byte[] pub) {
        Blake2b.Blake2b160 blake2b160 = new Blake2b.Blake2b160();
        return blake2b160.digest(pub);
    }


    /**
     * 加上Protocol后计算sum check的结果
     * <p>
     * sum check = blake4(PROTOCOL+blake160(pub))
     *
     * @param middle blake160之后的hash
     * @return sum check
     */
    private byte[] getSumCheckWithProtocol(byte[] middle) {
        byte[] addPrefix = new byte[middle.length + 1];
        System.arraycopy(middle, 0, addPrefix, 1, middle.length);
        addPrefix[0] = PROTOCOL;
        byte[] sumCheck = new byte[4];
        // 32bit = 4byte
        // Blake2bDigest(32)就是blake4
        Blake2bDigest blake2bDigest = new Blake2bDigest(32);
        blake2bDigest.update(addPrefix, 0, addPrefix.length);
        blake2bDigest.doFinal(sumCheck, 0);
        return sumCheck;
    }


    @Getter
    public enum FilAddressType {
        MAIN_NET_WALLET_ADDRESS("f1"),
        TEST_NET_WALLET_ADDRESS("t1");
        private final String addressPrefix;

        FilAddressType(String addressPrefix) {
            this.addressPrefix = addressPrefix;
        }
    }


}
