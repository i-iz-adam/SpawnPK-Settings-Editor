package org.spawnpk;

public class SettingsEditor {
    public static void main(String[] args) {
        String filePath = System.getProperty("user.home") + "/.spawnpk-data/settings.properties";
        SettingsManager manager = new SettingsManager(filePath);
        new SettingsUI(manager);
    }
}
