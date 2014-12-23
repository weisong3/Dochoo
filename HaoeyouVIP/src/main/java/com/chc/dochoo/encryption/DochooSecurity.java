package com.chc.dochoo.encryption;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provides default methods for encryption and decryption
 */
public abstract class DochooSecurity {
    static SecretKeySpec getSecretKeySpec(String key) {
        return new SecretKeySpec(key.getBytes(), "AES");
    }

    static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES");
    }
}
