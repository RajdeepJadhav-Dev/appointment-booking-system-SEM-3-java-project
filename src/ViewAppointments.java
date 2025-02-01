import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewAppointments extends JFrame {

    private JTextArea appointmentsArea;

    public ViewAppointments() {
        setTitle("View Appointments");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        appointmentsArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(appointmentsArea);
        add(scrollPane, BorderLayout.CENTER);

        loadAppointments();
    }

    private void loadAppointments() {
        try {
            Connection con = getConnection();
            String query = "SELECT * FROM appointments";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            StringBuilder appointments = new StringBuilder();

            while (rs.next()) {

                appointments.append("ID: ").append(rs.getInt("id")).append("\n")
                            .append("Patient: ").append(rs.getString("patient_name")).append("\n")
                            .append("Doctor: ").append(rs.getString("doctor_name")).append("\n")
                            .append("Date: ").append(rs.getString("appointment_date")).append("\n")
                            .append("Time: ").append(rs.getString("appointment_time")).append("\n")
                            .append("Status: ").append(rs.getString("status")).append("\n\n");
            }

            appointmentsArea.setText(appointments.toString());
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading appointments.");
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/DoctorAppointmentDB"; // Update as needed
        String user = "postgres";  // Replace with your PostgreSQL username
        String password = "postgres";  // Replace with your PostgreSQL password
        return DriverManager.getConnection(url, user, password);
    }
}