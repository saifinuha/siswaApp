// Saifin Nuha
// 240401010257
// IF503
// Pemrograman Berorientasi Objek
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {

    JTextField txtUsername;
    JPasswordField txtPassword;

    public Login() {
        try {
            Class.forName("org.postgresql.Driver");
            JOptionPane.showMessageDialog(null, "PostgreSQL Driver OK!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Driver NOT found!");
        }
        setTitle("Login Sistem Pembayaran SPP");
        setSize(350, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        JLabel lblJudul = new JLabel("LOGIN", JLabel.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 18));

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        panel.add(lblJudul, gbc);

        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=1;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx=1;
        panel.add(txtUsername, gbc);

        gbc.gridx=0; gbc.gridy=2;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx=1;
        panel.add(txtPassword, gbc);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        panel.add(btnLogin, gbc);

        add(panel);

        btnLogin.addActionListener(e -> login());
    }

    void login() {
        try {
            String sql = """
                SELECT username, role
                FROM users
                WHERE username = ?
                AND password = crypt(?, password)
            """;

            PreparedStatement ps = ConnectionPosgres.getConnection().prepareStatement(sql);
            ps.setString(1, txtUsername.getText());
            ps.setString(2, new String(txtPassword.getPassword()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login berhasil");

                new App(
                    rs.getString("username"),
                    rs.getString("role")
                ).setVisible(true);

                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}