/**
 *
 */
package com.magic.api.commons.tools.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jolestar
 */
public class AESEncrypter {

    private static String aesKeyStr = "NGQxNmUwMjM4M2Y0MTI2MTM3NDI0Y2MxMjA1N2IyNDM=";

    public static String NEW_AES_KEY_STR = "MzJCRTI2RkUxRUY3NDgxRUE3MDRCMzhFNzZENjhDNkM=";

    private SecretKey aesKey;

    private AESEncrypter() {
        aesKey = loadAesKey();
    }

    private AESEncrypter(String aes) {
        aesKey = loadAesKey(aes);
    }

    private static AESEncrypter INSTANCE;

    private static Map<String, AESEncrypter> INSTANCES = new HashMap<>();

    public static AESEncrypter getInstance() {
        if (INSTANCE == null) {
            synchronized (aesKeyStr) {
                if (INSTANCE == null) {
                    INSTANCE = new AESEncrypter();
                }
            }
        }
        return INSTANCE;
    }

    public static AESEncrypter getInstance(String aes) {
        if (INSTANCES.get(aes) == null) {
            synchronized (aesKeyStr) {
                if (INSTANCES.get(aes) == null) {
                    INSTANCES.put(aes, new AESEncrypter(aes));
                }
            }
        }
        return INSTANCES.get(aes);
    }

    public String encrypt(String msg) {
        try {
            Cipher ecipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return Encrypter.toHexString(ecipher.doFinal(msg.getBytes()));
        } catch (Exception e) {
            String errMsg = "decrypt error, data:" + msg;
            throw new RuntimeException(errMsg, e);
        }
    }

    public byte[] decrypt(String msg) {
        try {
            Cipher dcipher = Cipher.getInstance("AES");
            dcipher.init(Cipher.DECRYPT_MODE, aesKey);
            return dcipher.doFinal(Encrypter.toBytes(msg));
        } catch (Exception e) {
            String errMsg = "decrypt error, data:" + msg;
            throw new RuntimeException(errMsg, e);
        }
    }

    public String decryptAsString(String msg) {
        return new String(this.decrypt(msg));
    }

    private static SecretKey loadAesKey() {
        String buffer = new String(Base64.decodeBase64(aesKeyStr));
        byte[] keyStr = Encrypter.toBytes(buffer);
        SecretKeySpec aesKey = new SecretKeySpec(keyStr, "AES");
        return aesKey;
    }

    private static SecretKey loadAesKey(String aesKeyStr) {
        String buffer = new String(Base64.decodeBase64(aesKeyStr));
        byte[] keyStr = Encrypter.toBytes(buffer);
        SecretKeySpec aesKey = new SecretKeySpec(keyStr, "AES");
        return aesKey;
    }

}
