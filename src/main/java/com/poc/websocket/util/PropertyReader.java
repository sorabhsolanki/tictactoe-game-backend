package com.poc.websocket.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * class for loading property from config.properties file
 */
public class PropertyReader {

    private static final Map<String, String> propertyMap = new HashMap<>();
    private static Properties prop = new Properties();

    public static void init() {
        readPropertyFile();
        loadPropertiesInCache();
        loadAdminAuthKey();
    }

    private static void readPropertyFile() {
        if (prop.isEmpty()) {
            InputStream input = PropertyReader.class.getClassLoader().getResourceAsStream("config.properties");
            try {
                prop.load(input);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    private static void loadPropertiesInCache() {
        Enumeration stringEnumeration = prop.propertyNames();
        while (stringEnumeration.hasMoreElements()) {
            String key = stringEnumeration.nextElement().toString();
            propertyMap.put(key.trim(), prop.getProperty(key).trim());
        }
    }

    private static void loadAdminAuthKey() {
        final String path = propertyMap.get("adminFile");
        try {
            final FileInputStream in = new FileInputStream(path);
            prop.load(in);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Enumeration stringEnumeration = prop.propertyNames();
        while (stringEnumeration.hasMoreElements()) {
            String key = stringEnumeration.nextElement().toString();
            propertyMap.put(key.trim(), prop.getProperty(key).trim());
        }
    }

    public static String getAuthKey() {
        return propertyMap.get("authKey");
    }
}
