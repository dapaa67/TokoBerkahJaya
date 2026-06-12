/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import tokoberkah.dao.BarangDAO;
import tokoberkah.model.Barang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DialogCariBarang extends JDialog {

    private JTextField        txtCari;
    private JTable            tabel;
    private DefaultTableModel modelTabel;
    private JButton           btnPilih, btnBatal;

    private final BarangDAO   dao = new BarangDAO();
    private Barang            barangDipilih = null; // hasil pilihan

    // ── Warna ─────────────────────────────────────────────────
    private static final Color C_DARK   = new Color(30, 42, 58);
    private static final Color C_BLUE   = new Color(37, 99, 235);
    private static final Color C_GRAY   = new Color(240, 242, 245);
    private static final Color C_BORDER = new Color(229, 231, 235);
    private static final Color C_MUTED  = new Color(136, 136, 136);

    public DialogCariBarang(Frame parent) {
        super(parent, "Cari Barang", true); // modal
        buildUI();
        muatData("");
        setSize(620, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTabel(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header: judul + search bar ────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        JLabel lblTitle = new JLabel("Cari & Pilih Barang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(C_DARK);
        lblTitle.setBounds(16, 10, 300, 22);

        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.putClientProperty("JTextField.placeholderText",
            "Ketik nama atau ID barang...");
        txtCari.setBounds(16, 36, 460, 24);

        JButton btnCari = buatTombol("Cari", C_BLUE, Color.WHITE);
        btnCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCari.setBounds(484, 34, 110, 28);

        panel.add(lblTitle);
        panel.add(txtCari);
        panel.add(btnCari);

        // Listener cari
        btnCari.addActionListener(e -> muatData(txtCari.getText().trim()));
        txtCari.addActionListener(e -> muatData(txtCari.getText().trim()));

        // Real-time search saat mengetik
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                muatData(txtCari.getText().trim());
            }
        });

        return panel;
    }

    // ── Tabel daftar barang ───────────────────────────────────
    private JPanel buildTabel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] kolom = {"ID Barang", "Nama Barang", "Satuan", "Harga Jual", "Stok"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabel = new JTable(modelTabel);
        tabel.setRowHeight(34);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 1. Warna background pas dipilih (Merah super pudar)
        tabel.setSelectionBackground(new Color(254, 226, 226));
        
        // 2. Warna teks pas dipilih (Merah bata gelap biar kelihatan)
        tabel.setSelectionForeground(new Color(153, 27, 27));
        
        tabel.setShowHorizontalLines(true);
        
        // 3. Warna garis pembatas (Abu-abu kecokelatan)
        tabel.setGridColor(new Color(231, 229, 228));
        
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        // 4. Warna background header (Krem/putih tulang)
        tabel.getTableHeader().setBackground(new Color(250, 250, 249));
        
        // 5. Warna teks header (Cokelat gelap)
        tabel.getTableHeader().setForeground(new Color(87, 83, 78));
        
        tabel.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        // Lebar kolom
        tabel.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(210);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(70);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(60);

        // Double-click baris → langsung pilih
        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) pilihBarang();
                // Single click → aktifkan tombol pilih
                btnPilih.setEnabled(tabel.getSelectedRow() >= 0);
            }
        });

        panel.add(new JScrollPane(tabel), BorderLayout.CENTER);
        return panel;
    }

    // ── Footer: tombol Pilih & Batal ──────────────────────────
    private JPanel buildFooter() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 58));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));

        JLabel lblHint = new JLabel("Tip: double-click baris untuk langsung memilih");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(C_MUTED);
        lblHint.setBounds(16, 20, 320, 16);

        btnPilih = buatTombol("Pilih Barang", C_BLUE, Color.WHITE);
        btnPilih.setBounds(390, 12, 130, 34);
        btnPilih.setEnabled(false); // aktif setelah baris dipilih

        btnBatal = buatTombol("Batal", C_GRAY, C_DARK);
        btnBatal.setBorder(BorderFactory.createLineBorder(C_BORDER));
        btnBatal.setBounds(530, 12, 80, 34);

        panel.add(lblHint);
        panel.add(btnPilih);
        panel.add(btnBatal);

        btnPilih.addActionListener(e -> pilihBarang());
        btnBatal.addActionListener(e -> dispose());

        return panel;
    }

    // ── Muat / Filter Data ────────────────────────────────────
    private void muatData(String keyword) {
        modelTabel.setRowCount(0);
        List<Barang> list = dao.getAll();
        String k = keyword.toLowerCase();

        for (Barang b : list) {
            // Filter berdasarkan keyword
            if (k.isEmpty()
                || b.getNamaBarang().toLowerCase().contains(k)
                || b.getIdBarang().toLowerCase().contains(k)) {

                // Tandai stok menipis dengan warna berbeda nanti
                modelTabel.addRow(new Object[]{
                    b.getIdBarang(),
                    b.getNamaBarang(),
                    b.getSatuan(),
                    "Rp " + String.format("%,.0f", b.getHargaJual()),
                    b.getStok()
                });
            }
        }

        // Reset tombol pilih
        btnPilih.setEnabled(false);
    }

    // ── Pilih Barang dari Baris Terpilih ──────────────────────
    private void pilihBarang() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Pilih barang terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idBarang = modelTabel.getValueAt(row, 0).toString();
        barangDipilih   = dao.getById(idBarang);
        dispose(); // tutup dialog
    }

    // ── Getter hasil pilihan ──────────────────────────────────
    public Barang getBarangDipilih() {
        return barangDipilih;
    }

    // ── Helper tombol ─────────────────────────────────────────
    private JButton buatTombol(String teks, Color bg, Color fg) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}