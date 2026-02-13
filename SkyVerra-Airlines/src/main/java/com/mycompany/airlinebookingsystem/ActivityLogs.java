/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogs extends JFrame {

    private static final List<String> logs = new ArrayList<>();

    // دالة لتسجيل الأحداث
    public static void log(String message) {
        String timestamp = java.time.LocalDateTime.now().withNano(0).toString().replace('T', ' ');
        logs.add("[" + timestamp + "] " + message);
    }

    public ActivityLogs() {
        setTitle("Activity Logs");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(230, 240, 255));
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Admin Activity Logs", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        JTextArea logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logsArea.setBackground(Color.WHITE);
        logsArea.setMargin(new Insets(10, 10, 10, 10));

        StringBuilder logText = new StringBuilder();
        for (String log : logs) {
            logText.append(log).append("\n");
        }

        logsArea.setText(logText.toString());
        add(new JScrollPane(logsArea), BorderLayout.CENTER);

        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(230, 240, 255));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        log("Viewed Activity Logs");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(180, 210, 255));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 255), 2, true));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(160, 200, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 210, 255));
            }
        });

        return button;
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ActivityLogs());
}}