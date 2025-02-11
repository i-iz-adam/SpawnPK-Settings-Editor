package org.spawnpk;

public class Setting {
    private String key;
    private String title;
    private String description;
    private String value;

    public Setting(String key, String title, String description, String value) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.value = value;
    }

    // Getters and setters
    public String getKey() { return key; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}

