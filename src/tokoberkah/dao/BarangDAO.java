/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.dao;

import tokoberkah.model.Barang;
import tokoberkah.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    public List<Barang> getAll() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_barang ORDER BY id_barang";
        try (Connection c = DBUtil.getConnection();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Barang(
                    r.getString("id_barang"),
                    r.getInt("id_kategori"),
                    r.getString("nama_barang"),
                    r.getString("satuan"),
                    r.getDouble("harga_jual"),
                    r.getInt("stok")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Barang> getByKategori(int idKategori) {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_barang WHERE id_kategori = ? ORDER BY id_barang";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                list.add(new Barang(
                    r.getString("id_barang"),
                    r.getInt("id_kategori"),
                    r.getString("nama_barang"),
                    r.getString("satuan"),
                    r.getDouble("harga_jual"),
                    r.getInt("stok")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Barang getById(String id) {
        String sql = "SELECT * FROM tb_barang WHERE id_barang = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                return new Barang(
                    r.getString("id_barang"),
                    r.getInt("id_kategori"),
                    r.getString("nama_barang"),
                    r.getString("satuan"),
                    r.getDouble("harga_jual"),
                    r.getInt("stok")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Barang b) {
        String sql = "INSERT INTO tb_barang VALUES (?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getIdBarang());
            ps.setInt   (2, b.getIdKategori());
            ps.setString(3, b.getNamaBarang());
            ps.setString(4, b.getSatuan());
            ps.setDouble(5, b.getHargaJual());
            ps.setInt   (6, b.getStok());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Barang b) {
        String sql = "UPDATE tb_barang SET id_kategori=?, nama_barang=?, " +
                     "satuan=?, harga_jual=?, stok=? WHERE id_barang=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt   (1, b.getIdKategori());
            ps.setString(2, b.getNamaBarang());
            ps.setString(3, b.getSatuan());
            ps.setDouble(4, b.getHargaJual());
            ps.setInt   (5, b.getStok());
            ps.setString(6, b.getIdBarang());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM tb_barang WHERE id_barang=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Dipakai saat transaksi penjualan
    public boolean kurangiStok(String idBarang, int jumlah, Connection conn)
            throws SQLException {
        String sql = "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt   (1, jumlah);
        ps.setString(2, idBarang);
        return ps.executeUpdate() > 0;
    }
}
