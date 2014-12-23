package com.chc.dochoo.encryption;

import com.chcgp.hpad.util.general.CHCGeneralUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Encrypts
 */
public abstract class DochooEncryptor extends DochooSecurity {

    /**
     * Encrypts byte array with given key using AES algorithm
     * @param bytes
     * @param key
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encrypt(byte[] bytes, String key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        // Encode the original data with AES
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(key));
        return c.doFinal(bytes);
    }

    /**
     * Encrypts some string into Base64 (URL safe variant) string using key
     * @param content
     * @param key
     * @return new Base64 URL safe encrypted and encoded string
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static String encrypt(String content, String key)
            throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return CHCGeneralUtil.encodeUrlSafe64(encrypt(content.getBytes(), key));
    }

}
