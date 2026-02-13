package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); 
    }

    private void initComponents() {
        setTitle("Airline Admin Dashboard"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 240, 255)); // لون الخلفية بنات هذا

        // شعار الشركة
        ImageIcon logoIcon = new ImageIcon("logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(50, 20, 100, 100);
        add(logoLabel);

        // اسم الشركة
        JLabel companyNameLabel = new JLabel("Skyverra Airlines Admin Panel");
        companyNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        companyNameLabel.setForeground(Color.BLACK);
        companyNameLabel.setBounds(180, 40, 600, 50);
        add(companyNameLabel);

        // رسالة الترحيب
        JLabel welcomeLabel = new JLabel("Welcome, Admin!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBounds(180, 90, 300, 30);
        add(welcomeLabel);

        // أزرار الإدارة
        JButton flightsButton = createButton("✈️  Manage Flights", 250, "View, add, or edit flight details.");
        JButton bookingsButton = createButton("🧾  Manage Bookings", 330, "Review or manage customer bookings.");
        JButton addAdminButton = createButton("➕  Add New Admin", 410, "Register a new administrator.");
        JButton logsButton = createButton("📜  View Activity Logs", 490, "Check admin activity logs.");
        JButton logoutButton = createButton("Logout", 570, "Sign out and return to login screen.");

        add(flightsButton);
        add(bookingsButton);
        add(addAdminButton);
        add(logsButton);
        add(logoutButton);

        // أحداث الأزرار
        flightsButton.addActionListener(e -> new ManageFlights());
        
        bookingsButton.addActionListener(e -> new ManageBookings());
        
        addAdminButton.addActionListener(e -> new AddAdminPage());
        
        logsButton.addActionListener(e -> new ActivityLogs());
        
        logoutButton.addActionListener(e -> {
           new LoginSignupPage().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    // زر   + Tooltip
    private JButton createButton(String text, int yPosition, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        button.setBounds(500, yPosition, 400, 50);
        button.setBackground(new Color(180, 210, 255)); // لون أزرق سماوي هادئ
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 255), 2, true));
        button.setToolTipText(tooltip);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(160, 200, 255));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(180, 210, 255));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
    }
