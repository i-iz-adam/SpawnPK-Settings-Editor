package org.spawnpk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

enum ScreenMode {
    FIXED, RESIZABLE, FULL_SCREEN
}

class SettingsUI {
    private JFrame frame;
    private JPanel panel;
    private JTextField searchField;
    private JButton saveButton;
    private SettingsManager manager;
    private List<Component> valueFields = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    public SettingsUI(SettingsManager manager) {
        this.manager = manager;
        frame = new JFrame("Settings Editor");

        // Dark theme setup
        UIManager.put("Panel.background", new Color(30, 30, 30));
        UIManager.put("Label.foreground", new Color(200, 200, 200));
        UIManager.put("Button.background", new Color(50, 50, 50));
        UIManager.put("Button.foreground", new Color(200, 200, 200));
        UIManager.put("TextField.background", new Color(50, 50, 50));
        UIManager.put("TextField.foreground", new Color(200, 200, 200));
        UIManager.put("ComboBox.background", new Color(50, 50, 50));
        UIManager.put("ComboBox.foreground", new Color(200, 200, 200));
        UIManager.put("CheckBox.background", new Color(30, 30, 30));
        UIManager.put("CheckBox.foreground", new Color(200, 200, 200));

        panel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30)); // Dark background for scrolling area

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveSettings());

        searchField = new JTextField();
        searchField.setToolTipText("Search settings...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshSettings(searchField.getText().trim().toLowerCase());
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchField, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(saveButton, BorderLayout.SOUTH);

        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        loadSettings("");
    }

    private void loadSettings(String filter) {
        panel.removeAll();
        valueFields.clear();
        keys.clear();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10); // Minimal spacing

        for (String key : manager.getAllProperties().stringPropertyNames()) {
            if (!key.toLowerCase().contains(filter)) continue;

            Setting setting = manager.getSetting(key);
            JLabel label = new JLabel(formatSettingName(key));

            Component inputField;
            if (key.equals("screen_mode")) {
                JComboBox<ScreenMode> comboBox = new JComboBox<>(ScreenMode.values());
                try {
                    comboBox.setSelectedItem(ScreenMode.valueOf(setting.getValue().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    comboBox.setSelectedItem(ScreenMode.FIXED);
                }
                inputField = comboBox;
            } else if (isBoolean(setting.getValue())) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(Boolean.parseBoolean(setting.getValue()));
                inputField = checkBox;
            } else if (isInteger(setting.getValue())) {
                JTextField textField = new JTextField(setting.getValue());
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || textField.getText().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
                            e.consume();
                        }
                    }
                });
                inputField = textField;
            } else {
                continue;
            }

            gbc.gridx = 0;
            panel.add(label, gbc);

            gbc.gridx = 1;
            panel.add(inputField, gbc);

            gbc.gridy++; // Move to next row

            valueFields.add(inputField);
            keys.add(key);
        }

        panel.revalidate();
        panel.repaint();
    }

    private void refreshSettings(String filter) {
        loadSettings(filter);
    }

    private void saveSettings() {
        for (int i = 0; i < valueFields.size(); i++) {
            Component field = valueFields.get(i);
            String key = keys.get(i);

            if (field instanceof JCheckBox) {
                boolean value = ((JCheckBox) field).isSelected();
                manager.updateSetting(key, String.valueOf(value));
            } else if (field instanceof JTextField) {
                manager.updateSetting(key, ((JTextField) field).getText());
            } else if (field instanceof JComboBox) {
                @SuppressWarnings("unchecked")
                JComboBox<ScreenMode> comboBox = (JComboBox<ScreenMode>) field;
                manager.updateSetting(key, ((ScreenMode) comboBox.getSelectedItem()).name());
            }
        }
        manager.saveProperties();
        JOptionPane.showMessageDialog(frame, "Settings saved successfully!");
    }

    private boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String formatSettingName(String key) {
        String[] words = key.split("_|\\s+");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return formatted.toString().trim();
    }
}
