package com.sourcecode.web;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

class Des3 {

    protected static String des(String data) {
        String result = null;
        try {
            byte[] en = ees3DecodeECB("d50d2de4fd5ac018e9c1683ae5eff41f".getBytes(), Base64.decode(data.replace("-", "+").replace("_", "/")));

            result = new String(en, "UTF-8");
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    protected static String ens(String data) {
        String result = null;
        try {
            byte[] en = des3EncodeECB("d50d2de4fd5ac018e9c1683ae5eff41f".getBytes(), data.getBytes());
            result = new String(Base64.encode(en));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    protected static byte[] des3EncodeECB(byte[] key, byte[] data) {
        byte[] bOut = null;
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            bOut = cipher.doFinal(data);
        } catch (IllegalBlockSizeException e) {
            bOut = null;
        } catch (InvalidKeyException e) {
            bOut = null;
        } catch (NoSuchAlgorithmException e) {
            bOut = null;
        } catch (InvalidKeySpecException e) {
            bOut = null;
        } catch (BadPaddingException e) {
            bOut = null;
        } catch (NoSuchPaddingException e) {
            bOut = null;
        }
        return bOut;
    }

    protected static byte[] ees3DecodeECB(byte[] key, byte[] data) {
        byte[] bOut = null;
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            bOut = cipher.doFinal(data);
        } catch (IllegalBlockSizeException e) {
            bOut = null;
        } catch (InvalidKeyException e) {
            bOut = null;
        } catch (NoSuchAlgorithmException e) {
            bOut = null;
        } catch (InvalidKeySpecException e) {
            bOut = null;
        } catch (BadPaddingException e) {
            bOut = null;
        } catch (NoSuchPaddingException e) {
            bOut = null;
        }
        return bOut;
    }

    public static void main(String[] args) {
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTrgLfKsKBJDb-ECj5LVLgYw=="));
    }

}
