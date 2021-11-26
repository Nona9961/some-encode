package com.nona.someEncode.key;

import com.nona.someEncode.base.Base58;
import com.nona.someEncode.crypto.SECP256K1Support;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * btc的公私钥钱包
 * <p>
 * btc中一个私钥根据公钥是否压缩对应着2个地址<br/>
 * 默认使用不压缩的公钥对应的地址
 * <p>
 * ----->好像项目中并没有什么用
 *
 * @author nona9961
 * @date 2021/11/23 14:42
 */
public class BtcWallet extends SECP256K1KeyWallet {

    private final byte MAIN_NET_PREFIX = (byte) 0x80;
    private final byte TEST_NET_PREFIX = (byte) 0xef;
    private final byte COMPRESSED_PUB_KEY_SUFFIX = (byte) 0x01;
    private final String COMPRESSED_PRI_KEY_SUFFIX = "01";
    private final byte PUB_KEY_PREFIX = (byte) 0x00;

    private BtcWalletCache uncompressedBtcWalletCache;
    private BtcWalletCache compressedBtcWalletCache;

    private boolean isMainNet;

    private BtcWallet(boolean isMainNet) {
        super();
        this.isMainNet = isMainNet;
    }

    public static BtcWallet mainNet() {
        return new BtcWallet(true);
    }

    public static BtcWallet testNet() {
        return new BtcWallet(false);
    }


    /**
     * 默认提供不压缩公钥对应的私钥
     *
     * @return 16进制私钥
     */
    @Override
    public String getPrivateHex() {
        return getPrivateKey(false);
    }


    /**
     * 默认获取不压缩公钥的地址
     *
     * @return BTC地址
     */
    @Override
    public String getAddress() {
        return getAddress(false);
    }

    /**
     * 获取BTC地址，根据公钥的是否压缩获取不同的形式
     *
     * @param isCompressedPubKey 公钥是否压缩
     * @return btc地址
     */
    public String getAddress(boolean isCompressedPubKey) {
        BtcWalletCache cache = getCache(isCompressedPubKey);
        if (null != cache.pubHexAddress) {
            return cache.pubHexAddress;
        }
        byte[] pubKey = this.pub;
        if (isCompressedPubKey) {
            pubKey = SECP256K1Support.getPubCompressedFromPrivate(getPrivateHex());
        }
        byte[] pubMiddlePhase = hashPub(pubKey);
        byte[] withPrefix = addPubPrefix(pubMiddlePhase);
        byte[] checkSum = getCheckSum(withPrefix);
        byte[] beforeBase58 = addCheckSumSuffix(withPrefix, checkSum);
        String address = Base58.encode(beforeBase58);
        cache.pubHexAddress = address;
        return address;
    }

    /**
     * 获取16进制字符串的私钥，根据公钥是否压缩获取的形式不同
     *
     * @param isCompressedPubKey 是否是压缩公钥
     * @return 16进制字符串形式的私钥
     */
    public String getPrivateKey(boolean isCompressedPubKey) {
        BtcWalletCache cache = getCache(isCompressedPubKey);
        if (null != cache.privateHexKey) {
            return cache.privateHexKey;
        }
        cache.privateHexKey = Hex.toHexString(pri);
        if (isCompressedPubKey) {
            cache.privateHexKey = cache.privateHexKey + COMPRESSED_PRI_KEY_SUFFIX;
        }
        return cache.privateHexKey;
    }


    /**
     * 得到WIF格式的私钥
     * <p>
     * 根据对应的公钥是否是压缩的情形，具有2个WIF，它们的关系是：<br/>
     * Base58解码后去掉校验和，压缩的WIF比未压缩的WIF末尾多一个0x01
     *
     * @param isCompressedPubKey 对应是否是压缩格式的公钥
     * @return WIF格式的私钥
     */
    public String getWIFPrivateKey(boolean isCompressedPubKey) {
        BtcWalletCache cache = getCache(isCompressedPubKey);
        if (null != cache.priWIFKey) {
            return cache.priWIFKey;
        }
        byte[] extendedPriKey = extendPrivateKey(this.pri, isCompressedPubKey);
        byte[] checkSum = getCheckSum(extendedPriKey);
        byte[] beforeBase58 = addCheckSumSuffix(extendedPriKey, checkSum);
        String priKeyWIF = Base58.encode(beforeBase58);
        cache.priWIFKey = priKeyWIF;
        return priKeyWIF;
    }


    // ====================================================================
    // ======================== private method ============================
    // ====================================================================

