package org.spawnpk;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsLoader {
    public static Map<String, Setting> loadSettings() {
        Properties properties = new Properties();
        Map<String, Setting> settingsMap = new HashMap<>();

        String filePath = System.getProperty("user.home") + "/.spawnpk-data/settings.properties";
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);

            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                String title = key.replace("_", " "); // Simple title generation
                String description = "Description for " + title; // Placeholder description

                settingsMap.put(key, new Setting(key, title, description, value));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return settingsMap;
    }
}

