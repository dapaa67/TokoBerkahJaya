/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import com.formdev.flatlaf.FlatLightLaf;
import tokoberkah.util.DBUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormLogin extends JFrame {

    // ── Komponen ──────────────────────────────────────────────
    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox      chkShowPass;
    private JRadioButton   rbAdmin, rbPetugas;
    private ButtonGroup    bgRole;
    private JButton        btnLogin;
    private JLabel         lblError;

    // ── Warna Modern ──────────────────────────────────────────
    private static final Color C_BRAND  = new Color(37, 99, 235);   // Blue 600
    private static final Color C_BRAND_HOVER = new Color(29, 78, 216); // Blue 700
    private static final Color C_TEXT_MAIN = new Color(15, 23, 42);  // Slate 900
    private static final Color C_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color C_ERROR  = new Color(239, 68, 68);    // Red 500

    public FormLogin() {
        setupLookAndFeel();
        buildUI();
        addListeners();
    }

    // ── Look & Feel ───────────────────────────────────────────
    private void setupLookAndFeel() {
        FlatLightLaf.setup();
        UIManager.put("Button.arc",          12);
        UIManager.put("Component.arc",        12);
        UIManager.put("TextComponent.arc",    12);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("TextComponent.margin", new Insets(6, 12, 6, 12));
    }

    // ── Bangun UI ─────────────────────────────────────────────
    private void buildUI() {
        setTitle("Toko Berkah Jaya - Login");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buildPanelKiri(),  BorderLayout.WEST);
        add(buildPanelKanan(), BorderLayout.CENTER);
    }

    // ── Panel Kiri (Branding Modern) ──────────────────────────
    private JPanel buildPanelKiri() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Modern smooth gradient (tetap dipertahankan agar terlihat premium)
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(30, 58, 138), // Blue 900
                    getWidth(), getHeight(), new Color(15, 23, 42) // Slate 900
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(420, 600));

        // Kotak ikon logo (dibuat lebih simpel)
        JPanel iconBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Lingkaran putih transparan untuk logo
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Inisial "TB" (Toko Berkah)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
                FontMetrics fm = g2.getFontMetrics();
                String text = "TB";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 8);
                g2.dispose();
            }
        };
        iconBox.setOpaque(false);
        iconBox.setBounds(160, 170, 100, 100);

        JLabel lblNama = new JLabel("Toko Berkah Jaya");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNama.setForeground(Color.WHITE);
        lblNama.setHorizontalAlignment(SwingConstants.CENTER);
        lblNama.setBounds(40, 290, 340, 40);

        JLabel lblTagline = new JLabel("Sistem Manajemen Penjualan");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTagline.setForeground(new Color(148, 163, 184)); // Slate 400
        lblTagline.setHorizontalAlignment(SwingConstants.CENTER);
        lblTagline.setBounds(40, 330, 340, 30);

        JLabel lblVer = new JLabel("Versi 1.0.0");
        lblVer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVer.setForeground(new Color(100, 116, 139));
        lblVer.setHorizontalAlignment(SwingConstants.CENTER);
        lblVer.setBounds(160, 520, 100, 20);

        panel.add(iconBox);
        panel.add(lblNama);
        panel.add(lblTagline);
        panel.add(lblVer);
        
        return panel;
    }

    // ── Panel Kanan (Form Modern) ─────────────────────────────
    private JPanel buildPanelKanan() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel content = new JPanel(null);
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(360, 480));

        // Judul Form
        JLabel lblJudul = new JLabel("Selamat Datang!");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblJudul.setForeground(C_TEXT_MAIN);
        lblJudul.setBounds(20, 30, 320, 40);

        JLabel lblSub = new JLabel("Silakan masuk ke akun Anda.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(C_TEXT_MUTED);
        lblSub.setBounds(20, 70, 320, 20);

        // Field Username
        JLabel lblU = new JLabel("Username");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblU.setForeground(C_TEXT_MAIN);
        lblU.setBounds(20, 130, 320, 20);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username");
        txtUsername.putClientProperty("JTextField.showClearButton", true);
        txtUsername.setBounds(20, 155, 320, 42);

        // Field Password
        JLabel lblP = new JLabel("Password");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblP.setForeground(C_TEXT_MAIN);
        lblP.setBounds(20, 215, 320, 20);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty("JTextField.placeholderText", "••••••••");
        txtPassword.putClientProperty("JTextField.showClearButton", true);
        txtPassword.setBounds(20, 240, 320, 42);

        // Checkbox Show Password
        chkShowPass = new JCheckBox("Tampilkan sandi");
        chkShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkShowPass.setForeground(C_TEXT_MUTED);
        chkShowPass.setBackground(Color.WHITE);
        chkShowPass.setBounds(15, 290, 150, 25);

        // Switch Role (Segmented Control Style)
        JPanel pnlRole = new JPanel(new GridLayout(1, 2, 0, 0));
        pnlRole.setBackground(new Color(241, 245, 249)); // Slate 100
        pnlRole.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlRole.setBounds(20, 330, 320, 40);

        rbAdmin = new JRadioButton("Admin");
        rbAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rbAdmin.setHorizontalAlignment(SwingConstants.CENTER);
        rbAdmin.setBackground(Color.WHITE); 
        rbAdmin.setForeground(C_BRAND);
        rbAdmin.setOpaque(true);
        rbAdmin.setSelected(true);
        rbAdmin.setBorder(BorderFactory.createEmptyBorder());

        rbPetugas = new JRadioButton("Petugas");
        rbPetugas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rbPetugas.setHorizontalAlignment(SwingConstants.CENTER);
        rbPetugas.setBackground(new Color(241, 245, 249));
        rbPetugas.setForeground(C_TEXT_MUTED);
        rbPetugas.setOpaque(true);
        rbPetugas.setBorder(BorderFactory.createEmptyBorder());

        bgRole = new ButtonGroup();
        bgRole.add(rbAdmin);
        bgRole.add(rbPetugas);

        pnlRole.add(rbAdmin);
        pnlRole.add(rbPetugas);

        // Animasi pergantian role
        ActionListener roleListener = e -> {
            if (rbAdmin.isSelected()) {
                rbAdmin.setBackground(Color.WHITE);
                rbAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
                rbAdmin.setForeground(C_BRAND);
                
                rbPetugas.setBackground(new Color(241, 245, 249));
                rbPetugas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                rbPetugas.setForeground(C_TEXT_MUTED);
            } else {
                rbPetugas.setBackground(Color.WHITE);
                rbPetugas.setFont(new Font("Segoe UI", Font.BOLD, 13));
                rbPetugas.setForeground(C_BRAND);
                
                rbAdmin.setBackground(new Color(241, 245, 249));
                rbAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                rbAdmin.setForeground(C_TEXT_MUTED);
            }
        };
        rbAdmin.addActionListener(roleListener);
        rbPetugas.addActionListener(roleListener);

        // Error Label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(C_ERROR);
        lblError.setBounds(20, 375, 320, 20);

        // Tombol Login
        btnLogin = new JButton("Masuk Sekarang");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(C_BRAND);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(20, 400, 320, 45);

        content.add(lblJudul);
        content.add(lblSub);
        content.add(lblU);
        content.add(txtUsername);
        content.add(lblP);
        content.add(txtPassword);
        content.add(chkShowPass);
        content.add(pnlRole);
        content.add(lblError);
        content.add(btnLogin);

        rightPanel.add(content);
        return rightPanel;
    }

    // ── Listeners ─────────────────────────────────────────────
    private void addListeners() {
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> prosesLogin());

        chkShowPass.addActionListener(e -> {
            char echo = chkShowPass.isSelected() ? (char) 0 : (char) 8226;
            txtPassword.setEchoChar(echo);
        });

        btnLogin.addActionListener(e -> prosesLogin());

        // Hover Effect untuk tombol Login
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btnLogin.isEnabled()) {
                    btnLogin.setBackground(C_BRAND_HOVER);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btnLogin.isEnabled()) {
                    btnLogin.setBackground(C_BRAND);
                }
            }
        });
    }

    // ── Logika Login ──────────────────────────────────────────
    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role     = rbAdmin.isSelected() ? "Admin" : "Petugas";

        if (username.isEmpty() || password.isEmpty()) {
            tampilError("Username dan password tidak boleh kosong!");
            goyang(txtUsername);
            goyang(txtPassword);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Memproses...");
        btnLogin.setBackground(new Color(148, 163, 184)); // Slate 400
        lblError.setText(" ");

        SwingWorker<Object[], Void> worker = new SwingWorker<>() {
            @Override
            protected Object[] doInBackground() {
                // Simulasi delay sedikit untuk efek UI
                try { Thread.sleep(500); } catch(Exception ignored) {}
                return cekDatabase(username, password, role);
            }

            @Override
            protected void done() {
                try {
                    Object[] hasil = get();
                    if (hasil != null) {
                        int    idUser = (int)    hasil[0];
                        String nama   = (String) hasil[1];
                        String lvl    = (String) hasil[2];

                        dispose();
                        new FormUtama(idUser, nama, lvl).setVisible(true);
                    } else {
                        tampilError("Username/password salah atau role tidak sesuai!");
                        goyang(txtUsername);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                        resetTombol();
                    }
                } catch (Exception ex) {
                    tampilError("Gagal koneksi: " + ex.getMessage());
                    resetTombol();
                }
            }
        };
        worker.execute();
    }

    private void resetTombol() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Masuk Sekarang");
        btnLogin.setBackground(C_BRAND);
    }

    private Object[] cekDatabase(String username, String password, String role) {
        String sql = "SELECT id_user, nama_lengkap, level FROM tb_user WHERE username=? AND password=? AND level=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("nama_lengkap"),
                    rs.getString("level")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void tampilError(String pesan) {
        lblError.setText(pesan);
        txtUsername.putClientProperty("JComponent.outline", "error");
        txtPassword.putClientProperty("JComponent.outline", "error");
        
        Timer timer = new Timer(2500, e -> {
            txtUsername.putClientProperty("JComponent.outline", null);
            txtPassword.putClientProperty("JComponent.outline", null);
            lblError.setText(" ");
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void goyang(Component comp) {
        final Point titikAsal = comp.getLocation();
        final int[] gerak = {-6, 6, -4, 4, -2, 2, 0};
        int delay = 0;
        for (int d : gerak) {
            Timer t = new Timer(delay, e -> comp.setLocation(titikAsal.x + d, titikAsal.y));
            t.setRepeats(false);
            t.start();
            delay += 35;
        }
    }
}
