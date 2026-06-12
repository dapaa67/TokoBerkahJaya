/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import tokoberkah.util.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DialogDetailDashboard extends JDialog {

    public DialogDetailDashboard(Frame parent, int idJual, String namaCust) {
        super(parent, true);
        setTitle("Detail Transaksi - #" + idJual);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Detail Pesanan: " + namaCust);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 42, 58));
        header.add(lblTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── Tabel Detail ──
        String[] kolom = {"Nama Barang", "Harga", "Qty", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowSelectionAllowed(false); // Gak perlu diblok
        
        // Tema tabel (Warm)
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(231, 229, 228));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(250, 250, 249));
        table.getTableHeader().setForeground(new Color(87, 83, 78));

        // Lebar Kolom
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        // ── Ambil Data dari Database ──
        String sql = """
            SELECT b.nama_barang, d.harga, d.jumlah, d.subtotal 
            FROM tb_detail_penjualan d 
            JOIN tb_barang b ON d.id_barang = b.id_barang 
            WHERE d.id_jual = ?
        """;
        
        double grandTotal = 0;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJual);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double sub = rs.getDouble("subtotal");
                grandTotal += sub;
                model.addRow(new Object[]{
                    rs.getString("nama_barang"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga")).replace(",", "."),
                    rs.getInt("jumlah") + "x",
                    "Rp " + String.format("%,.0f", sub).replace(",", ".")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel panelTengah = new JPanel(new BorderLayout());
        panelTengah.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        panelTengah.setBackground(Color.WHITE);
        panelTengah.add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelTengah, BorderLayout.CENTER);

        // ── Footer (Total & Tombol Tutup) ──
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        
        JLabel lblTotal = new JLabel("Total: Rp " + String.format("%,.0f", grandTotal).replace(",", "."));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(220, 38, 38)); // Merah total
        
        JButton btnTutup = new JButton("Tutup");
        btnTutup.setBackground(new Color(240, 242, 245));
        btnTutup.setFocusPainted(false);
        btnTutup.addActionListener(e -> dispose());

        footer.add(lblTotal, BorderLayout.WEST);
        footer.add(btnTutup, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }
}
