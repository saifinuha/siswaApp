import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConnectionPosgres {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null) {
                String url = "jdbc:postgresql://localhost:5432/dbProjectSiswa";
                String user = "nuha";      // ganti sesuai user PG
                String pass = "";      // ganti password PG
                conn = DriverManager.getConnection(url, user, pass);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
}