package tokoberkah.view;

import tokoberkah.dao.BarangDAO;
import tokoberkah.dao.CustomerDAO;
import tokoberkah.dao.LaporanDAO;
import tokoberkah.dao.PenjualanDAO;
import tokoberkah.model.Barang;
import tokoberkah.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PanelPenjualan extends JPanel {

    // ── Komponen Form ─────────────────────────────────────────
    private JComboBox<Customer> cmbCustomer;
    private JTextField          txtNamaBarang, txtHarga, txtJumlah, txtInfoStok;
    private JButton             btnCariBarang, btnTambah, btnHapusItem, btnSimpan, btnBatal;

    // ── Komponen Keranjang ────────────────────────────────────
    private JTable            tblKeranjang;
    private DefaultTableModel modelKeranjang;
    private JLabel            lblGrandTotal;

    // ── Komponen Riwayat ──────────────────────────────────────
    private JTable            tblRiwayat;
    private DefaultTableModel modelRiwayat;
    private JButton           btnRefreshRiwayat, btnDetailRiwayat;

    // ── Tab ───────────────────────────────────────────────────
    private JTabbedPane tabbedPane;

    // ── Data ──────────────────────────────────────────────────
    private final List<Object[]> keranjang = new ArrayList<>();
    private Barang               barangDipilih = null;
    private final int            idUserLogin;

    // ── DAO ───────────────────────────────────────────────────
    private final BarangDAO    barangDAO    = new BarangDAO();
    private final CustomerDAO  customerDAO  = new CustomerDAO();
    private final PenjualanDAO penjualanDAO = new PenjualanDAO();
    private final LaporanDAO   laporanDAO   = new LaporanDAO();

    // ── Warna Modern ──────────────────────────────────────────
    private static final Color C_BG         = new Color(248, 250, 252); // Slate 50
    private static final Color C_BRAND      = new Color(37, 99, 235);   // Blue 600
    private static final Color C_BRAND_HOVER= new Color(29, 78, 216);   // Blue 700
    private static final Color C_GREEN      = new Color(22, 163, 74);   // Green 600
    private static final Color C_GREEN_HOVER= new Color(21, 128, 61);
    private static final Color C_RED        = new Color(220, 38, 38);   // Red 600
    private static final Color C_RED_HOVER  = new Color(185, 28, 28);
    private static final Color C_TEXT_MAIN  = new Color(15, 23, 42);    // Slate 900
    private static final Color C_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color C_BORDER     = new Color(226, 232, 240); // Slate 200
    private static final Color C_CARD_BG    = Color.WHITE;

    public PanelPenjualan(int idUser) {
        this.idUserLogin = idUser;
        setLayout(new BorderLayout());
        setBackground(C_BG);
        add(buildTopbar(),  BorderLayout.NORTH);
        add(buildTabbed(),  BorderLayout.CENTER);
        muatCustomer();
    }

    // ── Topbar ────────────────────────────────────────────────
    private JPanel buildTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(C_CARD_BG);
        top.setPreferredSize(new Dimension(0, 84));
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("Transaksi Penjualan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(C_TEXT_MAIN);
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Input transaksi belanja dan lihat riwayat penjualan kasir Anda.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(C_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblJudul);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblSub);

        top.add(textPanel, BorderLayout.WEST);
        return top;
    }

    // ── Tab Panel ─────────────────────────────────────────────
    private JTabbedPane buildTabbed() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(C_BG);

        JPanel tabForm = buildTabForm();
        tabbedPane.addTab("  Form Transaksi  ", tabForm);

        JPanel tabRiwayat = buildTabRiwayat();
        tabbedPane.addTab("  Riwayat Kasir  ", tabRiwayat);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                muatRiwayat();
            }
        });

        return tabbedPane;
    }

    // ── Tab 1: Form Transaksi ─────────────────────────────────
    private JPanel buildTabForm() {
        JPanel panel = new JPanel(new BorderLayout(24, 0));
        panel.setBackground(C_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        panel.add(buildPanelKiri(),  BorderLayout.WEST);
        panel.add(buildPanelKanan(), BorderLayout.CENTER);
        return panel;
    }

    // ── Panel Kiri: Input Item ────────────────────────────────
    private JPanel buildPanelKiri() {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(C_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(360, 0));
        card.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));

        JLabel lblTitle = new JLabel("Tambah Item");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBounds(24, 20, 260, 20);

        JPanel garis1 = buatGaris(24, 54, 312);

        // Customer
        JLabel lblCust = buatLabel("Pilih Customer", 24, 70);
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCustomer.setBounds(24, 94, 312, 40);

        JPanel garis2 = buatGaris(24, 154, 312);

        // Barang
        JLabel lblBarang = buatLabel("Pilih Barang", 24, 174);
        txtNamaBarang = new JTextField();
        txtNamaBarang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNamaBarang.setEditable(false);
        txtNamaBarang.setBackground(new Color(241, 245, 249));
        txtNamaBarang.putClientProperty("JTextField.placeholderText", "Klik tombol Cari...");
        txtNamaBarang.setBounds(24, 198, 220, 40);

        btnCariBarang = buatTombol("Cari", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnCariBarang.setBounds(252, 198, 84, 40);

        // Harga
        JLabel lblHarga = buatLabel("Harga Satuan", 24, 254);
        txtHarga = new JTextField();
        txtHarga.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtHarga.setEditable(false);
        txtHarga.setBackground(new Color(241, 245, 249));
        txtHarga.setBounds(24, 278, 312, 40);

        // Stok
        JLabel lblStok = buatLabel("Stok Tersedia", 24, 334);
        txtInfoStok = new JTextField();
        txtInfoStok.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtInfoStok.setEditable(false);
        txtInfoStok.setBackground(new Color(241, 245, 249));
        txtInfoStok.setBounds(24, 358, 312, 40);

        // Jumlah
        JLabel lblJumlah = buatLabel("Jumlah Beli", 24, 414);
        txtJumlah = new JTextField();
        txtJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtJumlah.putClientProperty("JTextField.placeholderText", "Masukkan angka...");
        txtJumlah.setBounds(24, 438, 312, 40);

        txtJumlah.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE)
                    e.consume();
            }
            public void keyReleased(KeyEvent e) {
                hitungTotalSementara();
            }
        });

        // Tombol Tambah
        btnTambah = buatTombol("+ Tambah ke Keranjang", C_GREEN, Color.WHITE, C_GREEN_HOVER);
        btnTambah.setBounds(24, 500, 312, 46);

        card.add(lblTitle);    card.add(garis1);
        card.add(lblCust);     card.add(cmbCustomer);
        card.add(garis2);
        card.add(lblBarang);   card.add(txtNamaBarang); card.add(btnCariBarang);
        card.add(lblHarga);    card.add(txtHarga);
        card.add(lblStok);     card.add(txtInfoStok);
        card.add(lblJumlah);   card.add(txtJumlah);
        card.add(btnTambah);

        btnCariBarang.addActionListener(e -> bukaDialogCariBarang());
        btnTambah.addActionListener(e     -> tambahKeKeranjang());
        txtJumlah.addActionListener(e     -> tambahKeKeranjang());

        return card;
    }

    // ── Panel Kanan: Keranjang ────────────────────────────────
    private JPanel buildPanelKanan() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C_BG);

        JPanel cardTabel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(C_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        cardTabel.setOpaque(false);
        cardTabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        JPanel headerTabel = new JPanel(new BorderLayout());
        headerTabel.setOpaque(false);
        headerTabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblKeranjang = new JLabel("Daftar Belanja");
        lblKeranjang.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKeranjang.setForeground(C_TEXT_MAIN);

        btnHapusItem = buatTombol("Hapus Terpilih", C_RED, Color.WHITE, C_RED_HOVER);
        btnHapusItem.setPreferredSize(new Dimension(140, 36));
        btnHapusItem.setEnabled(false);

        headerTabel.add(lblKeranjang, BorderLayout.WEST);
        headerTabel.add(btnHapusItem, BorderLayout.EAST);

        String[] kolom = {"No", "ID Barang", "Nama Barang", "Harga Satuan", "Qty", "Subtotal"};
        modelKeranjang = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblKeranjang = new JTable(modelKeranjang);
        tblKeranjang.setRowHeight(44);
        tblKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblKeranjang.setForeground(C_TEXT_MAIN);
        tblKeranjang.setSelectionBackground(new Color(239, 246, 255));
        tblKeranjang.setSelectionForeground(C_BRAND);
        tblKeranjang.setShowVerticalLines(false);
        tblKeranjang.setGridColor(C_BORDER);
        tblKeranjang.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader th = tblKeranjang.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(C_TEXT_MUTED);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < tblKeranjang.getColumnCount(); i++) {
            tblKeranjang.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        tblKeranjang.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblKeranjang.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblKeranjang.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblKeranjang.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblKeranjang.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblKeranjang.getColumnModel().getColumn(5).setPreferredWidth(130);

        tblKeranjang.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                btnHapusItem.setEnabled(tblKeranjang.getSelectedRow() >= 0);
            }
        });

        JScrollPane sp = new JScrollPane(tblKeranjang);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        cardTabel.add(headerTabel, BorderLayout.NORTH);
        cardTabel.add(sp, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel();
        panelBawah.setLayout(new BoxLayout(panelBawah, BoxLayout.X_AXIS));
        panelBawah.setOpaque(false);
        panelBawah.setPreferredSize(new Dimension(0, 88));
        panelBawah.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblTotalText = new JLabel("Total:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalText.setForeground(C_TEXT_MUTED);

        lblGrandTotal = new JLabel("Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGrandTotal.setForeground(C_BRAND);
        
        panelBawah.add(lblTotalText);
        panelBawah.add(Box.createHorizontalStrut(12));
        panelBawah.add(lblGrandTotal);
        
        // Glue akan mendesak tombol ke kanan dan tulisan ke kiri
        panelBawah.add(Box.createHorizontalGlue());

        btnBatal = buatTombol("Batal / Reset", new Color(241, 245, 249), C_TEXT_MAIN, new Color(226, 232, 240));
        btnBatal.setPreferredSize(new Dimension(140, 46));
        btnBatal.setMaximumSize(new Dimension(140, 46));

        btnSimpan = buatTombol("Simpan Transaksi", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnSimpan.setPreferredSize(new Dimension(180, 46));
        btnSimpan.setMaximumSize(new Dimension(180, 46));

        panelBawah.add(btnBatal);
        panelBawah.add(Box.createHorizontalStrut(12));
        panelBawah.add(btnSimpan);

        cardTabel.add(panelBawah, BorderLayout.SOUTH);
        wrapper.add(cardTabel, BorderLayout.CENTER);

        btnHapusItem.addActionListener(e -> hapusItemKeranjang());
        btnSimpan.addActionListener(e    -> simpanTransaksi());
        btnBatal.addActionListener(e     -> resetForm());

        return wrapper;
    }

    // ── Tab 2: Riwayat Saya ───────────────────────────────────
    private JPanel buildTabRiwayat() {
        JPanel panel = new JPanel(new BorderLayout(24, 0));
        panel.setBackground(C_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(C_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel wrapTitle = new JPanel();
        wrapTitle.setLayout(new BoxLayout(wrapTitle, BoxLayout.Y_AXIS));
        wrapTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("Riwayat Transaksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Daftar transaksi yang pernah Anda buat");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(C_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapTitle.add(lblTitle);
        wrapTitle.add(Box.createVerticalStrut(4));
        wrapTitle.add(lblSub);

        JPanel wrapBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        wrapBtns.setOpaque(false);

        btnRefreshRiwayat = buatTombol("Refresh", new Color(241, 245, 249), C_TEXT_MAIN, new Color(226, 232, 240));
        btnRefreshRiwayat.setPreferredSize(new Dimension(100, 40));

        btnDetailRiwayat = buatTombol("Lihat Detail", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnDetailRiwayat.setPreferredSize(new Dimension(130, 40));
        btnDetailRiwayat.setEnabled(false);

        wrapBtns.add(btnRefreshRiwayat);
        wrapBtns.add(btnDetailRiwayat);

        header.add(wrapTitle, BorderLayout.WEST);
        header.add(wrapBtns, BorderLayout.EAST);

        String[] kolom = {"ID Jual", "Tanggal", "Customer", "Jml Item", "Total Bayar"};
        modelRiwayat = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblRiwayat = new JTable(modelRiwayat);
        tblRiwayat.setRowHeight(44);
        tblRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblRiwayat.setForeground(C_TEXT_MAIN);
        tblRiwayat.setSelectionBackground(new Color(239, 246, 255));
        tblRiwayat.setSelectionForeground(C_BRAND);
        tblRiwayat.setShowVerticalLines(false);
        tblRiwayat.setGridColor(C_BORDER);
        tblRiwayat.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader th = tblRiwayat.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(C_TEXT_MUTED);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < tblRiwayat.getColumnCount(); i++) {
            tblRiwayat.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        tblRiwayat.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRiwayat.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblRiwayat.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblRiwayat.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblRiwayat.getColumnModel().getColumn(4).setPreferredWidth(150);

        tblRiwayat.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                btnDetailRiwayat.setEnabled(tblRiwayat.getSelectedRow() >= 0);
                if (e.getClickCount() == 2) bukaDetailRiwayat();
            }
        });

        JScrollPane sp = new JScrollPane(tblRiwayat);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 14));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));

        JLabel lblInfo = new JLabel("Tip: Double-click pada baris tabel untuk melihat detail item yang dibeli");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(C_TEXT_MUTED);
        footer.add(lblInfo);

        card.add(footer, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);

        btnRefreshRiwayat.addActionListener(e -> muatRiwayat());
        btnDetailRiwayat.addActionListener(e  -> bukaDetailRiwayat());

        return panel;
    }

    // ── Muat Data ─────────────────────────────────────────────
    private void muatCustomer() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem(new Customer("", "-- Pelanggan Umum --", "", ""));
        for (Customer c : customerDAO.getAll()) cmbCustomer.addItem(c);
    }

    private void muatRiwayat() {
        modelRiwayat = penjualanDAO.getRiwayatSaya(idUserLogin);
        tblRiwayat.setModel(modelRiwayat);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < tblRiwayat.getColumnCount(); i++) {
            tblRiwayat.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        tblRiwayat.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRiwayat.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblRiwayat.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblRiwayat.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblRiwayat.getColumnModel().getColumn(4).setPreferredWidth(150);
        btnDetailRiwayat.setEnabled(false);
    }

    // ── Buka Dialog Detail Riwayat ────────────────────────────
    private void bukaDetailRiwayat() {
        int row = tblRiwayat.getSelectedRow();
        if (row < 0) return;
        int idJual = (int) modelRiwayat.getValueAt(row, 0);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogDetailTransaksi(parent, idJual).setVisible(true);
    }

    // ── Buka Dialog Cari Barang ───────────────────────────────
    private void bukaDialogCariBarang() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogCariBarang dialog = new DialogCariBarang(parent);
        dialog.setVisible(true);

        Barang hasil = dialog.getBarangDipilih();
        if (hasil != null) {
            barangDipilih = hasil;
            txtNamaBarang.setText(hasil.getNamaBarang());
            txtHarga.setText(String.valueOf(hasil.getHargaJual()));

            int stok = hasil.getStok();
            txtInfoStok.setText(stok + " pcs");
            txtInfoStok.setForeground(stok <= 5 ? C_RED : C_GREEN);

            txtJumlah.setText("");
            txtJumlah.requestFocus();
            updateGrandTotal();
        }
    }

    // ── Hitung Total Sementara ────────────────────────────────
    private void hitungTotalSementara() {
        try {
            double harga  = Double.parseDouble(txtHarga.getText().trim());
            int jumlah    = txtJumlah.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtJumlah.getText().trim());
            double total  = hitungTotalKeranjang() + (harga * jumlah);
            lblGrandTotal.setText("Rp " + String.format("%,.0f", total).replace(",", "."));
        } catch (NumberFormatException e) { /* abaikan */ }
    }

    // ── Tambah ke Keranjang ───────────────────────────────────
    private void tambahKeKeranjang() {
        if (barangDipilih == null) {
            tampilPesanModern("Validasi", "Pilih barang terlebih dahulu!<br>Klik tombol Cari.", true);
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tampilPesanModern("Validasi", "Masukkan jumlah yang valid (angka lebih dari 0)!", true);
            txtJumlah.requestFocus();
            return;
        }

        Barang stokCek = barangDAO.getById(barangDipilih.getIdBarang());
        if (stokCek == null) return;

        int sudahDiKeranjang = 0;
        for (Object[] item : keranjang) {
            if (item[0].equals(barangDipilih.getIdBarang()))
                sudahDiKeranjang += (int) item[2];
        }

        int sisaStok = stokCek.getStok() - sudahDiKeranjang;
        if (sisaStok <= 0) {
            tampilPesanModern("Stok Habis", 
                "Stok barang ini sudah habis di keranjang!<br>" +
                "Stok total di toko: <b>" + stokCek.getStok() + " pcs</b><br>" +
                "Sudah di keranjang: <b>" + sudahDiKeranjang + " pcs</b>", true);
            return;
        }
        if (jumlah > sisaStok) {
            tampilPesanModern("Stok Tidak Cukup", 
                "Jumlah melebihi stok tersedia!<br>" +
                "Sisa tersedia : <b>" + sisaStok + " pcs</b><br>" +
                "Jumlah diminta: <b>" + jumlah + " pcs</b>", true);
            return;
        }

        double harga    = Double.parseDouble(txtHarga.getText().trim());
        double subtotal = harga * jumlah;

        keranjang.add(new Object[]{
            barangDipilih.getIdBarang(),
            barangDipilih.getNamaBarang(),
            jumlah, harga, subtotal
        });

        modelKeranjang.addRow(new Object[]{
            modelKeranjang.getRowCount() + 1,
            barangDipilih.getIdBarang(),
            barangDipilih.getNamaBarang(),
            "Rp " + String.format("%,.0f", harga).replace(",", "."),
            jumlah,
            "Rp " + String.format("%,.0f", subtotal).replace(",", ".")
        });

        updateGrandTotal();
        txtJumlah.setText("");
        txtNamaBarang.setText("");
        txtHarga.setText("");
        txtInfoStok.setText("");
        barangDipilih = null;
        btnCariBarang.requestFocus();
    }

    // ── Hapus Item Keranjang ──────────────────────────────────
    private void hapusItemKeranjang() {
        int row = tblKeranjang.getSelectedRow();
        if (row < 0) return;
        keranjang.remove(row);
        modelKeranjang.removeRow(row);
        for (int i = 0; i < modelKeranjang.getRowCount(); i++)
            modelKeranjang.setValueAt(i + 1, i, 0);
        updateGrandTotal();
        btnHapusItem.setEnabled(false);
    }

    // ── Update Grand Total ────────────────────────────────────
    private void updateGrandTotal() {
        lblGrandTotal.setText("Rp " + String.format("%,.0f", hitungTotalKeranjang()).replace(",", "."));
    }

    private double hitungTotalKeranjang() {
        double total = 0;
        for (Object[] item : keranjang) total += (double) item[4];
        return total;
    }

    // ── Simpan Transaksi ──────────────────────────────────────
    private void simpanTransaksi() {
        Customer customer = (Customer) cmbCustomer.getSelectedItem();

        if (keranjang.isEmpty()) {
            tampilPesanModern("Validasi", "Keranjang belanja masih kosong!<br>Tambahkan minimal satu barang.", true);
            return;
        }

        double grandTotal = hitungTotalKeranjang();
        String namaPembeli = (customer != null && !customer.getIdCustomer().isEmpty()) 
                             ? customer.getNamaCustomer() : "Pelanggan Umum";
        String idCustToSave = (customer != null && !customer.getIdCustomer().isEmpty()) 
                               ? customer.getIdCustomer() : null;

        StringBuilder sb = new StringBuilder();
        for (Object[] item : keranjang) {
            sb.append("- ").append(item[1])
              .append("  x").append(item[2])
              .append("  =  Rp ").append(String.format("%,.0f", (double) item[4]).replace(",", "."))
              .append("\n");
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogKonfirmasiTransaksi dialog = new DialogKonfirmasiTransaksi(parent, grandTotal, namaPembeli, sb.toString().trim());
        dialog.setVisible(true);

        if (!dialog.isDikonfirmasi()) return;

        long kembalian = dialog.getKembalian();
        List<Object[]> itemsDAO = new ArrayList<>();
        for (Object[] item : keranjang) {
            itemsDAO.add(new Object[]{item[0], item[2], item[3], item[4]});
        }

        boolean berhasil = penjualanDAO.simpanTransaksi(idCustToSave, idUserLogin, grandTotal, itemsDAO);

        if (berhasil) {
            tampilPesanModern("Transaksi Berhasil", 
                "Transaksi atas nama <b>" + namaPembeli + "</b> sukses disimpan.<br><br>" +
                "Total Belanja  : Rp " + String.format("%,.0f", grandTotal).replace(",", ".") + "<br>" +
                "Uang Bayar     : Rp " + String.format("%,.0f", (double) dialog.getUangBayar()).replace(",", ".") + "<br>" +
                "Kembalian      : Rp " + String.format("%,.0f", (double) kembalian).replace(",", "."), false);
            resetForm();
            tabbedPane.setSelectedIndex(1);
        } else {
            tampilPesanModern("Error", "Transaksi gagal disimpan ke database! Coba lagi.", true);
        }
    }

    // ── Reset Form ────────────────────────────────────────────
    private void resetForm() {
        keranjang.clear();
        modelKeranjang.setRowCount(0);
        txtJumlah.setText("");
        txtHarga.setText("");
        txtInfoStok.setText("");
        txtNamaBarang.setText("");
        barangDipilih = null;
        lblGrandTotal.setText("Rp 0");
        btnHapusItem.setEnabled(false);
        muatCustomer();
    }

    // ── Helpers ───────────────────────────────────────────────
    private JLabel buatLabel(String teks, int x, int y) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_TEXT_MAIN);
        lbl.setBounds(x, y, 260, 18);
        return lbl;
    }

    private JPanel buatGaris(int x, int y, int lebar) {
        JPanel g = new JPanel();
        g.setBackground(C_BORDER);
        g.setBounds(x, y, lebar, 1);
        return g;
    }

    private JButton buatTombol(String teks, Color bg, Color fg, Color hover) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(bg); }
        });
        return btn;
    }
    
    private void tampilPesanModern(String judul, String pesan, boolean isError) {
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>" + pesan + "</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);
        
        showModernModal(judul, content, null, "Tutup", isError ? C_RED : C_BRAND, isError ? C_RED_HOVER : C_BRAND_HOVER, false);
    }
    
    private void showModernModal(String title, JPanel content, ActionListener onConfirm, String confirmBtnText, Color confirmBtnColor, Color confirmBtnHover, boolean showCancel) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) parentWindow, title, true);
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(C_TEXT_MAIN);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 24, 0));
        centerPanel.add(content, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);

        if (showCancel) {
            JButton btnCancel = buatTombol("Batal", new Color(241, 245, 249), C_TEXT_MAIN, new Color(226, 232, 240));
            btnCancel.setPreferredSize(new Dimension(100, 38));
            btnCancel.addActionListener(e -> dialog.dispose());
            footer.add(btnCancel);
        }

        JButton btnConfirm = buatTombol(confirmBtnText, confirmBtnColor, Color.WHITE, confirmBtnHover);
        btnConfirm.setPreferredSize(new Dimension(140, 38));
        btnConfirm.addActionListener(e -> {
            dialog.dispose();
            if(onConfirm != null) onConfirm.actionPerformed(e);
        });
        footer.add(btnConfirm);

        mainPanel.add(footer, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
    }
}