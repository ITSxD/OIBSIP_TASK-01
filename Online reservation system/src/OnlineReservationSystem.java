import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OnlineReservationSystem {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public OnlineReservationSystem() {
        // Set the futuristic theme
        FlatLightLaf.setup();

        // Main Frame Setup
        frame = new JFrame("Online Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Rounded Corners
        frame.setUndecorated(true);
        frame.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 600, 400, 50, 50));
        frame.setLocationRelativeTo(null);

        // CardLayout for Navigation
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add Screens
        mainPanel.add(loginScreen(), "Login");
        mainPanel.add(reservationScreen(), "Reservation");
        mainPanel.add(cancellationScreen(), "Cancellation");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Login Screen
    private JPanel loginScreen() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.decode("#1E1E2F"));

        JLabel titleLabel = createLabel("Login", 220, 30, 160, 30, 20);
        JLabel userLabel = createLabel("Username:", 100, 100, 100, 20, 16);
        JLabel passLabel = createLabel("Password:", 100, 140, 100, 20, 16);

        JTextField userField = createTextField(200, 100, 200, 25);
        JPasswordField passField = createPasswordField(200, 140, 200, 25);

        JButton loginButton = createButton("Login", 250, 200, 100, 30);
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (validateLogin(username, password)) {
                cardLayout.show(mainPanel, "Reservation");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(titleLabel);
        panel.add(userLabel);
        panel.add(passLabel);
        panel.add(userField);
        panel.add(passField);
        panel.add(loginButton);

        return panel;
    }

    // Reservation Screen
    private JPanel reservationScreen() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.decode("#1E1E2F"));

        JLabel titleLabel = createLabel("Reservation", 220, 30, 160, 30, 20);
        JLabel nameLabel = createLabel("Name:", 100, 80, 100, 20, 16);
        JLabel trainNumLabel = createLabel("Train Number:", 100, 120, 100, 20, 16);
        JLabel trainNameLabel = createLabel("Train Name:", 100, 160, 100, 20, 16);
        JLabel classTypeLabel = createLabel("Class Type:", 100, 200, 100, 20, 16);
        JLabel journeyDateLabel = createLabel("Journey Date:", 100, 240, 100, 20, 16);

        JTextField nameField = createTextField(220, 80, 200, 25);
        JTextField trainNumField = createTextField(220, 120, 200, 25);
        JTextField trainNameField = createTextField(220, 160, 200, 25);
        JTextField classTypeField = createTextField(220, 200, 200, 25);
        JTextField journeyDateField = createTextField(220, 240, 200, 25);

        JButton insertButton = createButton("Reserve", 250, 300, 100, 30);
        insertButton.addActionListener(e -> {
            String name = nameField.getText();
            int trainNum = Integer.parseInt(trainNumField.getText());
            String trainName = trainNameField.getText();
            String classType = classTypeField.getText();
            String journeyDate = journeyDateField.getText();

            reserveTicket(name, trainNum, trainName, classType, journeyDate);
        });

        panel.add(titleLabel);
        panel.add(nameLabel);
        panel.add(trainNumLabel);
        panel.add(trainNameLabel);
        panel.add(classTypeLabel);
        panel.add(journeyDateLabel);
        panel.add(nameField);
        panel.add(trainNumField);
        panel.add(trainNameField);
        panel.add(classTypeField);
        panel.add(journeyDateField);
        panel.add(insertButton);

        return panel;
    }

    // Cancellation Screen
    private JPanel cancellationScreen() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.decode("#1E1E2F"));

        JLabel titleLabel = createLabel("Cancellation", 220, 30, 160, 30, 20);
        JLabel pnrLabel = createLabel("PNR Number:", 100, 100, 100, 20, 16);

        JTextField pnrField = createTextField(220, 100, 200, 25);

        JButton cancelButton = createButton("Cancel Ticket", 250, 150, 140, 30);
        cancelButton.addActionListener(e -> {
            int pnr = Integer.parseInt(pnrField.getText());
            cancelTicket(pnr);
        });

        panel.add(titleLabel);
        panel.add(pnrLabel);
        panel.add(pnrField);
        panel.add(cancelButton);

        return panel;
    }

    // Helper Methods for GUI
    private JLabel createLabel(String text, int x, int y, int width, int height, int fontSize) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.decode("#FFD700"));
        return label;
    }

    private JTextField createTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        return textField;
    }

    private JPasswordField createPasswordField(int x, int y, int width, int height) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(x, y, width, height);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        return passwordField;
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(Color.decode("#FFD700"));
        button.setForeground(Color.decode("#1E1E2F"));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.decode("#FFD700"), 2, true));
        return button;
    }

    // Logic for Login Validation
    private boolean validateLogin(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic for Ticket Reservation
    private void reserveTicket(String name, int trainNum, String trainName, String classType, String journeyDate) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO reservations (name, train_number, train_name, class_type, journey_date, from_place, to_place) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, name);
            ps.setInt(2, trainNum);
            ps.setString(3, trainName);
            ps.setString(4, classType);
            ps.setString(5, journeyDate);
            ps.setString(6, "Unknown");
            ps.setString(7, "Unknown");

            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Reservation successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Reservation failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Logic for Ticket Cancellation
    private void cancelTicket(int pnr) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM reservations WHERE pnr = ?")) {

            ps.setInt(1, pnr);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Ticket canceled successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "PNR not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Cancellation failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineReservationSystem::new);
    }
}
