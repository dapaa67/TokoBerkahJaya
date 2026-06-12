package tokoberkah.dao;

import tokoberkah.util.DBUtil;
import java.sql.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PenjualanDAO {

    // Simpan 1 transaksi dengan banyak item sekaligus
    public boolean simpanTransaksi(String idCustomer, int idUser,
                                   double totalBayar,
                                   List<Object[]> items) {
        // items: tiap Object[] = {idBarang, jumlah, hargaSatuan, subtotal}
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // mulai transaksi DB

            // 1. INSERT header ke tb_penjualan
            String sqlHeader =
                "INSERT INTO tb_penjualan " +
                "(tgl_transaksi, id_customer, id_user, total_bayar) " +
                "VALUES (CURDATE(), ?, ?, ?)";
            PreparedStatement psHeader =
                conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            if (idCustomer == null || idCustomer.trim().isEmpty()) {
                psHeader.setNull(1, Types.VARCHAR);
            } else {
                psHeader.setString(1, idCustomer);
            }
            psHeader.setInt   (2, idUser);
            psHeader.setDouble(3, totalBayar);
            psHeader.executeUpdate();

            // Ambil id_jual yang baru dibuat
            ResultSet rs = psHeader.getGeneratedKeys();
            if (!rs.next()) throw new SQLException("Gagal mendapat id_jual!");
            int idJual = rs.getInt(1);

            // 2. INSERT tiap item ke tb_detail_penjualan
            String sqlDetail =
                "INSERT INTO tb_detail_penjualan " +
                "(id_jual, id_barang, jumlah_beli, harga_satuan, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);

            // 3. UPDATE stok tiap barang
            String sqlStok =
                "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?";
            PreparedStatement psStok = conn.prepareStatement(sqlStok);

            for (Object[] item : items) {
                String idBarang    = (String) item[0];
                int    jumlah      = (int)    item[1];
                double harga       = (double) item[2];
                double subtotal    = (double) item[3];

                // Insert detail
                psDetail.setInt   (1, idJual);
                psDetail.setString(2, idBarang);
                psDetail.setInt   (3, jumlah);
                psDetail.setDouble(4, harga);
                psDetail.setDouble(5, subtotal);
                psDetail.addBatch();

                // Update stok
                psStok.setInt   (1, jumlah);
                psStok.setString(2, idBarang);
                psStok.addBatch();
            }

            psDetail.executeBatch();
            psStok.executeBatch();

            conn.commit(); // semua berhasil → simpan
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    public DefaultTableModel getRiwayatSaya(int idUser) {
        String[] kolom = {"ID Jual", "Tanggal", "Customer",
                          "Jml Item", "Total Bayar"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(kolom, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

        String sql = """
            SELECT
                p.id_jual,
                p.tgl_transaksi,
                IFNULL(c.nama_customer, 'Pelanggan Umum') AS nama_customer,
                COUNT(d.id_detail)  AS jml_item,
                p.total_bayar
            FROM tb_penjualan p
            LEFT JOIN tb_customer c ON p.id_customer = c.id_customer
            LEFT JOIN tb_detail_penjualan d ON p.id_jual = d.id_jual
            WHERE p.id_user = ?
            GROUP BY p.id_jual
            ORDER BY p.id_jual DESC
            """;

        try (java.sql.Connection conn = tokoberkah.util.DBUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_jual"),
                    rs.getString("tgl_transaksi"),
                    rs.getString("nama_customer"),
                    rs.getInt("jml_item") + " item",
                    "Rp " + String.format("%,.0f", rs.getDouble("total_bayar"))
                              .replace(",", ".")
                });
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
        return model;
    }
}