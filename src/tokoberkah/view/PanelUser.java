package tokoberkah.view;

import tokoberkah.util.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PanelUser extends JPanel {

    // ── Komponen ──────────────────────────────────────────────
    private JTextField     txtUsername, txtNama;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbLevel;
    private JButton        btnSimpan, btnUpdate, btnHapus, btnBatal;
    private JTable         tabel;
    private DefaultTableModel modelTabel;
    private int            idUserDipilih = -1;

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

    public PanelUser() {
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

        JLabel lblJudul = new JLabel("Manajemen User");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(C_TEXT_MAIN);
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Kelola akses login untuk Admin dan Petugas kasir.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(C_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblJudul);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblSub);

        top.add(textPanel, BorderLayout.WEST);
        return top;
    }

    // ── Konten ────────────────────────────────────────────────
    private JPanel buildKonten() {
        JPanel konten = new JPanel(new BorderLayout(24, 0));
        konten.setBackground(C_BG);
        konten.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        konten.add(buildFormPanel(),  BorderLayout.WEST);
        konten.add(buildTabelPanel(), BorderLayout.CENTER);
        return konten;
    }

    // ── Form Panel ────────────────────────────────────────────
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
        card.setPreferredSize(new Dimension(320, 0));

        JLabel lblTitle = new JLabel("Form Data User");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBounds(24, 20, 230, 20);

        JPanel garis = new JPanel();
        garis.setBackground(C_BORDER);
        garis.setBounds(24, 54, 272, 1);

        int y = 70;

        // Username
        JLabel lblU = buatLabel("Username", 24, y);
        txtUsername = buatTextField(y + 24);
        txtUsername.putClientProperty("JTextField.placeholderText", "Contoh: admin123");
        y += 80;

        // Password
        JLabel lblP = buatLabel("Password", 24, y);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty("JTextField.placeholderText", "********");
        txtPassword.setBounds(24, y + 24, 272, 40);
        y += 80;

        // Nama Lengkap
        JLabel lblN = buatLabel("Nama Lengkap", 24, y);
        txtNama = buatTextField(y + 24);
        txtNama.putClientProperty("JTextField.placeholderText", "Masukkan nama...");
        y += 80;

        // Level
        JLabel lblL = buatLabel("Level Akses", 24, y);
        cmbLevel = new JComboBox<>(new String[]{"Admin", "Petugas"});
        cmbLevel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLevel.setBounds(24, y + 24, 272, 40);
        cmbLevel.setBackground(Color.WHITE);
        y += 76;

        // Garis Pembatas Tombol
        JPanel garisTombol = new JPanel();
        garisTombol.setBackground(C_BORDER);
        garisTombol.setBounds(24, y, 272, 1);
        y += 20;

        // Tombol
        btnSimpan = buatTombol("Simpan",  C_BRAND,  Color.WHITE, C_BRAND_HOVER);
        btnSimpan.setBounds(24, y, 130, 42);

        btnBatal  = buatTombol("Batal",   new Color(241, 245, 249),  C_TEXT_MAIN, new Color(226, 232, 240));
        btnBatal.setBorder(BorderFactory.createLineBorder(C_BORDER));
        btnBatal.setBounds(166, y, 130, 42);
        y += 54;

        btnUpdate = buatTombol("Update",  C_GREEN, Color.WHITE, C_GREEN_HOVER);
        btnUpdate.setBounds(24, y, 130, 42);
        btnUpdate.setEnabled(false);

        btnHapus  = buatTombol("Hapus",   C_RED,   Color.WHITE, C_RED_HOVER);
        btnHapus.setBounds(166, y, 130, 42);
        btnHapus.setEnabled(false);

        card.add(lblTitle);   card.add(garis);
        card.add(lblU);       card.add(txtUsername);
        card.add(lblP);       card.add(txtPassword);
        card.add(lblN);       card.add(txtNama);
        card.add(lblL);       card.add(cmbLevel);
        card.add(garisTombol);
        card.add(btnSimpan);  card.add(btnBatal);
        card.add(btnUpdate);  card.add(btnHapus);

        // Listeners
        btnSimpan.addActionListener(e -> simpanUser());
        btnUpdate.addActionListener(e -> konfirmasiUpdateUser());
        btnHapus.addActionListener(e  -> konfirmasiHapusUser());
        btnBatal.addActionListener(e  -> resetForm());

        // Enter → simpan
        ActionListener enter = e -> { if (btnSimpan.isEnabled()) simpanUser(); };
        txtUsername.addActionListener(enter);
        txtNama.addActionListener(enter);

        return card;
    }

    // ── Tabel Panel ───────────────────────────────────────────
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

        JLabel lblTitle = new JLabel("Daftar Pengguna Sistem");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        header.add(lblTitle, BorderLayout.WEST);

        String[] kolom = {"ID", "Username", "Nama Lengkap", "Level Akses"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabel = new JTable(modelTabel);
        tabel.setRowHeight(44);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabel.setForeground(C_TEXT_MAIN);
        tabel.setSelectionBackground(new Color(239, 246, 255));
        tabel.setSelectionForeground(C_BRAND);
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(C_BORDER);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader th = tabel.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(C_TEXT_MUTED);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer paddingRenderer = new DefaultTableCellRenderer();
        paddingRenderer.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        for (int i = 0; i < tabel.getColumnCount(); i++) {
            if (i != 3) {
                tabel.getColumnModel().getColumn(i).setCellRenderer(paddingRenderer);
            }
        }

        tabel.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(140);

        // Custom renderer — warna badge level
        tabel.getColumnModel().getColumn(3).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val,
                        boolean sel, boolean foc, int row, int col) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                    lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                    String level = val != null ? val.toString() : "";
                    if (level.equals("Admin")) {
                        lbl.setForeground(sel ? C_BRAND : C_BRAND);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        lbl.setForeground(sel ? C_GREEN : C_GREEN);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    }
                    return lbl;
                }
            }
        );

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
        return card;
    }

    // ── Muat Data ─────────────────────────────────────────────
    private void muatData() {
        modelTabel.setRowCount(0);
        String sql = "SELECT id_user, username, nama_lengkap, level FROM tb_user ORDER BY id_user";
        try (Connection c = DBUtil.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery(sql)) {
            while (r.next()) {
                modelTabel.addRow(new Object[]{
                    r.getInt("id_user"),
                    r.getString("username"),
                    r.getString("nama_lengkap"),
                    r.getString("level")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Isi Form dari Tabel ───────────────────────────────────
    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        idUserDipilih = (int) modelTabel.getValueAt(row, 0);
        txtUsername.setText(modelTabel.getValueAt(row, 1).toString());
        txtNama.setText(modelTabel.getValueAt(row, 2).toString());
        cmbLevel.setSelectedItem(modelTabel.getValueAt(row, 3).toString());
        txtPassword.setText(""); // password tidak ditampilkan

        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(241, 245, 249));

        btnSimpan.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnHapus.setEnabled(true);
    }

    // ── CRUD ──────────────────────────────────────────────────
    private void simpanUser() {
        if (!validasi()) return;

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String nama     = txtNama.getText().trim();
        String level    = (String) cmbLevel.getSelectedItem();

        if (usernameAda(username)) {
            tampilPesanModern("Validasi", "Username <b>" + username + "</b> sudah terdaftar!<br>Silakan gunakan username lain.", true);
            return;
        }

        String sql = "INSERT INTO tb_user (username, password, nama_lengkap, level) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, nama);
            ps.setString(4, level);
            ps.executeUpdate();
            
            tampilPesanModern("Sukses", "Pengguna <b>" + nama + "</b> berhasil ditambahkan!", false);
            muatData();
            resetForm();
        } catch (SQLException e) {
            e.printStackTrace();
            tampilPesanModern("Error", "Gagal menyimpan data pengguna ke database!", true);
        }
    }
    
    private void konfirmasiUpdateUser() {
        if (!validasi()) return;
        String nama = txtNama.getText().trim();
        
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Apakah Anda yakin ingin memperbarui data pengguna <b>" + nama + "</b>?</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);
        
        showModernModal("Konfirmasi Update", content, e -> updateUser(), "Update Data", C_BRAND, C_BRAND_HOVER, true);
    }

    private void updateUser() {
        String password = new String(txtPassword.getPassword()).trim();
        String nama     = txtNama.getText().trim();
        String level    = (String) cmbLevel.getSelectedItem();

        String sql = password.isEmpty()
            ? "UPDATE tb_user SET nama_lengkap=?, level=? WHERE id_user=?"
            : "UPDATE tb_user SET nama_lengkap=?, level=?, password=? WHERE id_user=?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, level);
            if (!password.isEmpty()) {
                ps.setString(3, password);
                ps.setInt(4, idUserDipilih);
            } else {
                ps.setInt(3, idUserDipilih);
            }
            ps.executeUpdate();
            
            String pesan = "Data pengguna <b>" + nama + "</b> berhasil diperbarui.<br>";
            pesan += (password.isEmpty() ? "<i>Password tidak diubah.</i>" : "<i>Password ikut diupdate.</i>");
            
            tampilPesanModern("Update Sukses", pesan, false);
            muatData();
            resetForm();
        } catch (SQLException e) {
            e.printStackTrace();
            tampilPesanModern("Error", "Gagal memperbarui data pengguna!", true);
        }
    }
    
    private void konfirmasiHapusUser() {
        if (idUserDipilih < 0) return;
        
        if (jumlahAdmin() <= 1 && cmbLevel.getSelectedItem().equals("Admin")) {
            tampilPesanModern("Tidak Diizinkan", "Tidak bisa menghapus Admin terakhir!<br>Sistem harus memiliki minimal 1 Admin aktif.", true);
            return;
        }
        
        String username = txtUsername.getText();
        
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>Apakah Anda yakin ingin menghapus pengguna <b>" + username + "</b>?<br>Data yang dihapus tidak dapat dikembalikan.</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);
        
        showModernModal("Konfirmasi Hapus", content, e -> hapusUser(), "Hapus Permanen", C_RED, C_RED_HOVER, true);
    }

    private void hapusUser() {
        String sql = "DELETE FROM tb_user WHERE id_user = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUserDipilih);
            ps.executeUpdate();
            
            tampilPesanModern("Hapus Sukses", "Pengguna berhasil dihapus dari sistem.", false);
            muatData();
            resetForm();
        } catch (SQLException e) {
            e.printStackTrace();
            tampilPesanModern("Error", "Gagal menghapus pengguna dari database!", true);
        }
    }

    // ── Helper ────────────────────────────────────────────────
    private boolean usernameAda(String username) {
        String sql = "SELECT COUNT(*) FROM tb_user WHERE username = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet r = ps.executeQuery();
            return r.next() && r.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    private int jumlahAdmin() {
        String sql = "SELECT COUNT(*) FROM tb_user WHERE level = 'Admin'";
        try (Connection c = DBUtil.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery(sql)) {
            return r.next() ? r.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private boolean validasi() {
        if (txtUsername.getText().trim().isEmpty() || txtNama.getText().trim().isEmpty()) {
            tampilPesanModern("Validasi Gagal", "<b>Username</b> dan <b>Nama Lengkap</b> wajib diisi!", true);
            return false;
        }
        if (btnSimpan.isEnabled() && new String(txtPassword.getPassword()).trim().isEmpty()) {
            tampilPesanModern("Validasi Gagal", "<b>Password</b> wajib diisi untuk membuat pengguna baru!", true);
            return false;
        }
        return true;
    }

    private void resetForm() {
        txtUsername.setText("");    
        txtUsername.setEditable(true);
        txtUsername.setBackground(Color.WHITE);
        txtPassword.setText("");    
        txtNama.setText("");
        cmbLevel.setSelectedIndex(0);
        idUserDipilih = -1;
        tabel.clearSelection();
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    private JLabel buatLabel(String teks, int x, int y) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(C_TEXT_MAIN);
        lbl.setBounds(x, y, 230, 18);
        return lbl;
    }

    private JTextField buatTextField(int y) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBounds(24, y, 272, 40);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        return txt;
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
    
    // ── Sistem Pop-Up Modern ─────────────────────────────────
    private void tampilPesanModern(String judul, String pesan, boolean isError) {
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);
        JLabel lbl = new JLabel("<html>" + pesan + "</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(C_TEXT_MAIN);
        content.add(lbl, BorderLayout.CENTER);
        
        showModernModal(judul, content, null, "Mengerti", 
            isError ? C_RED : C_BRAND, 
            isError ? C_RED_HOVER : C_BRAND_HOVER, false);
    }

    private void showModernModal(String title, JPanel content, ActionListener onConfirm, 
                                 String confirmBtnText, Color confirmBtnColor, Color confirmBtnHover, boolean showCancel) {
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
            btnCancel.setBorder(BorderFactory.createLineBorder(C_BORDER));
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
