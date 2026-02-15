import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class App extends JFrame {

    JTextField txtId, txtNama, txtKelas, txtJumlah;
    // remember current record id when a row is selected
    String selectedId = null;
    JComboBox<String> cbJurusan, cbPembayaran;
    JTable table;
    DefaultTableModel model;

    public App(String username, String role) {
        setTitle("Sistem Pembayaran SPP | Login: " + username);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // === FORM ===
        txtId = new JTextField(10);
        // editability controlled by selection/resetForm
        txtNama = new JTextField(10);
        txtKelas = new JTextField(10);
        txtJumlah = new JTextField(10);

        cbJurusan = new JComboBox<>(new String[]{"IPA","IPS","Bahasa"});
        cbPembayaran = new JComboBox<>(new String[]{"SPP Januari","SPP Februari","SPP Maret"});

        JPanel form = new JPanel(new GridLayout(3,4,5,5));
        form.add(new JLabel("ID Siswa"));
        form.add(txtId);
        form.add(new JLabel("Nama"));
        form.add(txtNama);
        form.add(new JLabel("Kelas"));
        form.add(txtKelas);
        form.add(new JLabel("Jurusan"));
        form.add(cbJurusan);
        form.add(new JLabel("Pembayaran"));
        form.add(cbPembayaran);
        form.add(new JLabel("Jumlah"));
        form.add(txtJumlah);

        // === BUTTON ===
        JButton btnSimpan = new JButton("Simpan");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnCetak = new JButton("Cetak");

        JPanel tombol = new JPanel();
        tombol.add(btnSimpan);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnCetak);

        // Pembatasan CRUD
        if (!role.equalsIgnoreCase("admin")) {
            btnHapus.setEnabled(false);
        }

        // === TABLE ===
        model = new DefaultTableModel(
            new String[]{"ID","Nama","Kelas","Jurusan","Pembayaran","Jumlah"}, 0
        );
        table = new JTable(model);

        add(form, BorderLayout.NORTH);
        add(tombol, BorderLayout.CENTER);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        tampilData();
        resetForm();

        btnSimpan.addActionListener(e -> { simpan(); resetForm(); });
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> { hapus(); resetForm(); });
        btnCetak.addActionListener(e -> ekspor());

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                selectedId = model.getValueAt(r,0).toString();
                txtId.setText(selectedId);
                txtId.setEditable(false);
                txtNama.setText(model.getValueAt(r,1).toString());
                txtKelas.setText(model.getValueAt(r,2).toString());
                cbJurusan.setSelectedItem(model.getValueAt(r,3));
                cbPembayaran.setSelectedItem(model.getValueAt(r,4));
                txtJumlah.setText(model.getValueAt(r,5).toString());
            }
        });
    }

    void tampilData() {
        model.setRowCount(0);
        try {
            Statement st = ConnectionPosgres.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM pembayaran_spp");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_siswa"),
                    rs.getString("nama_siswa"),
                    rs.getString("kelas"),
                    rs.getString("jurusan"),
                    rs.getString("pembayaran"),
                    rs.getInt("jumlah")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void simpan() {
        try {
            String sql = "INSERT INTO pembayaran_spp VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = ConnectionPosgres.getConnection().prepareStatement(sql);
            ps.setString(1, txtId.getText());
            ps.setString(2, txtNama.getText());
            ps.setString(3, txtKelas.getText());
            ps.setString(4, cbJurusan.getSelectedItem().toString());
            ps.setString(5, cbPembayaran.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(txtJumlah.getText()));
            ps.executeUpdate();
            tampilData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void ubah() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu sebelum mengubah.");
            return;
        }
        try {
            String sql = """
                UPDATE pembayaran_spp
                SET nama_siswa=?, kelas=?, jurusan=?, pembayaran=?, jumlah=?
                WHERE id_siswa=?
            """;
            PreparedStatement ps = ConnectionPosgres.getConnection().prepareStatement(sql);
            ps.setString(1, txtNama.getText());
            ps.setString(2, txtKelas.getText());
            ps.setString(3, cbJurusan.getSelectedItem().toString());
            ps.setString(4, cbPembayaran.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtJumlah.getText()));
            // use original id stored when row was selected
            ps.setString(6, selectedId);
            int count = ps.executeUpdate();
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada baris yang diubah. ID mungkin tidak valid.");
            }
            tampilData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void hapus() {
        try {
            String sql = "DELETE FROM pembayaran_spp WHERE id_siswa=?";
            PreparedStatement ps = ConnectionPosgres.getConnection().prepareStatement(sql);
            ps.setString(1, txtId.getText());
            ps.executeUpdate();
            tampilData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    void ekspor() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan sebagai CSV/TXT");
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // user cancelled
        }
        File file = chooser.getSelectedFile();
        // ensure extension
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".csv") && !path.toLowerCase().endsWith(".txt")) {
            file = new File(path + ".csv");
        }
        try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
            // header
            for (int c = 0; c < table.getColumnCount(); c++) {
                pw.print(table.getColumnName(c));
                if (c < table.getColumnCount() - 1) pw.print(",");
            }
            pw.println();
            // data rows
            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    Object val = table.getValueAt(r, c);
                    pw.print(val == null ? "" : val.toString());
                    if (c < table.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke " + file.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // reset form fields when not editing an existing record
    void resetForm() {
        selectedId = null;
        txtId.setEditable(true);
        txtId.setText("");
        txtNama.setText("");
        txtKelas.setText("");
        cbJurusan.setSelectedIndex(0);
        cbPembayaran.setSelectedIndex(0);
        txtJumlah.setText("");
        table.clearSelection();
    }
}