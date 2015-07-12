package com.example.orensharon.finalproject.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

/**
 * Created by orensharon on 12/1/14.
 * This class contains a number of help methods which might will be needed
 * In the whole project classes
 */
public class MD5Checksum {

    public final static String MD5 = "MD5";

    // This helper will used after the uploading of each content.
    // The purpose of the method is to verify the integrity of the uploaded file
    public static String getMd5Hash(byte[] input) {
        try     {
            MessageDigest md = MessageDigest.getInstance(MD5);
            md.update(input);
            return getMd5Hash(md);
        } catch(NoSuchAlgorithmException e) {
            Log.e("MD5", e.getMessage());
            return null;
        }
    }

    public static String getMd5Hash(String string) {
        return getMd5Hash(string.getBytes());
    }

    public static String getMd5Hash(MessageDigest digest) {
        BigInteger number = new BigInteger(1, digest.digest());
        String md5 = number.toString(16);

        // need to pad the hash
        while (md5.length() < 32)
            md5 = "0" + md5;

        return md5;
    }

    public static String getMd5HashFromFilePath(String path) {

        File f = new File(path);
        return getMd5Hash(f);
    }

    public static String getMd5Hash(File file) {

        FileInputStream is = null;

        try {
            is = new FileInputStream(file);
            byte[] buffer = new byte[8192];

            int read = 0;
            try {
                MessageDigest digest = MessageDigest.getInstance(MD5);
                while( (read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                return getMd5Hash(digest).toUpperCase();
            } catch(IOException e) {
                throw new RuntimeException("Unable to process file for MD5", e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Unable to process file for MD5", e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
