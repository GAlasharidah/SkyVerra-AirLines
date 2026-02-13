package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageFlights extends JFrame {

    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JTextField flightNameField, departureField, destinationField, departureTimeField, arrivalTimeField, priceField, capacityField;
    private JButton addButton, updateButton, deleteButton, backButton, refreshButton;

    public ManageFlights() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadFlights();
    }

    private void initComponents() {
        setTitle("Manage Flights");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 240, 255));

        JLabel titleLabel = new JLabel("Manage Flights", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        flightsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(230, 240, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addFormField(formPanel, gbc, row++, "Flight Name:", flightNameField = new JTextField());
        addFormField(formPanel, gbc, row++, "Departure:", departureField = new JTextField());
        addFormField(formPanel, gbc, row++, "Destination:", destinationField = new JTextField());
        addFormField(formPanel, gbc, row++, "Departure Time (YYYY-MM-DD HH:MM:SS):", departureTimeField = new JTextField());
        addFormField(formPanel, gbc, row++, "Arrival Time (YYYY-MM-DD HH:MM:SS):", arrivalTimeField = new JTextField());
        addFormField(formPanel, gbc, row++, "Price:", priceField = new JTextField());
        addFormField(formPanel, gbc, row++, "Capacity:", capacityField = new JTextField());

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(new Color(230, 240, 255));

        addButton = createButton("Add Flight", buttonsPanel);
        updateButton = createButton("Update Flight", buttonsPanel);
        deleteButton = createButton("Delete Flight", buttonsPanel);
        refreshButton = createButton("Refresh", buttonsPanel);
        backButton = createButton("Back", buttonsPanel);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonsPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Event Listeners
        addButton.addActionListener(e -> addFlight());
        updateButton.addActionListener(e -> updateFlight());
        deleteButton.addActionListener(e -> deleteFlight());
        refreshButton.addActionListener(e -> loadFlights());
        backButton.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });

        flightsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = flightsTable.getSelectedRow();
                flightNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                departureField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                destinationField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                departureTimeField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                arrivalTimeField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                priceField.setText(tableModel.getValueAt(selectedRow, 6).toString());
                capacityField.setText(tableModel.getValueAt(selectedRow, 7).toString());
            }
        });

        setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(textField, gbc);
    }

    private JButton createButton(String text, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 230, 250));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        panel.add(button);
        return button;
    }

    private void loadFlights() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM flights";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            tableModel = buildTableModel(rs);
            flightsTable.setModel(tableModel);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flights!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFlight() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO flights (Flight_name, Departure, Destination, Departure_time, Arrival_time, Price, Capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, flightNameField.getText());
            pst.setString(2, departureField.getText());
            pst.setString(3, destinationField.getText());
            pst.setString(4, departureTimeField.getText());
            pst.setString(5, arrivalTimeField.getText());
            pst.setString(6, priceField.getText());
            pst.setString(7, capacityField.getText());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Flight added successfully!");
            ActivityLogs.log("Added flight: " + flightNameField.getText() + " from " + departureField.getText() + " to " + destinationField.getText());
            loadFlights();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE flights SET Flight_name = ?, Departure = ?, Destination = ?, Departure_time = ?, Arrival_time = ?, Price = ?, Capacity = ? WHERE Flight_no = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, flightNameField.getText());
            pst.setString(2, departureField.getText());
            pst.setString(3, destinationField.getText());
            pst.setString(4, departureTimeField.getText());
            pst.setString(5, arrivalTimeField.getText());
            pst.setString(6, priceField.getText());
            pst.setString(7, capacityField.getText());
            pst.setInt(8, flightId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Flight updated successfully!");
            ActivityLogs.log("Updated flight: " + flightNameField.getText() + " (ID: " + flightId + ")");
            loadFlights();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating flight!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this flight?", "Delete Flight", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int flightId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM flights WHERE Flight_no = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, flightId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Flight deleted successfully!");
            ActivityLogs.log("Deleted flight (ID: " + flightId + ")");
            loadFlights();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting flight!", "Error", JOptionPane.ERROR_MESSAGE);
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
        new ManageFlights();
    }
}
