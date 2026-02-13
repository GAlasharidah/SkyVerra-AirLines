package com.mycompany.airlinebookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Skyverra Airlines - Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);

        try {
            setContentPane(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/TicketImage.jpg")))));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Background image not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel navBar = new JPanel();
        navBar.setLayout(null);
        navBar.setBackground(new Color(255, 255, 255, 200));
        navBar.setBounds(0, 0, 1366, 50);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(navBar);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/SkyVerraIcon.jpg"));
            Image image = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(image));
            logoLabel.setBounds(7, 4, 42, 42);
            logoLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
            navBar.add(logoLabel);
        } catch (Exception e) {
            System.out.println("⚠️ Logo icon not found.");
        }

        JButton aboutUsBtn = new JButton("About Us");
        aboutUsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aboutUsBtn.setBounds(50, 10, 100, 30);
        styleNavButton(aboutUsBtn, "AboutUsPage");
        navBar.add(aboutUsBtn);

        JButton contactUsBtn = new JButton("Contact Us");
        contactUsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contactUsBtn.setBounds(160, 10, 110, 30);
        styleNavButton(contactUsBtn, "ContactUsPage");
        navBar.add(contactUsBtn);

        try {
            ImageIcon userIcon = new ImageIcon(getClass().getResource("/images/UserIcon.png"));
            Image scaledIcon = userIcon.getImage().getScaledInstance(42, 42, Image.SCALE_SMOOTH);
            JButton userButton = new JButton(new ImageIcon(scaledIcon));
            userButton.setBounds(1230, 4, 42, 42);
            userButton.setFocusPainted(false);
            userButton.setContentAreaFilled(false);
            userButton.setBorderPainted(false);
            userButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPopupMenu userMenu = new JPopupMenu();
            JMenuItem editProfileItem = new JMenuItem("Edit Profile");
            JMenuItem logoutItem = new JMenuItem("Logout");

            editProfileItem.addActionListener(e -> {
                dispose();
                try {
                    Class.forName("com.mycompany.airlinebookingsystem.EditProfile")
                          .getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to open EditProfile page.");
                }
            });

            logoutItem.addActionListener(e -> {
                dispose();
                try {
                    Class.forName("com.mycompany.airlinebookingsystem.LoginPage")
                          .getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to return to Login page.");
                }
            });

            userMenu.add(editProfileItem);
            userMenu.add(logoutItem);

            userButton.addActionListener(e -> userMenu.show(userButton, 0, userButton.getHeight()));
            navBar.add(userButton);

        } catch (Exception e) {
            System.out.println("⚠️ User icon not found.");
        }

        JLabel titleLabel = new JLabel("Skyverra Airlines");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 60, 600, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(titleLabel);

        JLabel welcomeLabel = new JLabel("Welcome, " + Session.currentName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(120, 110, 300, 30);
        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(welcomeLabel);

        JLabel sloganLabel = new JLabel("Your journey begins here!");
        sloganLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        sloganLabel.setForeground(Color.WHITE);
        sloganLabel.setBounds(120, 145, 600, 30);
        sloganLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(sloganLabel);

        addNavButton("Search Flights", 150, 200, "SearchFlightsPage");
        addNavButton("Book Flight", 150, 260, "BookingPaymentPage");
        addNavButton("My Tickets & Bookings", 150, 320, "TicketsPage");

        JPanel parisCard = createDestinationCard("Paris", "From 299 SAR", "C:/Users/leena/Desktop/paris.jpg", 40, 400);
        add(parisCard);

        JPanel tokyoCard = createDestinationCard("Tokyo", "From 399 SAR", "C:/Users/leena/Desktop/tokyo.jpg", 390, 400);
        add(tokyoCard);

        JPanel footer = new JPanel();
        footer.setLayout(null);
        footer.setBackground(new Color(255, 255, 255, 200));
        footer.setBounds(0, 610, 1366, 40);
        add(footer);

        JButton privacyBtn = new JButton("Privacy Policy");
        privacyBtn.setBounds(20, 10, 120, 20);
        styleFooterButton(privacyBtn);
        footer.add(privacyBtn);

        JButton termsBtn = new JButton("Terms");
        termsBtn.setBounds(150, 10, 80, 20);
        styleFooterButton(termsBtn);
        footer.add(termsBtn);

        JButton helpBtn = new JButton("Help");
        helpBtn.setBounds(240, 10, 80, 20);
        styleFooterButton(helpBtn);
        footer.add(helpBtn);

        JLabel copyrightLabel = new JLabel("© 2025 Skyverra Airlines");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.DARK_GRAY);
        copyrightLabel.setBounds(1080, 10, 250, 20);
        footer.add(copyrightLabel);

        setVisible(true);
    }

    private JPanel createDestinationCard(String city, String price, String imagePath, int x, int y) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, 320, 200);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));

        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(316, 110, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imageLabel.setBounds(2, 2, 316, 110);
            card.add(imageLabel);
        } catch (Exception e) {
            JLabel imageLabel = new JLabel("Image not found");
            imageLabel.setBounds(10, 10, 300, 110);
            card.add(imageLabel);
        }

        JLabel cityLabel = new JLabel(city + "  –  " + price);
        cityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cityLabel.setForeground(Color.DARK_GRAY);
        cityLabel.setBounds(10, 120, 300, 20);
        card.add(cityLabel);

        JButton bookBtn = new JButton("Book Now");
        bookBtn.setBounds(100, 150, 120, 30);
        bookBtn.setBackground(Color.WHITE);
        bookBtn.setForeground(Color.decode("#4682B4"));
        bookBtn.setFocusPainted(false);
        bookBtn.setBorder(BorderFactory.createLineBorder(Color.decode("#4682B4")));
        bookBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> {
            dispose();
            try {
                Class.forName("com.mycompany.airlinebookingsystem.BookingPaymentPage")
                      .getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to open BookingPaymentPage");
            }
        });
        card.add(bookBtn);
        return card;
    }

    private void addNavButton(String text, int x, int y, String pageClassName) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 320, 45);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        Color steelBlue = Color.decode("#4682B4");

        button.setBackground(Color.WHITE);
        button.setForeground(steelBlue);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(steelBlue, 2, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Click to " + text.toLowerCase());
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        button.addActionListener(e -> {
            dispose();
            try {
                Class.forName("com.mycompany.airlinebookingsystem." + pageClassName)
                        .getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to open " + pageClassName);
            }
        });

        add(button);
    }

    private void styleNavButton(JButton btn, String pageClassName) {
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });

        btn.addActionListener(e -> {
            dispose();
            try {
                Class.forName("com.mycompany.airlinebookingsystem." + pageClassName)
                        .getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to open " + pageClassName);
            }
        });
    }

    private void styleFooterButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 200)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
