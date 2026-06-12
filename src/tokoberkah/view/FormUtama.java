package tokoberkah.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import tokoberkah.view.PanelLaporan;

public class FormUtama extends JFrame {

    private JPanel      pnlKonten;
    private CardLayout  cardLayout;

    // Warna Modern (Konsisten dengan Login & Dashboard)
    private static final Color C_SIDEBAR_BG = new Color(15, 23, 42); // Slate 900
    private static final Color C_HOVER      = new Color(30, 41, 59); // Slate 800
    private static final Color C_ACTIVE     = new Color(37, 99, 235); // Blue 600
    private static final Color C_TEXT       = new Color(148, 163, 184); // Slate 400
    private static final Color C_TEXT_ACTIVE= Color.WHITE;
    private static final Color C_BORDER     = new Color(30, 41, 59); // Slate 800
    private static final Color C_BG_CONTENT = new Color(248, 250, 252); // Slate 50

    private static final int LEBAR_SIDEBAR = 260; // Lebarkan sedikit agar proporsional

    private JButton btnAktif = null;
    private String levelUser;
    private int idUserLogin;
    private String namaUser;

    public FormUtama(int idUser, String nama, String level) {
        this.idUserLogin = idUser;
        this.namaUser    = nama;
        this.levelUser   = level;

        setTitle("Toko Berkah Jaya");
        setSize(1366, 768); // Diperbesar ke resolusi standar agar teks tidak elipsis
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(nama, level), BorderLayout.WEST);
        add(buildKonten(),             BorderLayout.CENTER);
    }

    // ── Sidebar ───────────────────────────────────────────────
    private JPanel buildSidebar(String nama, String level) {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(C_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(LEBAR_SIDEBAR, 0));

        // ── Panel Atas (Logo & Menu) ──
        JPanel panelAtas = new JPanel(null);
        panelAtas.setBackground(C_SIDEBAR_BG);

        // Logo dan Branding
        JPanel logoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString("TB", 6, 24);
                g2.dispose();
            }
        };
        logoBox.setOpaque(false);
        logoBox.setBounds(20, 24, 34, 34);

        JLabel lblLogo = new JLabel("Toko Berkah");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBounds(66, 22, 180, 24);

        JLabel lblSub = new JLabel("Sistem Manajemen v1.0");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(C_TEXT);
        lblSub.setBounds(66, 46, 180, 16);

        // Garis pemisah bawah logo
        JPanel garis = new JPanel();
        garis.setBackground(C_BORDER);
        garis.setBounds(20, 84, LEBAR_SIDEBAR - 40, 1);

        panelAtas.add(logoBox);
        panelAtas.add(lblLogo);
        panelAtas.add(lblSub);
        panelAtas.add(garis);

        // ── List Menu ──
        int y = 104;

        JButton btnDashboard = buildNavBtn("dashboard", "Dashboard");
        btnDashboard.setBounds(20, y, LEBAR_SIDEBAR - 40, 44);
        panelAtas.add(btnDashboard);
        y += 48;

        JButton btnBarang = buildNavBtn("barang", "Data Barang");
        btnBarang.setBounds(20, y, LEBAR_SIDEBAR - 40, 44);
        panelAtas.add(btnBarang);
        y += 48;

        JButton btnCustomer = buildNavBtn("customer", "Data Customer");
        btnCustomer.setBounds(20, y, LEBAR_SIDEBAR - 40, 44);
        panelAtas.add(btnCustomer);
        y += 48;

        // Garis pemisah kelompok menu
        JPanel g2 = new JPanel();
        g2.setBackground(C_BORDER);
        g2.setBounds(20, y + 4, LEBAR_SIDEBAR - 40, 1);
        panelAtas.add(g2);
        y += 16;

        JButton btnPenjualan = buildNavBtn("penjualan", "Kasir / Penjualan");
        btnPenjualan.setBounds(20, y, LEBAR_SIDEBAR - 40, 44);
        panelAtas.add(btnPenjualan);
        y += 48;

        if (level.equals("Admin")) {
            JButton btnLaporan = buildNavBtn("laporan", "Laporan Penjualan");
            btnLaporan.setBounds(20, y, LEBAR_SIDEBAR - 40, 44);
            panelAtas.add(btnLaporan);
            y += 48;

            JPanel g3 = new JPanel();
            g3.setBackground(C_BORDER);
            g3.setBounds(20, y + 4, LEBAR_SIDEBAR - 40, 1);
            panelAtas.add(g3);
            y += 16;

            JButton btnUser = buildNavBtn("user", "Manajemen Pengguna");
            btnUser.setBounds(20, y, LEBAR_SIDEBAR - 40, 44);
            panelAtas.add(btnUser);
        }

        // ── Panel Bawah (Info User & Logout) ──
        JPanel panelBawah = new JPanel(null);
        panelBawah.setBackground(C_SIDEBAR_BG);
        panelBawah.setPreferredSize(new Dimension(LEBAR_SIDEBAR, 154));

        JPanel garisBawah = new JPanel();
        garisBawah.setBackground(C_BORDER);
        garisBawah.setBounds(20, 0, LEBAR_SIDEBAR - 40, 1);

        // Info User Box (Rounded Card)
        JPanel userBox = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59)); // Slate 800
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        userBox.setOpaque(false);
        userBox.setBounds(20, 20, LEBAR_SIDEBAR - 40, 64);

        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(C_ACTIVE);
                g2d.fillOval(0, 0, 40, 40);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2d.getFontMetrics();
                String inisial = nama.length() > 0 ? String.valueOf(nama.charAt(0)).toUpperCase() : "?";
                g2d.drawString(inisial,
                    (40 - fm.stringWidth(inisial)) / 2,
                    (40 + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        avatar.setOpaque(false);
        avatar.setBounds(12, 12, 40, 40);

        JLabel lblNama = new JLabel(nama);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNama.setForeground(Color.WHITE);
        lblNama.setBounds(62, 14, 140, 18);

        JLabel lblLevel = new JLabel(level);
        lblLevel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLevel.setForeground(level.equals("Admin") ? new Color(96, 165, 250) : new Color(52, 211, 153)); // Blue 400 : Emerald 400
        lblLevel.setBounds(62, 34, 140, 14);

        userBox.add(avatar);
        userBox.add(lblNama);
        userBox.add(lblLevel);

        // Tombol Logout
        JButton btnLogout = new JButton("Keluar Aplikasi");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(new Color(248, 113, 113));  // Red 400
        btnLogout.setBackground(C_SIDEBAR_BG);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBounds(20, 96, LEBAR_SIDEBAR - 40, 40);
        
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(true);
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(69, 10, 10)); // Red 950 hover effect
            }
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(C_SIDEBAR_BG);
            }
        });
        btnLogout.addActionListener(e -> prosesLogout());

        panelBawah.add(garisBawah);
        panelBawah.add(userBox);
        panelBawah.add(btnLogout);

        sidebar.add(panelAtas, BorderLayout.CENTER);
        sidebar.add(panelBawah, BorderLayout.SOUTH);

        // Set default aktif dengan simulasi klik agar highlight terpanggil
        SwingUtilities.invokeLater(() -> {
            btnDashboard.doClick();
        });

        return sidebar;
    }

    private JButton buildNavBtn(String panelName, String label) {
        JButton btn = new JButton("   " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == btnAktif) {
                    g2.setColor(C_ACTIVE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else if (getModel().isRollover()) {
                    g2.setColor(C_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(C_TEXT);
        btn.setContentAreaFilled(false); 
        btn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != btnAktif) btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != btnAktif) btn.setForeground(C_TEXT);
            }
        });

        btn.addActionListener(e -> {
            tampilPanel(panelName);
            setAktif(btn);
        });

        return btn;
    }

    private void setAktif(JButton btn) {
        if (btnAktif != null) {
            btnAktif.setForeground(C_TEXT);
            btnAktif.repaint();
        }
        btnAktif = btn;
        btnAktif.setForeground(C_TEXT_ACTIVE);
        btnAktif.repaint();
    }

    // ── Area Konten ───────────────────────────────────────────
    private JPanel buildKonten() {
        cardLayout = new CardLayout();
        pnlKonten  = new JPanel(cardLayout);
        pnlKonten.setBackground(C_BG_CONTENT);

        pnlKonten.add(new PanelDashboard(),            "dashboard");
        pnlKonten.add(new PanelBarang(levelUser),       "barang");
        pnlKonten.add(new PanelCustomer(),             "customer");
        pnlKonten.add(new PanelPenjualan(idUserLogin), "penjualan");
        pnlKonten.add(new PanelLaporan(),              "laporan");

        // Panel user hanya dibuat kalau Admin
        if (levelUser.equals("Admin")) {
            pnlKonten.add(new PanelUser(), "user");
        }

        return pnlKonten;
    }

    public void tampilPanel(String nama) {
        cardLayout.show(pnlKonten, nama);
    }
    
    private void prosesLogout() {
        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin keluar dari aplikasi?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            dispose(); // tutup FormUtama
            // Buka kembali FormLogin
            java.awt.EventQueue.invokeLater(() -> {
                new FormLogin().setVisible(true);
            });
        }
    }
}