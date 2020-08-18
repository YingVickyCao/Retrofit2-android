package com.hades.example.retrofit2.android;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHATool {
    public static String ALGORITHM_NAME_SHA256 = "SHA-256";
    public static String ALGORITHM_NAME_SH1 = "SHA-1";

    private String mAlgorithmName = ALGORITHM_NAME_SHA256;
    private static volatile SHATool mInstance;

    public static SHATool getInstance() {
        if (null == mInstance) {
            synchronized (SHATool.class) {
                if (null == mInstance) {
                    mInstance = new SHATool();
                }
            }
        }
        return mInstance;
    }

    private SHATool() {
    }

    public SHATool setAlgorithmName(String algorithmName) {
        mAlgorithmName = algorithmName;
        return this;
    }

    public byte[] digest_string2bytes(String originalString) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(mAlgorithmName);
            return digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String digest_string2string(String originalString) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(mAlgorithmName);
            byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
//            return bytesToHex2(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] digest_bytes2bytes(byte[] src) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(mAlgorithmName);
            return digest.digest(src);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String digest_bytes2String(byte[] src) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(mAlgorithmName);
            byte[] desc = digest.digest(src);
            return bytesToHex(desc);
//            return bytesToHex2(desc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

//    private String bytesToHex2(byte[] hash) {
//        return DatatypeConverter.printHexBinary(hash);
//    }
}
