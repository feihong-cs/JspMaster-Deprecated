package com.feihong.util;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;


public class EncryptUtil {
    public static String encrypt(final String plaintext, byte[] encryptKey, byte[] ivs) {
        try {
            // new SecretKeySpec 中的 byte[] 的 length 必须是 16或者24或者32， 否则会抛 InvalidKeyException 异常
            SecretKey key = new SecretKeySpec(encryptKey, "AES");
            // IV的长度必须和 BlockSize 一致（在这里 byte[] 的 length 应该为16），否则会抛 InvalidAlgorithmParameterException 异常
            AlgorithmParameterSpec iv = new IvParameterSpec(ivs);
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] result = cipher.doFinal(plaintext.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(final String encrypted, byte[] encryptKey, byte[] ivs) {
        try {
            SecretKey key = new SecretKeySpec(encryptKey, "AES");
            AlgorithmParameterSpec iv = new IvParameterSpec(ivs);
            byte[] decodeBase64 = Base64.getDecoder().decode(encrypted);
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(decodeBase64), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void encryptFile(String filename, String key) {
        try {
            File source = new File(filename);
            File dest = new File(filename + ".lock");

            if(source.exists()) {
                //删除原来的 .lock 文件，创建一个新的 .lock 文件
                if(dest.exists()){
                    boolean flag = dest.delete();
                }
                dest.createNewFile();

                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(dest);

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] b = key.getBytes();
                md.update(b);
                byte[] secret = md.digest();

                SecretKeySpec secretKeySpec = new SecretKeySpec(secret, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                CipherInputStream cin = new CipherInputStream(in, cipher);
                byte[] cache = new byte[1024];
                int nRead = 0;
                while ((nRead = cin.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                out.close();
                cin.close();
                in.close();

                source.delete();
            }else{
                if(dest.exists()){
                    // do nothing
                }else{
                    throw new Exception(filename + " 以及 " + filename + ".lock 文件均不存在!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(String filename, String key){
        FileInputStream in = null;
        FileOutputStream out = null;
        CipherOutputStream cout = null;
        try {
            File source = new File(filename + ".lock");

            if(source.exists()) {
                in = new FileInputStream(source);
                out = new FileOutputStream(filename);

                //计算key
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] b = key.getBytes();
                md.update(b);
                byte[] secret = md.digest();

                SecretKeySpec secretKeySpec = new SecretKeySpec(secret, "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                cout = new CipherOutputStream(out, cipher);
                byte[] cache = new byte[1024];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    cout.write(cache, 0, nRead);
                    cout.flush();
                }
                cout.close();
                out.close();
                in.close();

                try{
                    Runtime.getRuntime().exec("attrib +H " + new File(filename).getAbsolutePath());
                }catch(Exception e){
                    //do nothing
                }

            }else{
                throw new Exception("lock文件不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
