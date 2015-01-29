package com.example.orensharon.finalproject;

import java.util.Hashtable;

/**
 * Created by orensharon on 12/1/14.
 */
public class HelpMethods {

    public static Hashtable<String,Boolean> StringToHashTable(String msg) {
        Hashtable<String,Boolean> hash = new Hashtable<String, Boolean>();

        if (msg == null) {
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


        return hash;
    }
}
