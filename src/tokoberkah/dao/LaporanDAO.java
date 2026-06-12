/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.dao;

import tokoberkah.util.DBUtil;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class LaporanDAO {

    // ── Ambil laporan berdasarkan filter tanggal & customer ───
    public DefaultTableModel getLaporan(String dariTgl, String sampaiTgl,
                                        String idCustomer) {
        String[] kolom = {"No", "ID Jual", "Tanggal", "Customer",
                          "Jml Item", "Total Bayar", "Kasir"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        StringBuilder sql = new StringBuilder("""
            SELECT
                p.id_jual,
                DATE_FORMAT(p.tgl_transaksi, '%d-%m-%Y') AS tgl_transaksi_fmt,
                IFNULL(c.nama_customer, 'Pelanggan Umum') AS nama_customer,
                COUNT(d.id_detail)        AS jml_item,
                p.total_bayar,
                u.nama_lengkap
            FROM tb_penjualan p
            LEFT JOIN tb_customer c    ON p.id_customer = c.id_customer
            JOIN tb_user u             ON p.id_user     = u.id_user
            LEFT JOIN tb_detail_penjualan d ON p.id_jual = d.id_jual
            WHERE p.tgl_transaksi BETWEEN ? AND ?
            """);

        // Filter customer kalau dipilih
        if (idCustomer != null && !idCustomer.equals("Semua")) {
            sql.append("AND p.id_customer = ? ");
        }
        sql.append("GROUP BY p.id_jual ORDER BY p.tgl_transaksi DESC, p.id_jual DESC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, dariTgl);
            ps.setString(2, sampaiTgl);
            if (idCustomer != null && !idCustomer.equals("Semua")) {
                ps.setString(3, idCustomer);
            }

            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    no++,
                    rs.getInt("id_jual"),
                    rs.getString("tgl_transaksi_fmt"),
                    rs.getString("nama_customer"),
                    rs.getInt("jml_item") + " item",
                    "Rp " + String.format("%,.0f", rs.getDouble("total_bayar"))
                              .replace(",", "."),
                    rs.getString("nama_lengkap")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return model;
    }

    // ── Stat cards ────────────────────────────────────────────
    public int getTotalTransaksi(String dari, String sampai) {
        String sql = "SELECT COUNT(*) FROM tb_penjualan " +
                     "WHERE tgl_transaksi BETWEEN ? AND ?";
        return ambilInt(sql, dari, sampai);
    }

    public double getTotalPendapatan(String dari, String sampai) {
        String sql = "SELECT IFNULL(SUM(total_bayar),0) FROM tb_penjualan " +
                     "WHERE tgl_transaksi BETWEEN ? AND ?";
        return ambilDouble(sql, dari, sampai);
    }

    public int getTotalItemTerjual(String dari, String sampai) {
        String sql = """
            SELECT IFNULL(SUM(d.jumlah_beli), 0)
            FROM tb_detail_penjualan d
            JOIN tb_penjualan p ON d.id_jual = p.id_jual
            WHERE p.tgl_transaksi BETWEEN ? AND ?
            """;
        return ambilInt(sql, dari, sampai);
    }

    // ── Detail satu transaksi ─────────────────────────────────
    public DefaultTableModel getDetailTransaksi(int idJual) {
        String[] kolom = {"Nama Barang", "Harga Satuan", "Jumlah", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = """
            SELECT b.nama_barang, d.harga_satuan, d.jumlah_beli, d.subtotal
            FROM tb_detail_penjualan d
            JOIN tb_barang b ON d.id_barang = b.id_barang
            WHERE d.id_jual = ?
            """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJual);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_barang"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_satuan"))
                              .replace(",", "."),
                    rs.getInt("jumlah_beli"),
                    "Rp " + String.format("%,.0f", rs.getDouble("subtotal"))
                              .replace(",", ".")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return model;
    }

    // ── Info header transaksi (untuk dialog detail) ───────────
    public String[] getHeaderTransaksi(int idJual) {
        // return: {idJual, tanggal, namaCustomer, namaKasir, totalBayar}
        String sql = """
            SELECT p.id_jual, DATE_FORMAT(p.tgl_transaksi, '%d-%m-%Y') AS tgl_transaksi_fmt,
                   IFNULL(c.nama_customer, 'Pelanggan Umum') AS nama_customer, 
                   u.nama_lengkap, p.total_bayar
            FROM tb_penjualan p
            LEFT JOIN tb_customer c ON p.id_customer = c.id_customer
            JOIN tb_user u     ON p.id_user     = u.id_user
            WHERE p.id_jual = ?
            """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJual);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("id_jual")),
                    rs.getString("tgl_transaksi_fmt"),
                    rs.getString("nama_customer"),
                    rs.getString("nama_lengkap"),
                    "Rp " + String.format("%,.0f", rs.getDouble("total_bayar"))
                              .replace(",", ".")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────
    private int ambilInt(String sql, String p1, String p2) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p1);
            ps.setString(2, p2);
            ResultSet r = ps.executeQuery();
            return r.next() ? r.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private double ambilDouble(String sql, String p1, String p2) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p1);
            ps.setString(2, p2);
            ResultSet r = ps.executeQuery();
            return r.next() ? r.getDouble(1) : 0;
        } catch (SQLException e) { return 0; }
    }
}