    /**
     * 向公钥地址添加公钥地址前缀：0x00
     * <p>
     * 此时公钥应该已经经过了一次sha256，一次ripeMd160
     *
     * @param pubMiddlePhase 公钥的中间阶段
     * @return 携带前缀的公钥
     */
    private byte[] addPubPrefix(byte[] pubMiddlePhase) {
        byte[] withPrefix = new byte[pubMiddlePhase.length + 1];
        System.arraycopy(pubMiddlePhase, 0, withPrefix, 1, pubMiddlePhase.length);
        withPrefix[0] = PUB_KEY_PREFIX;
        return withPrefix;
    }

    /**
     * 对公钥进行1次sha256，1次ripeMd160，得到hash
     *
     * @param pubKey 公钥
     * @return 公钥的hash
     */
    private byte[] hashPub(byte[] pubKey) {
        SHA256.Digest sha256Digest = new SHA256.Digest();
        byte[] afterSha256 = sha256Digest.digest(pubKey);
        RIPEMD160.Digest ripeMd160Digest = new RIPEMD160.Digest();
        return ripeMd160Digest.digest(afterSha256);
    }

    /**
     * 计算<b>压缩私钥</b>（WIF）时，主网和测试网返回的前缀不一样
     *
     * <li>主网是 0x80</li>
     * <li>测试网是 0xef</li>
     *
     * @return 前缀
     */
    private byte compressedPriKeyPrefix() {
        return isMainNet ? MAIN_NET_PREFIX : TEST_NET_PREFIX;
    }

    /**
     * 向私钥添加前缀和后缀
     *
     * <h3>前缀</h3>
     * 根据主网和测试网的不同，添加的前缀内容也不同，具体来说：
     * <table>
     * <tr>
     * <td>主网</td>
     * <td>0x80</td>
     * </tr>
     * <tr>
     * <td>测试网</td>
     * <td>0xef</td>
     * </tr>
     * </table>
     * <h3>后缀</h3>
     * <p>
     * 如果是对应的压缩公钥的私钥WIF，在后面要添加上0x01；相对的，<br/>
     * 对应不压缩公钥的私钥WIF，后面什么都不添加
     *
     * @param pri                私钥
     * @param isCompressedPubKey 是否对应压缩公钥
     * @return 加了前缀的私钥
     */
    private byte[] extendPrivateKey(byte[] pri, boolean isCompressedPubKey) {
        int extendLength = 1 + (isCompressedPubKey ? 1 : 0);
        byte[] extendPriKey = new byte[pri.length + extendLength];
        extendPriKey[0] = compressedPriKeyPrefix();
        System.arraycopy(pri, 0, extendPriKey, 1, pri.length);
        if (isCompressedPubKey) {
            extendPriKey[extendPriKey.length - 1] = COMPRESSED_PUB_KEY_SUFFIX;
        }
        return extendPriKey;
    }

    /**
     * 获取校验和
     * <p>
     * 对data进行2次sha256，取前4byte
     *
     * @param data 原始内容
     * @return 校验和
     */
    private byte[] getCheckSum(byte[] data) {
        SHA256.Digest digest = new SHA256.Digest();
        byte[] result = digest.digest(data);
        digest.reset();
        byte[] checkSumFull = digest.digest(result);
        return Arrays.copyOfRange(checkSumFull, 0, 4);
    }

    /**
     * 向携带前缀的私钥后面添加校验和后缀
     *
     * @param originalData 原始内容
     * @param checkSum     校验和
     * @return 完整的私钥
     */
    private byte[] addCheckSumSuffix(byte[] originalData, byte[] checkSum) {
        byte[] result = new byte[originalData.length + checkSum.length];
        System.arraycopy(originalData, 0, result, 0, originalData.length);
        System.arraycopy(checkSum, 0, result, originalData.length, checkSum.length);
        return result;
    }

    /**
     * 根据公钥是否被压缩获取对应的缓存信息
     *
     * @param isCompressedPubKey 公钥是否被压缩
     * @return 缓存信息
     */
    private BtcWalletCache getCache(boolean isCompressedPubKey) {
        if (isCompressedPubKey) {
            if (null == this.compressedBtcWalletCache) {
                this.compressedBtcWalletCache = new BtcWalletCache();
            }
            return this.compressedBtcWalletCache;
        } else {
            if (null == this.uncompressedBtcWalletCache) {
                this.uncompressedBtcWalletCache = new BtcWalletCache();
            }
            return this.uncompressedBtcWalletCache;
        }
    }

    // ====================================================================
    // ========================== cache class =============================
    // ====================================================================

    /**
     * 缓存用的内部类，对私钥、WIF私钥、地址进行缓存
     */
    private static class BtcWalletCache {
        private String privateHexKey;
        private String priWIFKey;
        private String pubHexAddress;
    }

}
