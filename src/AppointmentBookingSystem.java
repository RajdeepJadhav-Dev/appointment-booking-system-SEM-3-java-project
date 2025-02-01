import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AppointmentBookingSystem extends JFrame {

    private JTextField patientField, doctorField, dateField, timeField;
    private JButton bookButton, viewAppointmentsButton, deleteButton;

    public AppointmentBookingSystem() {
        // Set up the frame
        setTitle("Doctor Appointment Booking System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Main panel for form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setBackground(new Color(230, 240, 250)); // Light blue background

        // Set up labels and text fields with colors
        JLabel patientLabel = new JLabel("Patient Name:");
        patientLabel.setForeground(new Color(34, 87, 126)); // Dark blue text color
        patientField = new JTextField();

        JLabel doctorLabel = new JLabel("Doctor Name:");
        doctorLabel.setForeground(new Color(34, 87, 126));
        doctorField = new JTextField();

        JLabel dateLabel = new JLabel("Appointment Date (YYYY-MM-DD):");
        dateLabel.setForeground(new Color(34, 87, 126));
        dateField = new JTextField();

        JLabel timeLabel = new JLabel("Appointment Time (HH:MM):");
        timeLabel.setForeground(new Color(34, 87, 126));
        timeField = new JTextField();

        // Add labels and text fields to the form panel
        formPanel.add(patientLabel);
        formPanel.add(patientField);
        formPanel.add(doctorLabel);
        formPanel.add(doctorField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeField);

        // Panel for buttons with a different background color
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(210, 225, 245)); // Slightly darker blue

        // Buttons with custom colors
        bookButton = new JButton("Book Appointment");
        bookButton.setBackground(new Color(67, 160, 71)); // Green button
        bookButton.setForeground(Color.WHITE); // White text

        viewAppointmentsButton = new JButton("View Appointments");
        viewAppointmentsButton.setBackground(new Color(33, 150, 243)); // Blue button
        viewAppointmentsButton.setForeground(Color.WHITE);

        deleteButton = new JButton("Delete Appointment");
        deleteButton.setBackground(new Color(244, 67, 54)); // Red button
        deleteButton.setForeground(Color.WHITE);

        // Add buttons to button panel
        buttonPanel.add(bookButton);
        buttonPanel.add(viewAppointmentsButton);
        buttonPanel.add(deleteButton);

        // Add panels to the main frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up button actions
        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> bookAppointment()).start();
            }
        });

        viewAppointmentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ViewAppointments view = new ViewAppointments();
                view.setVisible(true);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idStr = JOptionPane.showInputDialog("Enter the Appointment ID to delete:");
                if (idStr != null && !idStr.isEmpty()) {
                    try {
                        int id = Integer.parseInt(idStr);
                        new Thread(() -> deleteAppointment(id)).start();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid ID format. Please enter a numeric value.");
                    }
                }
            }
        });
    }

    // Method to book an appointment
    private void bookAppointment() {
        String patientName = patientField.getText();
        String doctorName = doctorField.getText();
        String appointmentDate = dateField.getText();
        String appointmentTime = timeField.getText();

        try {
            Connection con = getConnection();
            String query = "INSERT INTO appointments (patient_name, doctor_name, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, patientName);
            ps.setString(2, doctorName);
            ps.setString(3, appointmentDate);
            ps.setString(4, appointmentTime);
            ps.executeUpdate();
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null, "Appointment booked successfully!")
            );
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null, "Error booking appointment.")
            );
        }
    }

    // Method to establish database connection
    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/DoctorAppointmentDB"; // Update as needed
        String user = "postgres";  // Replace with your PostgreSQL username
        String password = "postgres";  // Replace with your PostgreSQL password
        return DriverManager.getConnection(url, user, password);
    }

    // Method to delete an appointment
    public void deleteAppointment(int appointmentId) {
        try (Connection con = getConnection()) {
            String query = "DELETE FROM appointments WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, appointmentId);
            int rowsAffected = ps.executeUpdate();

            SwingUtilities.invokeLater(() -> {
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Appointment deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "No appointment found with the specified ID.");
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null, "Error deleting appointment: " + e.getMessage())
            );
        }
    }

    public static void main(String[] args) {
        AppointmentBookingSystem app = new AppointmentBookingSystem();
        app.setVisible(true);
    }
}
