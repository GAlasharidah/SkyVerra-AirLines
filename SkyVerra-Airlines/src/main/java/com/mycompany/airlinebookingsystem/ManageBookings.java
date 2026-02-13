package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageBookings extends JFrame {

    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JTextField flightIdField, passengerNameField, seatNumberField;
    private JButton updateButton, deleteButton, refreshButton, backButton;

    public ManageBookings() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadBookings();
    }

    private void initComponents() {
        setTitle("Manage Bookings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 240, 255));

        JLabel titleLabel = new JLabel("Manage Bookings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setBounds(450, 30, 600, 50);
        add(titleLabel);

        bookingsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBounds(50, 100, 1000, 300);
        add(scrollPane);

        int xLabel = 50, xField = 250, y = 420, gap = 60;

        JLabel flightIdLabel = createLabel("Flight ID:", xLabel, y);
        flightIdField = createTextField(xField, y);

        JLabel passengerNameLabel = createLabel("Passenger Name:", xLabel, y + gap);
        passengerNameField = createTextField(xField, y + gap);

        JLabel seatNumberLabel = createLabel("Seat Number:", xLabel, y + 2 * gap);
        seatNumberField = createTextField(xField, y + 2 * gap);

        add(flightIdLabel); add(flightIdField);
        add(passengerNameLabel); add(passengerNameField);
        add(seatNumberLabel); add(seatNumberField);

        updateButton = createButton("Update Booking", 500, y);
        deleteButton = createButton("Delete Booking", 500, y + gap);
        refreshButton = createButton("Refresh", 500, y + 2 * gap);
        backButton = createButton("Back to Dashboard", 500, y + 3 * gap);

        add(updateButton); add(deleteButton); add(refreshButton); add(backButton);

        updateButton.addActionListener(e -> updateBooking());
        deleteButton.addActionListener(e -> deleteBooking());
        refreshButton.addActionListener(e -> loadBookings());
        backButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });

        bookingsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = bookingsTable.getSelectedRow();
                flightIdField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                passengerNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                seatNumberField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });

        setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setBounds(x, y, 180, 30);
        return label;
    }

    private JTextField createTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setBounds(x, y, 200, 30);
        return field;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBounds(x, y, 220, 40);
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

    private void loadBookings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM booking";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            tableModel = buildTableModel(rs);
            bookingsTable.setModel(tableModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String flightId = flightIdField.getText().trim();
        String passengerName = passengerNameField.getText().trim();
        String seatNumber = seatNumberField.getText().trim();

        if (flightId.isEmpty() || passengerName.isEmpty() || seatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE booking SET Flight_ID = ?, Passenger_name = ?, Seat_number = ? WHERE Booking_ID = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, flightId);
            pst.setString(2, passengerName);
            pst.setString(3, seatNumber);
            pst.setInt(4, bookingId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Booking updated successfully!");
            ActivityLogs.log("Updated booking ID: " + bookingId);
            loadBookings();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this booking?", "Delete Booking", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int bookingId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String deletePaymentQuery = "DELETE FROM payment WHERE Booking_ID = ?";
            PreparedStatement paymentStmt = conn.prepareStatement(deletePaymentQuery);
            paymentStmt.setInt(1, bookingId);
            paymentStmt.executeUpdate();

            String deleteBookingQuery = "DELETE FROM booking WHERE Booking_ID = ?";
            PreparedStatement bookingStmt = conn.prepareStatement(deleteBookingQuery);
            bookingStmt.setInt(1, bookingId);
            bookingStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking deleted successfully!");
            ActivityLogs.log("Deleted booking ID: " + bookingId);
            loadBookings();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];

        for (int column = 1; column <= columnCount; column++) {
            columnNames[column - 1] = metaData.getColumnName(column);
        }

        java.util.Vector<String[]> data = new java.util.Vector<>();
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getString(i);
            }
            data.add(row);
        }

        String[][] dataArray = new String[data.size()][];
        data.toArray(dataArray);

        return new DefaultTableModel(dataArray, columnNames);
    }

    public static void main(String[] args) {
        new ManageBookings();
    }
}