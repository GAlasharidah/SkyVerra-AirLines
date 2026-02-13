package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

public class BookingPage extends JFrame {
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JButton paymentButton, backButton;
    private JSpinner adultSpinner, childSpinner;
    private JPanel seatPanel, rightPanel;
    private JLabel totalPriceLabel, classSummaryLabel, seatSummaryLabel;
    private List<JComboBox<String>> seatBoxes = new ArrayList<>();
    private List<JComboBox<String>> classBoxes = new ArrayList<>();
    private List<String> availableSeats = new ArrayList<>();
    private final String[] allSeats = generateSeatList();

    public BookingPage() {
        setTitle("Skyverra - Book Flight");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);

        try {
            setContentPane(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/AirPlaneSky.jpg")))));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to load background image.");
        }

        initComponents();
        loadFlights();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Book Your Flight");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setBounds(60, 30, 400, 40);
        add(titleLabel);

        flightsTable = new JTable();
        flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightsTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBounds(60, 90, 1100, 200);
        add(scrollPane);

        JLabel seatMessage = new JLabel("🎟️ Choose your seat here — may you have a wonderful flight!");
        seatMessage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        seatMessage.setBounds(60, 300, 700, 30);
        add(seatMessage);

        seatPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        seatPanel.setBounds(60, 340, 700, 400);
        seatPanel.setOpaque(false);
        add(seatPanel);

        rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setOpaque(false);
        rightPanel.setBounds(800, 340, 400, 250);
        add(rightPanel);

        JLabel adultLabel = createLabel("Adult Tickets:", 10, 10);
        adultSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
        adultSpinner.setBounds(160, 10, 60, 30);
        rightPanel.add(adultLabel);
        rightPanel.add(adultSpinner);

        JLabel childLabel = createLabel("Child Tickets (Age 1–14):", 10, 50);
        childSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        childSpinner.setBounds(240, 50, 60, 30);
        rightPanel.add(childLabel);
        rightPanel.add(childSpinner);

        totalPriceLabel = new JLabel("Total: 0.00 SAR");
        totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalPriceLabel.setBounds(10, 100, 300, 30);
        rightPanel.add(totalPriceLabel);

        paymentButton = createButton("Proceed to Payment", 10, 150);
        backButton = createButton("Back to Menu", 210, 150);
        rightPanel.add(paymentButton);
        rightPanel.add(backButton);

        classSummaryLabel = new JLabel();
        seatSummaryLabel = new JLabel();

