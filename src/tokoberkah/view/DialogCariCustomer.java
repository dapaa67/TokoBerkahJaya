/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import tokoberkah.model.Customer;
import tokoberkah.util.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DialogCariCustomer extends JDialog {

    private JTextField txtCari;
    private JTable tblCustomer;
    private DefaultTableModel modelCustomer;
    private Customer customerDipilih = null;

    public DialogCariCustomer(Frame parent) {
        super(parent, true); // true = modal (form di belakangnya gak bisa diklik sebelum ini ditutup)
        setTitle("Pilih Customer");
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // ── Panel Atas (Kolom Pencarian) ──────────────────────
        JPanel panelAtas = new JPanel(new BorderLayout(8, 0));
        panelAtas.setBackground(Color.WHITE);
        panelAtas.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JLabel lblCari = new JLabel("Cari Nama / ID:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(new Color(30, 42, 58));

        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.setPreferredSize(new Dimension(0, 34));
        
        // Listener pencarian Real-time
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                muatData(txtCari.getText().trim());
            }
        });

        panelAtas.add(lblCari, BorderLayout.WEST);
        panelAtas.add(txtCari, BorderLayout.CENTER);
        add(panelAtas, BorderLayout.NORTH);

        // ── Tengah (Tabel Customer) ───────────────────────────
        JPanel panelTengah = new JPanel(new BorderLayout());
        panelTengah.setBackground(Color.WHITE);
        panelTengah.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        String[] kolom = {"ID Customer", "Nama Customer", "Telepon"};
        modelCustomer = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int row, int column) { return false; } // Biar gak bisa diketik
        };

        tblCustomer = new JTable(modelCustomer);
        tblCustomer.setRowHeight(34);
        tblCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Styling Tabel Tema Hangat (Konsisten)
        tblCustomer.setSelectionBackground(new Color(254, 226, 226));
        tblCustomer.setSelectionForeground(new Color(153, 27, 27));
        tblCustomer.setShowHorizontalLines(true);
        tblCustomer.setGridColor(new Color(231, 229, 228));
        tblCustomer.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tblCustomer.getTableHeader().setBackground(new Color(250, 250, 249));
        tblCustomer.getTableHeader().setForeground(new Color(87, 83, 78));
        
        // Lebar kolom
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(120);

        // Listener klik ganda (Double Click) buat milih customer
        tblCustomer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblCustomer.getSelectedRow() != -1) {
                    pilihCustomer();
                }
            }
        });

        panelTengah.add(new JScrollPane(tblCustomer), BorderLayout.CENTER);
        add(panelTengah, BorderLayout.CENTER);

        // ── Bawah (Tombol Pilih) ──────────────────────────────
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBawah.setBackground(Color.WHITE);
        panelBawah.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));

        JButton btnPilih = new JButton("Pilih Customer");
        btnPilih.setBackground(new Color(37, 99, 235)); // C_BLUE
        btnPilih.setForeground(Color.WHITE);
        btnPilih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPilih.setPreferredSize(new Dimension(140, 36));
        btnPilih.setFocusPainted(false);
        btnPilih.addActionListener(e -> pilihCustomer());

        panelBawah.add(btnPilih);
        add(panelBawah, BorderLayout.SOUTH);

        // Tarik data pertama kali pop-up dibuka
        muatData(""); 
    }

    // ── Method Ambil Data dari Database ───────────────────────
    private void muatData(String keyword) {
        modelCustomer.setRowCount(0); // Bersihin tabel
        
        String sql = "SELECT id_customer, nama_customer, telepon FROM tb_customer " +
                     "WHERE nama_customer LIKE ? OR id_customer LIKE ? ORDER BY id_customer ASC";
                     
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                modelCustomer.addRow(new Object[]{
                    rs.getString("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("telepon")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data customer: " + e.getMessage());
        }
    }

    // ── Method Pas Customer Dipilih ───────────────────────────
    private void pilihCustomer() {
        int row = tblCustomer.getSelectedRow();
        if (row != -1) {
            customerDipilih = new Customer();
            
            // Masukin data dari baris yang diklik ke object Customer
            // NOTE: Sesuaikan nama Setter-nya kalau di class Customer lu beda (misal: setId(), setNama())
            customerDipilih.setIdCustomer(tblCustomer.getValueAt(row, 0).toString());
            customerDipilih.setNamaCustomer(tblCustomer.getValueAt(row, 1).toString());
            
            dispose(); // Tutup Pop-up
        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih customer dari tabel terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Buat Diambil Sama PanelPenjualan ──────────────────────
    public Customer getCustomerDipilih() {
        return customerDipilih;
    }
}
