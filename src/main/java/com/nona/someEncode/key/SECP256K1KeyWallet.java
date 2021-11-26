package com.nona.someEncode.key;

import com.nona.someEncode.crypto.SECP256K1Support;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyPair;

/**
 * @author nona9961
 * @date 2021/11/23 15:08
 */
public abstract class SECP256K1KeyWallet implements KeyWallet {

    protected final static int PRI_LENGTH = 32;
    protected final static int PUB_LENGTH = 64;

    protected final byte[] pri;
    protected final byte[] pub;

    protected SECP256K1KeyWallet() {
        KeyPair keyPair = SECP256K1Support.generateKeyPair();
        BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
        this.pri = normalizePrivateKey(privateKey.getD());
        BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();
        byte[] q = publicKey.getQ().getEncoded(false);
        this.pub = normalizePublicKey(q);
    }

    protected SECP256K1KeyWallet(String priKeyHex, boolean isPubCompressed) {
        this.pri = Hex.decode(priKeyHex);
        byte[] rawPub;
        if (isPubCompressed) {
            rawPub = SECP256K1Support.getPubCompressedFromPrivate(priKeyHex);
        } else {
            rawPub = SECP256K1Support.getPubUncompressedFromPrivate(priKeyHex);
        }
        this.pub = normalizePublicKey(rawPub);
    }

    /**
     * 生成私钥的bigInteger可能有33byte，第一个是0x00，是符号占位直接去掉
     *
     * @param d private key in bigInteger form
     * @return private key in byte array form
     */
    protected byte[] normalizePrivateKey(BigInteger d) {
        byte[] rawBytes = d.toByteArray();
        if (rawBytes.length == 32) {
            return rawBytes;
        }
        byte[] pri = new byte[PRI_LENGTH];
        System.arraycopy(rawBytes, 1, pri, 0, PRI_LENGTH);
        return pri;
    }

    /**
     * 生成的public key 第一个byte一定是 0x04，
     * 这个方法用于处理得到的公钥，比如去掉0x04，默认空实现
     *
     * @param pubByteWithPrefix public key with 0x04
     * @return public key
     */
    protected byte[] normalizePublicKey(byte[] pubByteWithPrefix) {
        return pubByteWithPrefix;
    }
}
