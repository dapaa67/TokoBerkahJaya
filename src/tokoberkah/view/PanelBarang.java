package tokoberkah.view;

import tokoberkah.dao.BarangDAO;
import tokoberkah.dao.KategoriDAO;
import tokoberkah.model.Barang;
import tokoberkah.model.Kategori;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PanelBarang extends JPanel {

    // ── Komponen ──────────────────────────────────────────────
    private JTextField  txtId, txtNama, txtSatuan, txtHarga, txtStok, txtCari;
    private JButton     btnSimpan, btnUpdate, btnHapus, btnBatal, btnCari, btnTambahKategori, btnHapusKategori;
    private JComboBox<Kategori> cmbKategori, cmbKategoriForm;
    private JTable      tabel;
    private DefaultTableModel modelTabel;

    // ── DAO ───────────────────────────────────────────────────
    private final String level;
    private final BarangDAO dao = new BarangDAO();
    private final KategoriDAO daoKategori = new KategoriDAO();

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

    public PanelBarang(String level) {
        this.level = level;
        setLayout(new BorderLayout());
        setBackground(C_BG);
        add(buildTopbar(), BorderLayout.NORTH);
        add(buildKonten(), BorderLayout.CENTER);
        muatKategori();
        muatData();
    }

    // ── Topbar ────────────────────────────────────────────────
    private JPanel buildTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(C_CARD_BG);
        top.setPreferredSize(new Dimension(0, 84)); // Sedikit dilebarkan
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        // Gunakan BoxLayout secara vertikal agar teks tidak terpotong (seperti di GridLayout)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("Data Barang");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(C_TEXT_MAIN);
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        String modeText = level.equals("Admin")
            ? "Kelola data inventaris dan barang toko Anda."
            : "Kelola data barang toko (Mode: Lihat Saja)";
        JLabel lblSub = new JLabel(modeText);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(level.equals("Admin") ? C_TEXT_MUTED : new Color(217, 119, 6)); // Amber 600 if read only
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblJudul);
        textPanel.add(Box.createVerticalStrut(4)); // Spasi 4px
        textPanel.add(lblSub);

        top.add(textPanel, BorderLayout.WEST);
        return top;
    }

    // ── Konten Utama ──────────────────────────────────────────
    private JPanel buildKonten() {
        JPanel konten = new JPanel(new BorderLayout(24, 0));
        konten.setBackground(C_BG);
        konten.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        konten.add(buildFormPanel(), BorderLayout.WEST);
        konten.add(buildTabelPanel(), BorderLayout.CENTER);
        return konten;
    }

    // ── Form Panel (kiri) ─────────────────────────────────────
    private JPanel buildFormPanel() {
        // Form dengan Rounded Border
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
        card.setPreferredSize(new Dimension(300, 0)); // Lebar tetap form
        card.setBorder(BorderFactory.createEmptyBorder(1,1,1,1)); // Biar gak potong custom border

        JLabel lblTitle = new JLabel("Form Input Barang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBounds(20, 20, 230, 20);

        JPanel garis = new JPanel();
        garis.setBackground(C_BORDER);
        garis.setBounds(20, 50, 260, 1);

        // Input Fields (diubah spacing dan bounds-nya)
        int y = 64;
        String[] labels = {"ID Barang", "Nama Barang", "Satuan", "Harga Jual", "Stok"};

        txtId     = buatField(card, labels[0], y);      y += 66;
        txtNama   = buatField(card, labels[1], y);      y += 66;
        txtSatuan = buatField(card, labels[2], y);      y += 66;
        txtHarga  = buatField(card, labels[3], y);      y += 66;
        txtStok   = buatField(card, labels[4], y);      y += 66;

        JLabel lblKategori = new JLabel("Kategori");
        lblKategori.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKategori.setForeground(C_TEXT_MAIN);
        lblKategori.setBounds(20, y, 260, 18);
        
        cmbKategoriForm = new JComboBox<>();
        cmbKategoriForm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbKategoriForm.setBackground(Color.WHITE);
        cmbKategoriForm.setBounds(20, y + 20, 172, 38); 

        btnTambahKategori = buatTombol("+", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnTambahKategori.setBounds(198, y + 20, 38, 38);
        btnTambahKategori.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTambahKategori.setToolTipText("Tambah Kategori Baru");
        btnTambahKategori.addActionListener(e -> tambahKategoriBaru());

        btnHapusKategori = buatTombol("-", C_RED, Color.WHITE, C_RED_HOVER);
        btnHapusKategori.setBounds(242, y + 20, 38, 38);
        btnHapusKategori.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnHapusKategori.setToolTipText("Hapus Kategori Terpilih");
        btnHapusKategori.addActionListener(e -> hapusKategoriTerpilih());

        card.add(lblKategori);
        card.add(cmbKategoriForm);
        card.add(btnTambahKategori);
        card.add(btnHapusKategori);
        y += 76;
        
        // Validasi Anti Simbol (Berdasarkan instruksi Dosen)
        // Filter input ID Barang (Hanya huruf, angka, dan strip)
        txtId.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != '-' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    tampilError("Karakter '" + c + "' tidak diizinkan! ID Barang hanya boleh huruf, angka, dan strip (-).", txtId);
                }
            }
        });

        // Filter input Nama Barang (Cegah simbol berbahaya @#$! dll)
        txtNama.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String badChars = "@#$!<>%^&*()+=_{}[]|\\:;\"'?/~`";
                if (badChars.indexOf(c) >= 0) {
                    e.consume();
                    tampilError("Simbol '" + c + "' dilarang digunakan pada Nama Barang!", txtNama);
                }
            }
        });

        // Filter input Satuan (Hanya huruf dan spasi)
        txtSatuan.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    tampilError("Karakter '" + c + "' tidak diizinkan! Satuan hanya boleh huruf.", txtSatuan);
                }
            }
        });

        // Filter input harga & stok
        txtHarga.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) e.consume();
                if (c == '.' && txtHarga.getText().contains(".")) e.consume();
            }
        });

        txtStok.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) e.consume();
            }
        });

        // Tombol-tombol Aksi
        btnSimpan = buatTombol("Simpan", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnSimpan.setBounds(20, y, 125, 42);

        btnBatal  = buatTombol("Batal", new Color(241, 245, 249), C_TEXT_MAIN, new Color(226, 232, 240)); // Slate 100
        btnBatal.setBounds(155, y, 125, 42);
        y += 52;

        btnUpdate = buatTombol("Update", C_GREEN, Color.WHITE, C_GREEN_HOVER);
        btnUpdate.setBounds(20, y, 125, 42);
        btnUpdate.setEnabled(false);

        btnHapus  = buatTombol("Hapus", C_RED, Color.WHITE, C_RED_HOVER);
        btnHapus.setBounds(155, y, 125, 42);
        btnHapus.setEnabled(false);

        card.add(lblTitle);
        card.add(garis);
        card.add(btnSimpan);
        card.add(btnBatal);
        card.add(btnUpdate);
        card.add(btnHapus);

        // Listener tombol
        btnSimpan.addActionListener(e -> simpanBarang());
        btnUpdate.addActionListener(e -> updateBarang());
        btnHapus.addActionListener(e  -> hapusBarang());
        btnBatal.addActionListener(e  -> resetForm());
        
        JRootPane rootPane = SwingUtilities.getRootPane(btnSimpan);
        if (rootPane != null) rootPane.setDefaultButton(btnSimpan);
        
        ActionListener enterSimpan = e -> { if (btnSimpan.isEnabled()) simpanBarang(); };
        txtId.addActionListener(enterSimpan);
        txtNama.addActionListener(enterSimpan);
        txtSatuan.addActionListener(enterSimpan);
        txtHarga.addActionListener(enterSimpan);
        txtStok.addActionListener(enterSimpan);

        if (!level.equals("Admin")) {
            btnSimpan.setVisible(false);
            btnUpdate.setVisible(false);
            btnHapus.setVisible(false);
            btnBatal.setVisible(false);

            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(new Color(254, 243, 199)); // Amber 50
            infoPanel.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1)); // Amber 300
            infoPanel.setBounds(20, y - 20, 260, 42);
            
            JLabel lblInfo = new JLabel("Mode: Lihat Saja (Read Only)");
            lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblInfo.setForeground(new Color(180, 83, 9)); // Amber 700
            lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
            infoPanel.add(lblInfo, BorderLayout.CENTER);
            card.add(infoPanel);

            txtId.setEditable(false);
            txtNama.setEditable(false);
            txtSatuan.setEditable(false);
            txtHarga.setEditable(false);
            txtStok.setEditable(false);
            txtId.setBackground(new Color(248, 250, 252));
            txtNama.setBackground(new Color(248, 250, 252));
            txtSatuan.setBackground(new Color(248, 250, 252));
            txtHarga.setBackground(new Color(248, 250, 252));
            txtStok.setBackground(new Color(248, 250, 252));
            cmbKategoriForm.setEnabled(false);
            btnTambahKategori.setEnabled(false);
            btnHapusKategori.setEnabled(false);
        }

        return card;
    }

    // Helper buat field
    private JTextField buatField(JPanel parent, String label, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_TEXT_MAIN);
        lbl.setBounds(20, y, 260, 18);

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.putClientProperty("JTextField.placeholderText", "Masukkan " + label.toLowerCase());
        txt.setBounds(20, y + 20, 260, 38);

        parent.add(lbl);
        parent.add(txt);
        return txt;
    }

    // Helper buat tombol
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

    // ── Tabel Panel (kanan) ───────────────────────────────────
    private JPanel buildTabelPanel() {
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

        // Header tabel + search
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblTitle = new JLabel("Daftar Barang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16)); // Margin kanan biar gak nabrak

        JPanel panelKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelKanan.setOpaque(false);

        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKategori.setBackground(Color.WHITE);
        cmbKategori.setPreferredSize(new Dimension(140, 38));
        cmbKategori.addActionListener(e -> {
            Kategori k = (Kategori) cmbKategori.getSelectedItem();
            if (k != null) filterBerdasarkanKategori(k.getIdKategori());
        });

        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.putClientProperty("JTextField.placeholderText", "Cari nama...");
        txtCari.setPreferredSize(new Dimension(180, 38));

        btnCari = buatTombol("Cari", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnCari.setPreferredSize(new Dimension(80, 38));

        panelKanan.add(cmbKategori);
        panelKanan.add(txtCari);
        panelKanan.add(btnCari);

        // Pasang judul di CENTER agar otomatis terpotong pakai elipsis (...) jika ruang terlalu sempit
        header.add(lblTitle, BorderLayout.CENTER);
        header.add(panelKanan, BorderLayout.EAST);

        // Tabel
        String[] kolom = {"ID", "Nama Barang", "Satuan", "Harga Jual", "Stok"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabel = new JTable(modelTabel);
        tabel.setRowHeight(44);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabel.setForeground(C_TEXT_MAIN);
        
        tabel.setSelectionBackground(new Color(239, 246, 255)); // Blue 50
        tabel.setSelectionForeground(C_BRAND);
        
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(C_BORDER);
        tabel.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader th = tabel.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(C_TEXT_MUTED);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for(int i=0; i<tabel.getColumnCount(); i++){
            tabel.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        tabel.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(60);

        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { isiFormDariTabel(); }
        });

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        btnCari.addActionListener(e -> cariBarang());
        txtCari.addActionListener(e -> cariBarang());

        return card;
    }

    // ── Muat Data & Kategori ──────────────────────────────────
    private void muatKategori() {
        cmbKategori.removeAllItems();
        cmbKategoriForm.removeAllItems();

        cmbKategori.addItem(new Kategori(0, "Semua Kategori"));
        cmbKategoriForm.addItem(new Kategori(0, "Pilih Kategori"));

        List<Kategori> list = daoKategori.getAll();
        for (Kategori k : list) {
            cmbKategori.addItem(k);
            cmbKategoriForm.addItem(k);
        }
    }

    private void tambahKategoriBaru() {
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        
        JLabel lbl = new JLabel("Nama Kategori Baru:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MUTED);
        
        JTextField txtInput = new JTextField();
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtInput.setPreferredSize(new Dimension(320, 42));
        txtInput.putClientProperty("JTextField.placeholderText", "Contoh: Makanan, Minuman...");
        
        content.add(lbl, BorderLayout.NORTH);
        content.add(txtInput, BorderLayout.CENTER);

        showModernModal("Tambah Kategori", content, e -> {
            String nama = txtInput.getText().trim();
            if (!nama.isEmpty()) {
                if (daoKategori.insert(nama)) {
                    muatKategori(); 
                    for (int i = 0; i < cmbKategoriForm.getItemCount(); i++) {
                        if (cmbKategoriForm.getItemAt(i).getNamaKategori().equalsIgnoreCase(nama)) {
                            cmbKategoriForm.setSelectedIndex(i);
                            break;
                        }
                    }
                    tampilPesanModern("Sukses", "Kategori baru berhasil ditambahkan!", false);
                } else {
                    tampilPesanModern("Error", "Gagal menambah kategori!", true);
                }
            }
        }, "Simpan Kategori", C_BRAND, C_BRAND_HOVER, true);
    }

    private void muatData() {
        if (cmbKategori != null && cmbKategori.getSelectedItem() != null) {
            Kategori k = (Kategori) cmbKategori.getSelectedItem();
            filterBerdasarkanKategori(k.getIdKategori());
        } else {
            filterBerdasarkanKategori(0);
        }
    }

    private void filterBerdasarkanKategori(int idKategori) {
        modelTabel.setRowCount(0);
        List<Barang> list;
        if (idKategori == 0) {
            list = dao.getAll();
        } else {
            list = dao.getByKategori(idKategori);
        }

        for (Barang b : list) {
            modelTabel.addRow(new Object[]{
                b.getIdBarang(),
                b.getNamaBarang(),
                b.getSatuan(),
                "Rp " + String.format("%,.0f", b.getHargaJual()),
                b.getStok()
            });
        }
    }

    // ── Isi Form dari Baris Terpilih ──────────────────────────
    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        String idBarang = modelTabel.getValueAt(row, 0).toString();
        Barang b = dao.getById(idBarang);
        if (b == null) return;

        txtId.setText(b.getIdBarang());
        txtNama.setText(b.getNamaBarang());
        txtSatuan.setText(b.getSatuan());
        txtHarga.setText(String.valueOf(b.getHargaJual()));
        txtStok.setText(String.valueOf(b.getStok()));

        int idKat = b.getIdKategori();
        boolean found = false;
        for (int i = 1; i < cmbKategoriForm.getItemCount(); i++) {
            if (cmbKategoriForm.getItemAt(i).getIdKategori() == idKat) {
                cmbKategoriForm.setSelectedIndex(i);
                found = true;
                break;
            }
        }
        if (!found) cmbKategoriForm.setSelectedIndex(0);

        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 245, 245));

        btnSimpan.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    // ── Hapus Kategori Terpilih ───────────────────────────────
    private void hapusKategoriTerpilih() {
        Kategori k = (Kategori) cmbKategoriForm.getSelectedItem();
        if (k == null || k.getIdKategori() == 0) return;

        int jumlahBarangTerkait = daoKategori.cekBarangTerkait(k.getIdKategori());
        if (jumlahBarangTerkait > 0) {
            tampilPesanModern("Validasi Keamanan",
                "Kategori <b>" + k.getNamaKategori() + "</b> sedang digunakan oleh " + jumlahBarangTerkait + " barang.<br>" +
                "Harap ubah kategori barang-barang tersebut sebelum menghapus kategori ini.", true);
            return;
        }

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        
        JLabel lbl = new JLabel("<html>Yakin ingin menghapus kategori <b>" + k.getNamaKategori() + "</b>?<br><span style='color:#dc2626;'>Data yang dihapus tidak bisa dikembalikan.</span></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Hapus", content, e -> {
            boolean berhasil = daoKategori.hapus(k.getIdKategori());
            if (berhasil) {
                tampilPesanModern("Sukses", "Kategori berhasil dihapus!", false);
                muatKategori();
            } else {
                tampilPesanModern("Error", "Gagal menghapus kategori!", true);
            }
        }, "Hapus Kategori", C_RED, C_RED_HOVER, true);
    }

    // ── CRUD Actions ──────────────────────────────────────────
    private void simpanBarang() {
        if (!validasiForm()) return;

        Barang b = ambilDariForm();
        if (dao.getById(b.getIdBarang()) != null) {
            tampilPesanModern("Duplikat", "ID Barang <b>" + b.getIdBarang() + "</b> sudah ada! Gunakan ID lain.", true);
            return;
        }

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Yakin ingin menyimpan barang <b>" + b.getNamaBarang() + "</b>?</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Simpan", content, e -> {
            if (dao.insert(b)) {
                tampilPesanModern("Sukses", "Barang berhasil ditambahkan!", false);
                muatData();
                resetForm();
            } else {
                tampilPesanModern("Error", "Gagal menambahkan barang!", true);
            }
        }, "Simpan", C_BRAND, C_BRAND_HOVER, true);
    }

    private void updateBarang() {
        if (!validasiForm()) return;

        Barang b = ambilDariForm();

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Yakin ingin menyimpan perubahan pada barang <b>" + txtId.getText() + "</b>?</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Update", content, e -> {
            if (dao.update(b)) {
                tampilPesanModern("Sukses", "Data berhasil diupdate!", false);
                muatData();
                resetForm();
            } else {
                tampilPesanModern("Error", "Gagal mengupdate data!", true);
            }
        }, "Update", C_GREEN, C_GREEN_HOVER, true);
    }

    private void hapusBarang() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        String id = txtId.getText();

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Yakin ingin menghapus barang <b>" + id + "</b>?<br><span style='color:#dc2626;'>Data tidak bisa dikembalikan!</span></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Hapus", content, e -> {
            if (dao.delete(id)) {
                tampilPesanModern("Sukses", "Barang berhasil dihapus!", false);
                muatData();
                resetForm();
            } else {
                tampilPesanModern("Error", "Gagal menghapus! Mungkin barang ini<br>sudah pernah dipakai di transaksi.", true);
            }
        }, "Hapus Barang", C_RED, C_RED_HOVER, true);
    }

    private void cariBarang() {
        String kata = txtCari.getText().trim().toLowerCase();
        int idKategori = 0;
        if (cmbKategori != null && cmbKategori.getSelectedItem() != null) {
            idKategori = ((Kategori) cmbKategori.getSelectedItem()).getIdKategori();
        }
        
        modelTabel.setRowCount(0);
        
        List<Barang> listSumber = (idKategori == 0) ? dao.getAll() : dao.getByKategori(idKategori);
        
        for (Barang b : listSumber) {
            if (b.getNamaBarang().toLowerCase().contains(kata)
             || b.getIdBarang().toLowerCase().contains(kata)) {
                modelTabel.addRow(new Object[]{
                    b.getIdBarang(),
                    b.getNamaBarang(),
                    b.getSatuan(),
                    "Rp " + String.format("%,.0f", b.getHargaJual()),
                    b.getStok()
                });
            }
        }
    }

    // ── Helper ────────────────────────────────────────────────
    private Barang ambilDariForm() {
        Barang b = new Barang();
        b.setIdBarang(txtId.getText().trim());
        b.setNamaBarang(txtNama.getText().trim());
        b.setSatuan(txtSatuan.getText().trim());
        b.setHargaJual(Double.parseDouble(txtHarga.getText().trim()));
        b.setStok(Integer.parseInt(txtStok.getText().trim()));
        
        Kategori k = (Kategori) cmbKategoriForm.getSelectedItem();
        b.setIdKategori(k != null && k.getIdKategori() > 0 ? k.getIdKategori() : 1); 
        return b;
    }

    private boolean validasiForm() {
        String id     = txtId.getText().trim();
        String nama   = txtNama.getText().trim();
        String satuan = txtSatuan.getText().trim();
        String harga  = txtHarga.getText().trim();
        String stok   = txtStok.getText().trim();

        if (id.isEmpty() || nama.isEmpty() || satuan.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
            tampilError("Semua field wajib diisi!", txtId);
            return false;
        }

        if (cmbKategoriForm.getSelectedIndex() == 0) {
            tampilPesanModern("Validasi", "Silakan pilih kategori terlebih dahulu!", true);
            cmbKategoriForm.requestFocus();
            return false;
        }

        if (id.length() > 10) {
            tampilError("ID Barang maksimal 10 karakter!", txtId);
            return false;
        }

        try {
            double h = Double.parseDouble(harga);
            if (h <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tampilError("Harga harus angka lebih dari 0!", txtHarga);
            return false;
        }

        try {
            int s = Integer.parseInt(stok);
            if (s < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tampilError("Stok harus angka 0 atau lebih!", txtStok);
            return false;
        }

        return true;
    }

    private void tampilError(String pesan, JTextField field) {
        tampilPesanModern("Perhatian", pesan, true);
        field.setBackground(new Color(254, 226, 226)); // Red 100
        field.requestFocus();
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                field.setBackground(Color.WHITE);
            }
        });
    }
    
    // Helper untuk Dialog Custom Modern
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

    private void resetForm() {
        txtId.setText("");      txtId.setEditable(true);
        txtId.setBackground(Color.WHITE);
        txtNama.setText("");    txtSatuan.setText("");
        txtHarga.setText("");   txtStok.setText("");
        cmbKategoriForm.setSelectedIndex(0);
        txtCari.setText("");
        tabel.clearSelection();
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        muatData();
    }
}
