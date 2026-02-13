/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddAdminPage extends JFrame {

    private JTextField firstNameField, middleNameField, lastNameField, emailField, phoneField;
    private JRadioButton maleRadio, femaleRadio;
    private ButtonGroup genderGroup;

    public AddAdminPage() {
        setTitle("Add New Admin");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 240, 255)); // خلفية هادئة

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 18);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 16);

        // العنوان
        JLabel titleLabel = new JLabel("Add New Admin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setBounds(150, 20, 400, 40);
        add(titleLabel);

        int xLabel = 400, xField = 520, width = 300, height = 30, y = 100, gap = 50;

        // First Name
        addLabel("First Name:", xLabel, y, labelFont);
        firstNameField = createTextField(xField, y, inputFont);
        add(firstNameField);

        // Middle Name
        y += gap;
        addLabel("Middle Name:", xLabel, y, labelFont);
        middleNameField = createTextField(xField, y, inputFont);
        add(middleNameField);

        // Last Name
        y += gap;
        addLabel("Last Name:", xLabel, y, labelFont);
        lastNameField = createTextField(xField, y, inputFont);
        add(lastNameField);

        // Email
        y += gap;
        addLabel("Email:", xLabel, y, labelFont);
        emailField = createTextField(xField, y, inputFont);
        add(emailField);

        // Phone
        y += gap;
        addLabel("Phone:", xLabel, y, labelFont);
        phoneField = createTextField(xField, y, inputFont);
        add(phoneField);

        // Gender
        y += gap;
        addLabel("Gender:", xLabel, y, labelFont);
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        maleRadio.setBounds(xField, y, 100, height);
        femaleRadio.setBounds(xField + 120, y, 100, height);
        maleRadio.setFont(inputFont);
        femaleRadio.setFont(inputFont);
        maleRadio.setBackground(getContentPane().getBackground());
        femaleRadio.setBackground(getContentPane().getBackground());
        add(maleRadio);
        add(femaleRadio);
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        // Save Button
        JButton saveButton = createStyledButton("Save");
        saveButton.setBounds(xField + 40, y + 70, 150, 40);
        add(saveButton);

        // Action for Save Button
        saveButton.addActionListener(e -> saveAdmin());

        setVisible(true);
    }

    private void addLabel(String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 120, 30);
        label.setFont(font);
        add(label);
    }

    private JTextField createTextField(int x, int y, Font font) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 300, 30);
        field.setFont(font);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(180, 210, 255));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 255), 2, true));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(160, 200, 255));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(180, 210, 255));
            }
        });

        return button;
    }

    private void saveAdmin() {
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "");

        if (firstName.isEmpty() || middleName.isEmpty() || lastName.isEmpty()
                || email.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!firstName.matches("[a-zA-Z]+") || !middleName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "Names should only contain letters!", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phone.matches("\\d{8,15}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be numeric and between 8-15 digits.", "Invalid Phone", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT * FROM admins WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email is already taken!", "Duplicate Email", JOptionPane.ERROR_MESSAGE);
                ActivityLogs.log("Attempted to add duplicate admin with email: " + email);
                return;
            }

            String insertQuery = "INSERT INTO admins (first_name, middle_name, last_name, email, phone, gender) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, firstName);
            insertStmt.setString(2, middleName);
            insertStmt.setString(3, lastName);
            insertStmt.setString(4, email);
            insertStmt.setString(5, phone);
            insertStmt.setString(6, gender);
            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            ActivityLogs.log("Added new admin: " + firstName + " " + lastName + " (" + email + ")");
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred!", "Error", JOptionPane.ERROR_MESSAGE);
            ActivityLogs.log("Database error while adding admin: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddAdminPage());
    }
}