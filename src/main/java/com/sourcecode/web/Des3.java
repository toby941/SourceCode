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

    public void des() {
        System.out.print("R_ONLINE :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTsAlr8d8U7mM="));
        System.out.print("R_LONGLAT :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTtAgNfSJrXwOfNsEYmx4x_w=="));
        System.out.print("R_LOCATION :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTtAgNfSJrXwMjlUd0zZ7BTAjldlM56kJ7"));
        System.out.print("R_AD :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTrDrSGf-LNX8="));
        System.out.print("R_BANNERSHOW :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQT_tuBtBw0dU7UW3rCFqLK0g=="));
        System.out.print("R_ADSHOW :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTnvQwvzqbC3oI5XZTOepCew=="));
        System.out.print("R_ADSHOWFINISHED :  ");
        System.out.println(Des3.des("84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTrgLfKsKBJDb-ECj5LVLgYw=="));
    }

    public static void ens() {
        System.out.print("HOST :  ");
        System.out.println(Des3.ens(HOST));
        System.out.print("ONLINE :  ");
        System.out.println(Des3.ens("/rest/online"));
        System.out.print("R_ONLINE :  ");
        System.out.println(Des3.ens(HOST + "/rest/online"));
        System.out.print("R_LONGLAT :  ");
        System.out.println(Des3.ens(HOST + "/rest/geolocation/ll"));
        System.out.print("R_LOCATION :  ");
        System.out.println(Des3.ens(HOST + "/rest/geolocation/pca"));
        System.out.print("R_AD :  ");
        System.out.println(Des3.ens(HOST + "/rest/ads"));
        System.out.print("R_BANNERSHOW :  ");
        System.out.println(Des3.ens(HOST + "/rest/banner/show"));
        System.out.print("R_ADSHOW :  ");
        System.out.println(Des3.ens(HOST + "/rest/ad/show"));
        System.out.print("R_ADSHOWFINISHED :  ");
        System.out.println(Des3.ens(HOST + "/rest/ad/finish"));
    }
    public static String HOST = "http://192.168.1.40";

    // public static String HOST="http://ad.airad.com";
    public static void main(String[] args) {
        ens();
        System.out.println(des("xJ4PXRs7JsC1w5IgDAT7MgYQHwKSk_Akl5AdCL2CRnnpLy0bAYcdIw=="));
    }
    // 通知上线
    protected static final String R_ONLINE = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTsAlr8d8U7mM=";
    // 提交SDK经纬度信息
    protected static final String R_LONGLAT = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTtAgNfSJrXwOfNsEYmx4x_w==";
    // 提交SDK地理位置
    protected static final String R_LOCATION = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTtAgNfSJrXwMjlUd0zZ7BTAjldlM56kJ7";
    // 请求广告ID列表
    protected static final String R_AD = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTrDrSGf-LNX8=";
    // Banner展示计数
    protected static final String R_BANNERSHOW = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQT_tuBtBw0dU7UW3rCFqLK0g==";
    // Ad载入完毕
    protected static final String R_ADSHOW = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTnvQwvzqbC3oI5XZTOepCew==";
    // Ad展示结束
    protected static final String R_ADSHOWFINISHED = "84bVlfSBjT9lcfYS_gsHucCI0i2VIZQTrgLfKsKBJDb-ECj5LVLgYw==";

    // // 通知上线
    // protected static final String R_ONLINE =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTIsAlr8d8U7mM=";
    // // 提交SDK经纬度信息
    // protected static final String R_LONGLAT =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTItAgNfSJrXwOfNsEYmx4x_w==";
    // // 提交SDK地理位置
    // protected static final String R_LOCATION =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTItAgNfSJrXwMjlUd0zZ7BTAjldlM56kJ7";
    // // 请求广告ID列表
    // protected static final String R_AD =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTIrDrSGf-LNX8=";
    // // Banner展示计数
    // protected static final String R_BANNERSHOW =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTI_tuBtBw0dU7UW3rCFqLK0g==";
    // // Ad载入完毕
    // protected static final String R_ADSHOW =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTInvQwvzqbC3oI5XZTOepCew==";
    // // Ad展示结束
    // protected static final String R_ADSHOWFINISHED =
    // "xJ4PXRs7JsC1w5IgDAT7Mth-nl88fsTIrgLfKsKBJDb-ECj5LVLgYw==";
}
