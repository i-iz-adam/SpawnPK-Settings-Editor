package org.spawnpk;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsManager {
    private Properties properties;
    private String filePath;

    public SettingsManager(String filePath) {
        this.filePath = filePath;
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Setting getSetting(String key) {
        return new Setting(key, key.replace("_", " "), "", properties.getProperty(key, ""));
    }

    public void updateSetting(String key, String value) {
        properties.setProperty(key, value);
    }

    public void saveProperties() {
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "Game Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getAllProperties() {
        return properties;
    }
}

