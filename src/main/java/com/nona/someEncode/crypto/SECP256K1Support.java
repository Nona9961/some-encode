package com.nona.someEncode.crypto;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

/**
 * 用于生成secp256k1的公私钥和签名,只做和SECP256K1相关的事情
 * <p>
 * 保存着椭圆图形信息，支持从私钥导出公钥
 *
 * @author nona9961
 * @date 2021/8/25 11:57
 */
@Log4j2
public class SECP256K1Support {

    private static final String ALGORITHM = "ECDSA";
    private static final String PROVIDER = "BC";
    private static final String CURVE_NAME = "secp256k1";
    private static final String HEX_PREFIX = "0x";
    private static final int HEX_PRIVATE_KEY_LENGTH = 64;

    private static final BigInteger HALF_N;
    private static KeyPairGenerator keyPairGenerator;
    private static X9ECParameters curve;
    private static ECDomainParameters ecDomainParameters;

    // init
    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(CURVE_NAME);
            keyPairGenerator.initialize(ecGenParameterSpec);
            SECP256K1Support.keyPairGenerator = keyPairGenerator;
            SECP256K1Support.curve = SECNamedCurves.getByName(CURVE_NAME);
            SECP256K1Support.ecDomainParameters = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
            HALF_N = curve.getN().shiftRight(1);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("init SECP256K1Support failed,the reason is: " + e.getMessage());
        }
    }

    /**
     * 提供公私钥对
     *
     * @return 公私钥对
     */
    public static KeyPair generateKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 基于secp256k1的签名方法，用sha256签
     *
     * @param signData 待签名数据
     * @param hexPk    私钥——16进制字符串
     * @return 签名——16进制字符串
     */
    public static String sign(byte[] signData, String hexPk) {
        if (ArrayUtil.isEmpty(signData) || StrUtil.isBlank(hexPk)) {
            throw new IllegalArgumentException("sign data or private key is empty");
        }
        hexPkLengthCheck(hexPk);
        BigInteger pkInteger = new BigInteger(1, Hex.decode(hexPk));
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(pkInteger, ecDomainParameters);
        signer.init(true, ecPrivateKeyParameters);
        BigInteger[] components = signer.generateSignature(signData);
        components[1] = regularize(components[1]);
        byte v = getV(hexPk, components, signData);
        byte[] sign = new byte[65];
        System.arraycopy(modifySignComp(components[0]), 0, sign, 0, 32);
        System.arraycopy(modifySignComp(components[1]), 0, sign, 32, 32);
        sign[64] = v;
        return Hex.toHexString(sign);
    }

    /**
     * 从私钥中提出未被压缩的公钥
     * <p>
     * 要被压缩过的公钥请看{@link #getPubCompressedFromPrivate(String)}
     *
     * @param hexPrivateStr 私钥——16进制字符串
     * @return 公钥——byte数组
     */
    public static byte[] getPubUncompressedFromPrivate(String hexPrivateStr) {
        hexPkLengthCheck(hexPrivateStr);
        final ECPoint point = getPubPoint(hexPrivateStr);
        return point.getEncoded(false);
    }

    /**
     * 从私钥中提出被压缩的公钥
     * <p>
     * 要没有被压缩过的公钥请看{@link #getPubUncompressedFromPrivate(String)}
     *
     * @param hexPrivateStr 私钥——16进制字符串
     * @return 公钥——byte数组
     */
    public static byte[] getPubCompressedFromPrivate(String hexPrivateStr) {
        hexPkLengthCheck(hexPrivateStr);
        final ECPoint point = getPubPoint(hexPrivateStr);
        return point.getEncoded(true);
    }


    /*================================== private method ===============================================*/

    /**
     * 从私钥中找到公钥对应的几何点
     *
     * @param hexPrivateStr 私钥——16进制字符串
     * @return 公钥对应的几何点
     */
    private static ECPoint getPubPoint(String hexPrivateStr) {
        BigInteger privateValue = new BigInteger(hexPrivateStr, 16);

        if (privateValue.bitLength() > curve.getN().bitLength()) {
            privateValue = privateValue.mod(curve.getN());
        }
        // K = kG
        return new FixedPointCombMultiplier().multiply(curve.getG(), privateValue);
    }

    /**
     * 该BigInteger应该有32byte，但有可能存在表征符号，去掉
     * <p>
     *
     * @param bigInteger biginteger
     * @return byte[]
     */
    private static byte[] modifySignComp(BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == 32) {
            return byteArray;
        }
        byte[] lessByte = new byte[32];
        System.arraycopy(byteArray, 1, lessByte, 0, 32);
        return lessByte;
    }

    /**
     * 检查私钥长度
     *
     * @param pk 十六进制私钥
     */
    private static void hexPkLengthCheck(String pk) {
        if (pk.startsWith(HEX_PREFIX)) {
            pk = pk.substring(2);
        }
        if (pk.length() != HEX_PRIVATE_KEY_LENGTH) {
            throw new RuntimeException("invalid private key,please check it");
        }
    }

    /**
     * 椭圆签名的(r,s)，如果s大于n的一半，那么将会有两个解，如：
     * <br/>
     * n = 10, s = 8 ,那么 (-8)%10=2,所以(r,8)和(r,2)都是解
     *
     * @param s 椭圆签名中的s
     * @return 修正后的s
     */
    private static BigInteger regularize(BigInteger s) {
        if (s.compareTo(HALF_N) > 0) {
            return curve.getN().subtract(s);
        }
        return s;
    }

    /**
     * 通过签名和原始数据找到trx签名中的v
     *
     * @param pk        私钥
     * @param component 签名，(r,s)
     * @param signData  原始数据
     * @return v
     */
    private static byte getV(String pk, BigInteger[] component, byte[] signData) {
        byte[] pubFromPrivate = getPubUncompressedFromPrivate(pk);
        int retryTimes = 0;
        for (int i = 0; i < 4; i++) {
            byte[] pubNew = resolvePubFromSign(i, component, signData);
            if (pubNew != null && Arrays.equals(pubNew, pubFromPrivate)) {
                retryTimes = i;
                break;
            }
        }
        byte vRaw = (byte) (retryTimes + 27);
        return vRaw > 27 ? (byte) (vRaw - 27) : vRaw;
    }

    /**
     * 用签名和签名原始数据找到公钥
     *
     * @param nonce         进行次数
     * @param signComponent 签名
     * @param signData      待签数据
     * @return 公钥
     */
    private static byte[] resolvePubFromSign(int nonce, BigInteger[] signComponent, byte[] signData) {
        BigInteger r = signComponent[0];
        BigInteger s = signComponent[1];
        BigInteger n = curve.getN(); // Curve order.
        BigInteger i = BigInteger.valueOf((long) nonce / 2);
        BigInteger x = r.add(i.multiply(n));
        //   1.2. Convert the integer x to an octet string X of length mlen
        // using the conversion routine
        //        specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or
        // mlen = ⌈m/8⌉.
        //   1.3. Convert the octet string (16 set binary digits)||X to an
        // elliptic curve point R using the
        //        conversion routine specified in Section 2.3.4. If this
        // conversion routine outputs “invalid”, then
        //        do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed
        // public key.
        ECCurve.Fp ecCurve = (ECCurve.Fp) curve.getCurve();
        BigInteger prime = ecCurve.getQ(); // Bouncy Castle is not consistent
        // about the letter it uses for the prime.
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything
            // takes place modulo Q.
            return null;
        }
        // Compressed allKeys require you to know an extra bit of data about the
        // y-coord as there are two possibilities.
        // So it's encoded in the recId.
        ECPoint R = decompressKey(x, (nonce & 1) == 1);
        //   1.4. If nR != point at infinity, then do another iteration of
        // Step 1 (callers responsibility).
        if (!R.multiply(n).isInfinity()) {
            return null;
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature
        // verification.
        BigInteger e = new BigInteger(1, signData);
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this
        // function via iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform
        // this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that
        // z + e = 0 (mod n). In the above equation
        // ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then
        // taking the mod. For example the additive
        // inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and -3 mod
        // 11 = 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = r.modInverse(n);
        BigInteger srInv = rInv.multiply(s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint.Fp q = (ECPoint.Fp) ECAlgorithms.sumOfTwoMultiplies(curve.getG(), eInvrInv, R, srInv);
        return q.getEncoded(false);
    }

    /**
     * Decompress a compressed public key (x co-ord and low-bit of y-coord).
     */
    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(curve.getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return curve.getCurve().decodePoint(compEnc);
    }
}
