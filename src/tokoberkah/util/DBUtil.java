/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBUtil {

    private static final String URL  = "jdbc:mysql://localhost:3306/db_berkah_jaya";
    private static final String USER = "root";
    private static final String PASS = "";  // ganti kalau MySQL kamu pakai password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void initDatabase() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            
            // Buat tb_kategori jika belum ada
            String createKategori = "CREATE TABLE IF NOT EXISTS tb_kategori (" +
                                    "id_kategori INT AUTO_INCREMENT PRIMARY KEY, " +
                                    "nama_kategori VARCHAR(100) NOT NULL" +
                                    ")";
            s.execute(createKategori);
            
            // Insert default data jika tabel kosong
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM tb_kategori");
            if (rs.next() && rs.getInt(1) == 0) {
                s.execute("INSERT INTO tb_kategori (nama_kategori) VALUES " +
                          "('Alat Rumah Tangga'), ('Makanan'), ('Minuman'), ('Elektronik')");
            }
            
            // Mengubah struktur tabel untuk mendukung walk-in customer (NULL)
            try {
                // Asumsi id_customer adalah VARCHAR(20) atau sejenisnya
                s.execute("ALTER TABLE tb_penjualan MODIFY id_customer VARCHAR(50) NULL");
            } catch (Exception alterEx) {
                // Abaikan jika sudah NULL atau tipe datanya berbeda
            }
            
        } catch (Exception e) {
            System.err.println("Gagal init database: " + e.getMessage());
        }
    }
}
