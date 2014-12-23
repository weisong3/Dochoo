package com.chc.dochoo.encryption;

import com.chcgp.hpad.util.general.CHCGeneralUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by HenryW on 1/28/14.
 */
public abstract class DochooDecryptor extends DochooSecurity {

    /**
     * Decrypts a base64 encoded (URL safe variant) string using key
     * @param base64UrlSafeEncoded
     * @param key
     * @return new string
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static String decrypt(String base64UrlSafeEncoded, String key)
            throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return new String(decrypt(CHCGeneralUtil.decodeBase64FromUrlSafe(base64UrlSafeEncoded), key));
    }

    /**
     * Decrypts byte array using key
     * @param bytes
     * @param key
     * @return new byte array
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decrypt(byte[] bytes, String key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = getCipher();
        c.init(Cipher.DECRYPT_MODE, getSecretKeySpec(key));
        return c.doFinal(bytes);
    }

}
