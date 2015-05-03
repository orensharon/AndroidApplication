package com.example.orensharon.finalproject.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by orensharon on 5/1/15.
 */
public class Helper {

    public static Map<String, String> StringToMap(String str) {

        Map<String, String> myMap = new HashMap<String, String>();

        if (!str.equals("")) {
            String[] pairs = str.split(",");
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i];
                pair = pair.replace("{","").trim();
                pair = pair.replace("}","").trim();

                String[] keyValue = pair.split("=");
                myMap.put(keyValue[0], keyValue[1]);
            }
        }
        return myMap;
    }

    public static List<String> StringToList(String str) {

        ArrayList<String> result;
        String temp;


        result = new ArrayList<String>();

        if (!str.equals("")) {

            temp = str.replace("[","").trim();
            temp = temp.replace("]","").trim();

            String[] values = temp.split(",");
            for (int i = 0; i < values.length; i++) {

                if (values[i].trim().equals("")){
                    continue;
                }
                result.add(values[i].trim());
            }

        }

        return result;
    }
}
