package tokoberkah.view;

import tokoberkah.dao.CustomerDAO;
import tokoberkah.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;

public class PanelCustomer extends JPanel {

    // ── Komponen ──────────────────────────────────────────────
    private JTextField txtId, txtNama, txtAlamat, txtTelepon, txtCari;
    private JButton    btnSimpan, btnUpdate, btnHapus, btnBatal, btnCari;
    private JTable     tabel;
    private DefaultTableModel modelTabel;

    // ── DAO ───────────────────────────────────────────────────
    private final CustomerDAO dao = new CustomerDAO();

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

    public PanelCustomer() {
        setLayout(new BorderLayout());
        setBackground(C_BG);
        add(buildTopbar(),  BorderLayout.NORTH);
        add(buildKonten(), BorderLayout.CENTER);
        muatData();
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

        JLabel lblJudul = new JLabel("Data Customer");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(C_TEXT_MAIN);
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Kelola informasi dan kontak pelanggan toko Anda.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(C_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblJudul);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblSub);

        top.add(textPanel, BorderLayout.WEST);
        return top;
    }

    // ── Konten Utama ──────────────────────────────────────────
    private JPanel buildKonten() {
        JPanel konten = new JPanel(new BorderLayout(24, 0));
        konten.setBackground(C_BG);
        konten.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        konten.add(buildFormPanel(),  BorderLayout.WEST);
        konten.add(buildTabelPanel(), BorderLayout.CENTER);
        return konten;
    }

    // ── Form Panel (kiri) ─────────────────────────────────────
    private JPanel buildFormPanel() {
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
        card.setPreferredSize(new Dimension(300, 0));
        card.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));

        JLabel lblTitle = new JLabel("Form Input Customer");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBounds(20, 20, 230, 20);

        JPanel garis = new JPanel();
        garis.setBackground(C_BORDER);
        garis.setBounds(20, 50, 260, 1);

        int y = 64;
        txtId      = buatField(card, "ID Customer", y);   y += 66;
        txtNama    = buatField(card, "Nama Customer", y); y += 66;
        txtAlamat  = buatField(card, "Alamat", y);        y += 66;
        txtTelepon = buatField(card, "No. Telepon", y);   y += 76;

        // Validasi Anti Simbol (Berdasarkan instruksi Dosen)
        // Filter input ID Customer (hanya huruf, angka, dan strip)
        txtId.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != '-' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    tampilError("Karakter '" + c + "' tidak diizinkan! ID Customer hanya boleh huruf, angka, dan strip (-).", txtId);
                }
            }
        });

        // Filter input Nama Customer (cegah simbol berbahaya @#$! dll)
        txtNama.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String badChars = "@#$!<>%^&*()+=_{}[]|\\:;\"?/~`";
                if (badChars.indexOf(c) >= 0) {
                    e.consume();
                    tampilError("Simbol '" + c + "' dilarang digunakan pada Nama Customer!", txtNama);
                }
            }
        });

        // Filter input Alamat (cegah injeksi basic seperti tag HTML / SQL)
        txtAlamat.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == '<' || c == '>' || c == ';' || c == '\\' || c == '\'' || c == '\"') {
                    e.consume();
                    tampilError("Karakter '" + c + "' dilarang digunakan pada Alamat karena alasan keamanan!", txtAlamat);
                }
            }
        });

        txtTelepon.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });

        // Tombol
        btnSimpan = buatTombol("Simpan", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnSimpan.setBounds(20, y, 125, 42);

        btnBatal  = buatTombol("Batal", new Color(241, 245, 249), C_TEXT_MAIN, new Color(226, 232, 240));
        btnBatal.setBounds(155, y, 125, 42);
        y += 52;

        btnUpdate = buatTombol("Update", C_GREEN, Color.WHITE, C_GREEN_HOVER);
        btnUpdate.setBounds(20, y, 125, 42);
        btnUpdate.setEnabled(false);

        btnHapus  = buatTombol("Hapus",  C_RED,   Color.WHITE, C_RED_HOVER);
        btnHapus.setBounds(155, y, 125, 42);
        btnHapus.setEnabled(false);

        card.add(lblTitle);
        card.add(garis);
        card.add(btnSimpan);
        card.add(btnBatal);
        card.add(btnUpdate);
        card.add(btnHapus);

        btnSimpan.addActionListener(e -> simpanCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnHapus.addActionListener(e  -> hapusCustomer());
        btnBatal.addActionListener(e  -> resetForm());

        JRootPane rootPane = SwingUtilities.getRootPane(btnSimpan);
        if (rootPane != null) rootPane.setDefaultButton(btnSimpan);

        ActionListener enterSimpan = e -> {
            if (btnSimpan.isEnabled()) simpanCustomer();
        };
        txtId.addActionListener(enterSimpan);
        txtNama.addActionListener(enterSimpan);
        txtAlamat.addActionListener(enterSimpan);
        txtTelepon.addActionListener(enterSimpan);

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

    // Helper buat tombol modern
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

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblTitle = new JLabel("Daftar Customer");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));

        JPanel panelKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelKanan.setOpaque(false);

        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCari.putClientProperty("JTextField.placeholderText", "Cari nama atau ID...");
        txtCari.setPreferredSize(new Dimension(200, 38));

        btnCari = buatTombol("Cari", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnCari.setPreferredSize(new Dimension(80, 38));

        panelKanan.add(txtCari);
        panelKanan.add(btnCari);

        header.add(lblTitle, BorderLayout.CENTER);
        header.add(panelKanan, BorderLayout.EAST);

        String[] kolom = {"ID Customer", "Nama Customer", "Alamat", "No. Telepon"};
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

        tabel.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(240);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(120);

        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                isiFormDariTabel();
            }
        });

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        btnCari.addActionListener(e -> cariCustomer());
        txtCari.addActionListener(e -> cariCustomer());

        return card;
    }

    // ── Muat Data ─────────────────────────────────────────────
    private void muatData() {
        modelTabel.setRowCount(0);
        for (Customer c : dao.getAll()) {
            modelTabel.addRow(new Object[]{
                c.getIdCustomer(),
                c.getNamaCustomer(),
                c.getAlamat(),
                c.getTelepon()
            });
        }
    }

    // ── Isi Form dari Baris Terpilih ──────────────────────────
    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        String id = modelTabel.getValueAt(row, 0).toString();
        Customer c = dao.getById(id);
        if (c == null) return;

        txtId.setText(c.getIdCustomer());
        txtNama.setText(c.getNamaCustomer());
        txtAlamat.setText(c.getAlamat());
        txtTelepon.setText(c.getTelepon());

        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 245, 245));

        btnSimpan.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    // ── CRUD ──────────────────────────────────────────────────
    private void simpanCustomer() {
        if (!validasiForm()) return;

        Customer c = ambilDariForm();
        if (dao.getById(c.getIdCustomer()) != null) {
            tampilPesanModern("Duplikat", "ID Customer <b>" + c.getIdCustomer() + "</b> sudah terdaftar! Gunakan ID lain.", true);
            return;
        }

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Yakin ingin menyimpan data customer <b>" + c.getNamaCustomer() + "</b>?</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Simpan", content, e -> {
            if (dao.insert(c)) {
                tampilPesanModern("Sukses", "Customer berhasil ditambahkan!", false);
                muatData();
                resetForm();
            } else {
                tampilPesanModern("Error", "Gagal menambahkan customer!", true);
            }
        }, "Simpan", C_BRAND, C_BRAND_HOVER, true);
    }

    private void updateCustomer() {
        if (!validasiForm()) return;

        Customer c = ambilDariForm();
        
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Yakin ingin menyimpan perubahan data customer <b>" + txtId.getText() + "</b>?</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Update", content, e -> {
            if (dao.update(c)) {
                tampilPesanModern("Sukses", "Data customer berhasil diupdate!", false);
                muatData();
                resetForm();
            } else {
                tampilPesanModern("Error", "Gagal mengupdate data!", true);
            }
        }, "Update", C_GREEN, C_GREEN_HOVER, true);
    }

    private void hapusCustomer() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        String id = txtId.getText();

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Yakin ingin menghapus customer <b>" + id + "</b>?<br><span style='color:#dc2626;'>Data tidak bisa dikembalikan!</span></html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);

        showModernModal("Konfirmasi Hapus", content, e -> {
            if (dao.delete(id)) {
                tampilPesanModern("Sukses", "Customer berhasil dihapus!", false);
                muatData();
                resetForm();
            } else {
                tampilPesanModern("Error", "Gagal menghapus! Customer ini mungkin<br>sudah punya riwayat transaksi.", true);
            }
        }, "Hapus Customer", C_RED, C_RED_HOVER, true);
    }

    private void cariCustomer() {
        String kata = txtCari.getText().trim().toLowerCase();
        modelTabel.setRowCount(0);
        for (Customer c : dao.getAll()) {
            if (c.getNamaCustomer().toLowerCase().contains(kata)
             || c.getIdCustomer().toLowerCase().contains(kata)) {
                modelTabel.addRow(new Object[]{
                    c.getIdCustomer(),
                    c.getNamaCustomer(),
                    c.getAlamat(),
                    c.getTelepon()
                });
            }
        }
    }

    // ── Helper ────────────────────────────────────────────────
    private Customer ambilDariForm() {
        return new Customer(
            txtId.getText().trim(),
            txtNama.getText().trim(),
            txtAlamat.getText().trim(),
            txtTelepon.getText().trim()
        );
    }

    private boolean validasiForm() {
        String id      = txtId.getText().trim();
        String nama    = txtNama.getText().trim();
        String telepon = txtTelepon.getText().trim();

        if (id.isEmpty() || nama.isEmpty() || telepon.isEmpty()) {
            tampilError("ID, Nama, dan Telepon wajib diisi!", txtId);
            return false;
        }

        if (id.length() > 10) {
            tampilError("ID Customer maksimal 10 karakter!", txtId);
            return false;
        }

        if (telepon.length() < 8 || telepon.length() > 15) {
            tampilError("Nomor telepon harus 8-15 digit!", txtTelepon);
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
        txtId.setText("");       txtId.setEditable(true);
        txtId.setBackground(Color.WHITE);
        txtNama.setText("");     txtAlamat.setText("");
        txtTelepon.setText("");  txtCari.setText("");
        tabel.clearSelection();
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        muatData();
    }
}