        adultSpinner.addChangeListener(e -> loadAvailableSeatsAndGenerateBoxes());
        childSpinner.addChangeListener(e -> loadAvailableSeatsAndGenerateBoxes());
        flightsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadAvailableSeatsAndGenerateBoxes();
            }
        });

        paymentButton.addActionListener(e -> proceedToPayment());
        backButton.addActionListener(e -> {
            new MainMenu().setVisible(true);
            dispose();
        });

        setVisible(true);
    }

    private void loadFlights() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT flight_no, flight_name, from_location, destination, departure_time, arrival_time, price FROM Flight";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            tableModel = buildReadOnlyTableModel(rs);
            flightsTable.setModel(tableModel);

            if (tableModel.getRowCount() > 0) {
                flightsTable.setRowSelectionInterval(0, 0);
                loadAvailableSeatsAndGenerateBoxes();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error loading flights.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableSeatsAndGenerateBoxes() {
        seatPanel.removeAll();
        seatBoxes.clear();
        classBoxes.clear();
        availableSeats.clear();

        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) return;

        String flightNo = tableModel.getValueAt(selectedRow, 0).toString();
        Set<String> bookedSeats = new HashSet<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT t.Seat_no FROM Ticket t JOIN Booking b ON t.booking_id = b.booking_id WHERE b.flight_no = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, flightNo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                bookedSeats.add(rs.getString("Seat_no"));
            }

            for (String seat : allSeats) {
                if (!bookedSeats.contains(seat)) availableSeats.add(seat);
            }

            int totalTickets = (int) adultSpinner.getValue() + (int) childSpinner.getValue();
            if (availableSeats.size() < totalTickets) {
                JOptionPane.showMessageDialog(this, "❌ Not enough seats available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Dimension cardSize = new Dimension(320, 80);

            for (int i = 0; i < totalTickets; i++) {
                JComboBox<String> seatBox = new JComboBox<>(availableSeats.toArray(new String[0]));
                JComboBox<String> classBox = new JComboBox<>(new String[]{"Economic", "Business", "First Class"});
                seatBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                classBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                seatBox.setToolTipText("Select seat");
                classBox.setToolTipText("Select class for this ticket");
                seatBoxes.add(seatBox);
                classBoxes.add(classBox);

                JLabel seatLabel = new JLabel("Seat " + (i + 1) + ":");
                seatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                JPanel miniCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
                miniCard.setPreferredSize(cardSize);
                miniCard.setPreferredSize(cardSize);
                miniCard.setMinimumSize(cardSize);
                miniCard.setMaximumSize(cardSize);

                miniCard.setBackground(Color.WHITE);
                miniCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
                miniCard.add(seatLabel);
                miniCard.add(seatBox);
                miniCard.add(new JLabel("  Class: "));
                miniCard.add(classBox);

                seatPanel.add(miniCard);
            }

            seatPanel.revalidate();
            seatPanel.repaint();
            updateTotalPrice();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Failed to load seats.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotalPrice() {
        if (flightsTable.getSelectedRow() == -1) return;

        double basePrice = Double.parseDouble(tableModel.getValueAt(flightsTable.getSelectedRow(), 6).toString());
        int adultCount = (int) adultSpinner.getValue();

        double total = 0.0;
        for (int i = 0; i < seatBoxes.size(); i++) {
            boolean isChild = i >= adultCount;
            String seatClass = (String) classBoxes.get(i).getSelectedItem();
            double addon = seatClass.equals("Business") ? 100 : seatClass.equals("First Class") ? 250 : 0;
            double price = basePrice + addon;
            if (isChild) price *= 0.5;
            total += price;
        }

        totalPriceLabel.setText("Total: " + String.format("%.2f", total) + " SAR");
    }

    private void proceedToPayment() {
        if (flightsTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<String> selectedSeats = new HashSet<>();
        StringBuilder seatList = new StringBuilder();
        StringBuilder classList = new StringBuilder();

        for (int i = 0; i < seatBoxes.size(); i++) {
            String seat = (String) seatBoxes.get(i).getSelectedItem();
            String seatClass = (String) classBoxes.get(i).getSelectedItem();

            if (seat == null || seat.trim().isEmpty() || seatClass == null) {
                JOptionPane.showMessageDialog(this, "Please select all seats and their classes.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedSeats.contains(seat)) {
                JOptionPane.showMessageDialog(this, "Duplicate seat selected: " + seat, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            selectedSeats.add(seat);
            seatList.append(seat).append(", ");
            classList.append(seat).append(" - ").append(seatClass).append(", ");
        }

        int row = flightsTable.getSelectedRow();
        String flightNo = tableModel.getValueAt(row, 0).toString();
        String basePrice = tableModel.getValueAt(row, 6).toString();

        seatSummaryLabel.setText("Seats: " + seatList.toString().replaceAll(", $", ""));
        classSummaryLabel.setText("Class Summary: " + classList.toString().replaceAll(", $", ""));

        new PaymentPage(
            flightNo,
            totalPriceLabel.getText().replace("Total: ", "").replace(" SAR", ""),
            seatSummaryLabel.getText().replace("Seats: ", ""),
            classSummaryLabel.getText().replace("Class Summary: ", ""),
            basePrice
        ).setVisible(true);
        dispose();
    }

    private DefaultTableModel buildReadOnlyTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) columnNames[i - 1] = metaData.getColumnName(i);
        Vector<String[]> data = new Vector<>();
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) row[i - 1] = rs.getString(i);
            data.add(row);
        }
        return new DefaultTableModel(data.toArray(new String[0][]), columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setBounds(x, y, 240, 30);
        return label;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBounds(x, y, 180, 40);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(new Color(210, 225, 250)); }
            public void mouseExited(MouseEvent e) { button.setBackground(Color.WHITE); }
        });
        return button;
    }

    private static String[] generateSeatList() {
        String[] rows = {"A", "B", "C", "D"};
        List<String> seats = new ArrayList<>();
        for (String row : rows) for (int i = 1; i <= 10; i++) seats.add(row + i);
        return seats.toArray(new String[0]);
    }

    public static void main(String[] args) {
        new BookingPage();
    }
}
