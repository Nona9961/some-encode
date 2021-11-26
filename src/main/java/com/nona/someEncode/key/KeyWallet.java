package com.nona.someEncode.key;

/**
 * 公私钥对接口
 *
 * @author nona9961
 * @date 2021/11/23 14:31
 */
public interface KeyWallet {

    String getPrivateHex();

    String getAddress();

}
