package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;

public class PaymentPage extends JFrame {

    private String flightNo, passengerName, seatNumber, seatClass, basePrice;

    private JTextField cardNameField, cardNumberField, expiryField, cvvField;
    private JButton payButton, backButton;

    public PaymentPage(String flightNo, String passengerName, String seatNumber, String seatClass, String basePrice) {
        this.flightNo = flightNo;
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.basePrice = basePrice;

        setTitle("Skyverra - Payment");
        setSize(700, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(230, 240, 255));

        initComponents();
    }

    private void initComponents() {
        JLabel title = new JLabel("Complete Your Payment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(200, 20, 400, 40);
        add(title);

        JLabel summaryLabel = new JLabel("<html><b>Passenger:</b> " + passengerName +
                "<br><b>Seat No:</b> " + seatNumber +
                "<br><b>Class:</b> " + seatClass +
                "<br><b>Flight No:</b> " + flightNo + "</html>");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        summaryLabel.setBounds(50, 80, 400, 100);
        add(summaryLabel);

        JLabel nameLabel = createLabel("Name on Card:", 50, 200);
        cardNameField = createField(250, 200);

        JLabel numberLabel = createLabel("Card Number:", 50, 240);
        cardNumberField = createField(250, 240);

        JLabel expiryLabel = createLabel("Expiry Date (MM/YY):", 50, 280);
        expiryField = createField(250, 280);

        JLabel cvvLabel = createLabel("CVV:", 50, 320);
        cvvField = createField(250, 320);

        payButton = createButton("Confirm Payment", 100, 380);
        backButton = createButton("Back", 350, 380);

        add(nameLabel); add(cardNameField);
        add(numberLabel); add(cardNumberField);
        add(expiryLabel); add(expiryField);
        add(cvvLabel); add(cvvField);
        add(payButton); add(backButton);

        payButton.addActionListener(e -> processPayment());
        backButton.addActionListener(e -> {
            new BookingPage().setVisible(true);
            dispose();
        });
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setBounds(x, y, 200, 30);
        return label;
    }

    private JTextField createField(int x, int y) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBounds(x, y, 250, 30);
        add(field);
        return field;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBounds(x, y, 180, 40);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(210, 225, 250));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private void processPayment() {
        String cardName = cardNameField.getText().trim();
        String cardNumber = cardNumberField.getText().trim();
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (cardName.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Card number must be 16 digits.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cvv.length() != 3 || !cvv.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "CVV must be 3 digits.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ✅ حساب السعر النهائي
        BigDecimal price = new BigDecimal(basePrice);
        if (seatClass.equals("Business")) {
            price = price.add(new BigDecimal("100.00"));
        } else if (seatClass.equals("First Class")) {
            price = price.add(new BigDecimal("250.00"));
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            int bookingId = getLatestBookingId(conn);

            if (bookingId == -1) {
                JOptionPane.showMessageDialog(this, "No booking found to attach payment.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 🧾 إدخال الدفع
            String insert = "INSERT INTO Payment (Booking_id, Amount, Payment_Method) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insert);
            stmt.setInt(1, bookingId);
            stmt.setBigDecimal(2, price);
            stmt.setString(3, "Credit Card");
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Payment Successful! Your ticket is confirmed.");
            new MainMenu().setVisible(true);
            dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Payment failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getLatestBookingId(Connection conn) throws SQLException {
        String sql = "SELECT Booking_id FROM Booking WHERE Cus_id = ? ORDER BY Booking_id DESC LIMIT 1";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, Session.currentUserId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) return rs.getInt("Booking_id");
        return -1;
    }
}
