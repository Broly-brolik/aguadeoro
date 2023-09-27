package com.aguadeoro.utils;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Functions {
    public static String readFromInputStream(String path)
            throws IOException {
        File orderEntryFile = new File(path);
        InputStream inputStream = new FileInputStream(orderEntryFile);

        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static String openAssetFile(String filename, Context context
    ) {
        try {
            InputStream assetFile = context.getAssets().open(filename);
            StringBuilder assetStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(assetFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    assetStringBuilder.append(line).append("\n");
                }
            }
            return assetStringBuilder.toString();
        } catch (IOException e) {
            return "";
        }
    }
}