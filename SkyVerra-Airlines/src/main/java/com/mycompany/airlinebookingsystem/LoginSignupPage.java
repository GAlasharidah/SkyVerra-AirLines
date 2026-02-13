package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.*;

public class LoginSignupPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    private JCheckBox showPasswordCheckbox;
    private JLabel errorLabel;

    public LoginSignupPage() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initComponents() {
        setTitle("Skyverra - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        try {
            setContentPane(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/Map.jpg")))));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load background image.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try {
            BufferedImage original = ImageIO.read(new File("C:/Users/leena/Desktop/iii.jpg"));
            int size = 50;
            Image scaledImage = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);

            BufferedImage rounded = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = rounded.createGraphics();
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(scaledImage, 0, 0, null);
            g2.dispose();

            JLabel iconLabel = new JLabel(new ImageIcon(rounded));
            iconLabel.setBounds(530, 55, size, size);
            add(iconLabel);

            JLabel welcomeLabel = new JLabel("Welcome to Skyverra!");
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setBounds(590, 50, 600, 50);
            add(welcomeLabel);
        } catch (Exception ex) {
            System.out.println("❌ Icon failed to load: " + ex.getMessage());
        }

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setBounds(650, 230, 300, 20);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(650, 260, 300, 30);
        emailField.setToolTipText("Enter your email (example: user@gmail.com)");
        add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(650, 310, 300, 20);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(650, 340, 300, 30);
        passwordField.setToolTipText("Enter your password.");
        add(passwordField);

        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setBounds(650, 375, 150, 20);
        showPasswordCheckbox.setBackground(new Color(0, 0, 0, 0));
        showPasswordCheckbox.setForeground(Color.WHITE);
        showPasswordCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordCheckbox.setFocusPainted(false);
        showPasswordCheckbox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckbox.isSelected() ? (char) 0 : '*');
        });
        add(showPasswordCheckbox);

        errorLabel = new JLabel("");
        errorLabel.setBounds(650, 400, 400, 20);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(errorLabel);

        loginButton = new JButton("Log in");
        loginButton.setBounds(650, 440, 140, 40);
        loginButton.setBackground(Color.decode("#ADD8E6"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> loginAction());
        add(loginButton);

        signupButton = new JButton("Sign up");
        signupButton.setBounds(810, 440, 140, 40);
        signupButton.setBackground(Color.WHITE);
        signupButton.setForeground(Color.decode("#ADD8E6"));
        signupButton.setBorder(BorderFactory.createLineBorder(Color.decode("#ADD8E6")));
        signupButton.setFocusPainted(false);
        signupButton.addActionListener(e -> {
            dispose();
            new SignupPage();
        });
        add(signupButton);

        JLabel forgotLabel = new JLabel("Forgot password?");
        forgotLabel.setForeground(Color.WHITE);
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setBounds(710, 500, 120, 20);
        add(forgotLabel);

        setVisible(true);
    }

    private void loginAction() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in both email and password.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        if (!email.endsWith("@gmail.com") && !email.endsWith("@hotmail.com")) {
            errorLabel.setText("Only Gmail or Hotmail emails are accepted.");
            return;
        }

        if (password.length() < 6 || !password.matches(".*[A-Z].*") || !password.matches(".*[!@#$%^&*()_+=<>?/{}|\\[\\]~`].*")) {
            errorLabel.setText("Password must be at least 6 chars, 1 uppercase, 1 symbol.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            // 1. Check Admin
            PreparedStatement pstAdmin = conn.prepareStatement("SELECT admin_id FROM Admin WHERE email = ? AND password = ?");
            pstAdmin.setString(1, email);
            pstAdmin.setString(2, password);
            ResultSet rsAdmin = pstAdmin.executeQuery();

            if (rsAdmin.next()) {
                Session.currentUserId = rsAdmin.getString("admin_id");
                Session.currentRole = "admin";
                JOptionPane.showMessageDialog(this, "Welcome Admin!");
                dispose();
                new AdminDashboard(); // صفحة خاصة بالمسؤول
                return;
            }

            // 2. Check Customer
            PreparedStatement pstCustomer = conn.prepareStatement("SELECT customer_id FROM Customer WHERE email = ? AND password = ?");
            pstCustomer.setString(1, email);
            pstCustomer.setString(2, password);
            ResultSet rsCustomer = pstCustomer.executeQuery();
            
            if (rsCustomer.next()) {
              Session.currentUserId = rsCustomer.getString("customer_id");
              Session.currentRole = "customer";
              Session.currentName = rsCustomer.getString("first_name"); 

              JOptionPane.showMessageDialog(this, "Welcome, " + Session.currentName + "!");

             dispose();
              new MainMenu(); 
            } else {
             errorLabel.setText("Incorrect email or password.");
               }


        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginSignupPage();
    }
}
