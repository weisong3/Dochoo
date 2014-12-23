package com.chcgp.hpad.util.network;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaPasswordEncoder {
	
	private ShaPasswordEncoder() {
	}

	public static String encodePassword(String rawPass, Object salt) {
		
        String saltedPass = mergePasswordAndSalt(rawPass, salt, false);

        MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [SHA-1]");
        }

        byte[] digest;

        try {
            digest = messageDigest.digest(saltedPass.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }

        return new String(encodeToHEX(digest));
    }

	private static String mergePasswordAndSalt(String password, Object salt, boolean strict) {
        if (password == null) {
            password = "";
        }

        if (strict && (salt != null)) {
            if ((salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1)) {
                throw new IllegalArgumentException("Cannot use { or } in salt.toString()");
            }
        }

        if ((salt == null) || "".equals(salt)) {
            return password;
        } else {
            return password + "{" + salt.toString() + "}";
        }
    }
	
    private static final char[] HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static char[] encodeToHEX(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2*nBytes];

        int j = 0;
        for (int i=0; i < nBytes; i++) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4 ];
            // Bottom 4
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return result;
    }
}
