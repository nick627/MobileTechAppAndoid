package com.example.a1.mobiletech4;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptoProvider {

    public String encryptMessage(String message, String key)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final byte[] keyBytes = key.getBytes();
        final byte[] messageBytes = message.getBytes();

        final byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CFB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        final byte[] resultBytes = (cipher.doFinal(messageBytes));
        return Base64.encodeToString(resultBytes, Base64.DEFAULT);
    }

    public String decryptMessage(String message, String key)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final byte[] keyBytes = key.getBytes();
        final byte[] encryptedBytes = message.getBytes();

        final byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/CFB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        
        final byte[] data = Base64.decode(encryptedBytes, Base64.DEFAULT);
        final byte[] resultBytes = cipher.doFinal(data);

        return new String(resultBytes);
    }

}
