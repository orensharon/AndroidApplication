package com.example.orensharon.finalproject.utils;

import java.util.Hashtable;

/**
 * Created by orensharon on 12/1/14.
 * This class contains a number of help methods which might will be needed
 * In the whole project classes
 */
public class HelpMethods {

    public static Hashtable<String,Boolean> StringToHashTable(String msg) {

        // From given string returns an hash table with keys and boolean values for each key

        Hashtable<String,Boolean> hash = new Hashtable<String, Boolean>();

        if (msg == null) {

            // No message received - returns an empty hash table
            return new Hashtable<String, Boolean>();
        }

        // Removing trailing brackets and split the string
        msg = msg.replace("{","").replace("}","");
        String[] items = msg.split(",");

        for (String item : items) {
            String contentName = item.split("=")[0].trim();
            String contentValue = item.split("=")[1].trim();

            // Put pair to hash table
            hash.put(contentName,Boolean.valueOf(contentValue));
        }

        // Return the hash table
        return hash;
    }
}
