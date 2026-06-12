package tokoberkah.dao;

import tokoberkah.model.Kategori;
import tokoberkah.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {
    
    public List<Kategori> getAll() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_kategori ORDER BY nama_kategori";
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Kategori(
                    r.getInt("id_kategori"),
                    r.getString("nama_kategori")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(String namaKategori) {
        String sql = "INSERT INTO tb_kategori (nama_kategori) VALUES (?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int cekBarangTerkait(int idKategori) {
        String sql = "SELECT COUNT(*) FROM tb_barang WHERE id_kategori = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean hapus(int idKategori) {
        String sql = "DELETE FROM tb_kategori WHERE id_kategori = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
