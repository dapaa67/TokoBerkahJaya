package tokoberkah.view;

import tokoberkah.util.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class PanelDashboard extends JPanel {

    // Warna Modern (Konsisten dengan FormLogin)
    private static final Color C_BG         = new Color(248, 250, 252); // Slate 50
    private static final Color C_BRAND      = new Color(37, 99, 235);   // Blue 600
    private static final Color C_TEXT_MAIN  = new Color(15, 23, 42);    // Slate 900
    private static final Color C_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color C_BORDER     = new Color(226, 232, 240); // Slate 200
    private static final Color C_CARD_BG    = Color.WHITE;

    public PanelDashboard() {
        setLayout(new BorderLayout());
        setBackground(C_BG);
        add(buildTopbar(), BorderLayout.NORTH);
        
        // Wrap konten dalam JScrollPane biar aman misal window kekecilan
        JScrollPane scrollPane = new JScrollPane(buildKonten());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // --- TAMBAHAN SENSOR AUTO-REFRESH ---
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshData();
            }
        });
        // ------------------------------------
    }

    // ── Topbar ────────────────────────────────────────────────
    private JPanel buildTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(C_CARD_BG);
        top.setPreferredSize(new Dimension(0, 80));
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(16, 28, 16, 28)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("Dashboard Overview");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(C_TEXT_MAIN);

        JLabel lblSub = new JLabel("Selamat datang kembali! Pantau aktivitas toko Anda hari ini.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(C_TEXT_MUTED);

        textPanel.add(lblJudul);
        textPanel.add(lblSub);

        top.add(textPanel, BorderLayout.WEST);
        return top;
    }

    // ── Konten Utama ──────────────────────────────────────────
    private JPanel buildKonten() {
        // Konten utama pakai BorderLayout dengan padding
        JPanel konten = new JPanel(new BorderLayout(24, 24));
        konten.setBackground(C_BG);
        konten.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24)); // Top, Left, Bottom, Right

        // ── 1. Area Atas: 4 Stat Cards ──
        // Pakai GridLayout (1 baris, 4 kolom) biar dinamis menyesuaikan lebar layar
        JPanel panelCards = new JPanel(new GridLayout(1, 4, 20, 0)); 
        panelCards.setBackground(C_BG);
        panelCards.setPreferredSize(new Dimension(0, 100)); // Tinggi kartu stat tetap 100px

        String[] labels = {"Transaksi Hari Ini", "Pendapatan Hari Ini", "Total Barang", "Total Customer"};
        Color[]  colors = {
            new Color(239, 246, 255), // Blue 50
            new Color(240, 253, 244), // Green 50
            new Color(255, 251, 235), // Amber 50
            new Color(254, 242, 242)  // Red 50
        };
        Color[] textColors = {
            C_BRAND,                  // Blue 600
            new Color(22, 163, 74),   // Green 600
            new Color(217, 119, 6),   // Amber 600
            new Color(220, 38, 38)    // Red 600
        };
        String[] values = {
            String.valueOf(ambilInt("SELECT COUNT(*) FROM tb_penjualan WHERE tgl_transaksi = CURDATE()")),
            "Rp " + String.format("%,.0f", ambilDouble("SELECT IFNULL(SUM(total_bayar),0) FROM tb_penjualan WHERE tgl_transaksi = CURDATE()")).replace(",", "."),
            String.valueOf(ambilInt("SELECT COUNT(*) FROM tb_barang")),
            String.valueOf(ambilInt("SELECT COUNT(*) FROM tb_customer"))
        };

        for (int i = 0; i < 4; i++) {
            JPanel card = buildStatCard(labels[i], values[i], colors[i], textColors[i]);
            panelCards.add(card);
        }

        // ── 2. Area Bawah: Tabel Transaksi & Stok Menipis ──
        // Pakai BorderLayout agar Tabel mengisi sisa ruang secara dinamis
        JPanel panelBawah = new JPanel(new BorderLayout(24, 0));
        panelBawah.setBackground(C_BG);

        // Tabel transaksi terbaru (Tengah/Center)
        JPanel tblPanel = buildTabelTransaksi();
        panelBawah.add(tblPanel, BorderLayout.CENTER);

        // Panel stok menipis (Kanan/East)
        JPanel stokPanel = buildStokMenipis();
        stokPanel.setPreferredSize(new Dimension(320, 0)); // Lebar tetap, tinggi ikut panelBawah
        panelBawah.add(stokPanel, BorderLayout.EAST);

        // Masukkan semuanya ke panel konten
        konten.add(panelCards, BorderLayout.NORTH);
        konten.add(panelBawah, BorderLayout.CENTER);

        return konten;
    }

    // ── Stat Card ─────────────────────────────────────────────
    private JPanel buildStatCard(String labelTitle, String value, Color bgDot, Color accent) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(C_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                // Draw accent bar on the left
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight(), 16, 16);
                g2.fillRect(3, 0, 3, getHeight()); // Cover right side of the round rect to make it straight on the right
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        JPanel circleDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgDot);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        circleDot.setOpaque(false);
        circleDot.setBounds(24, 24, 48, 48);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, value.startsWith("Rp") ? 20 : 26));
        lblVal.setForeground(C_TEXT_MAIN);
        lblVal.setBounds(88, 24, 180, 28);

        JLabel lblTitle = new JLabel(labelTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(C_TEXT_MUTED);
        lblTitle.setBounds(88, 52, 180, 18);

        card.add(circleDot);
        card.add(lblVal);
        card.add(lblTitle);

        return card;
    }

    // ── Tabel Transaksi Terbaru ───────────────────────────────
    private JPanel buildTabelTransaksi() {
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
        card.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Menghindari kepotong border manual

        // Header tabel
        JPanel header = new JPanel(null);
        header.setBackground(Color.WHITE);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        JLabel lblTitle = new JLabel("Transaksi Terbaru");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBounds(20, 18, 200, 20);
        header.add(lblTitle);

        // Tabel
        String[] kolom = {"ID", "Customer", "Item", "Total", "Tanggal"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Ambil data dari DB
        String sql = "SELECT p.id_jual, c.nama_customer, COUNT(d.id_detail) AS jml_item, p.total_bayar, p.tgl_transaksi " +
                     "FROM tb_penjualan p " +
                     "JOIN tb_customer c ON p.id_customer = c.id_customer " +
                     "LEFT JOIN tb_detail_penjualan d ON p.id_jual = d.id_jual " +
                     "GROUP BY p.id_jual, c.nama_customer, p.total_bayar, p.tgl_transaksi " +
                     "ORDER BY p.id_jual DESC LIMIT 10";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    "#" + rs.getInt("id_jual"),
                    rs.getString("nama_customer"),
                    rs.getInt("jml_item") + " brg",
                    "Rp " + String.format("%,.0f", rs.getDouble("total_bayar")).replace(",", "."),
                    rs.getString("tgl_transaksi")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        table.setRowHeight(44);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(C_TEXT_MAIN);
        
        // Warna saat baris di-klik (Sesuai brand color login)
        table.setSelectionBackground(new Color(239, 246, 255)); // Blue 50
        table.setSelectionForeground(C_BRAND); 
        
        table.setShowVerticalLines(false);
        table.setGridColor(C_BORDER); 
        table.setBorder(BorderFactory.createEmptyBorder());

        // Styling Judul Kolom (Header)
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(248, 250, 252)); // Slate 50
        th.setForeground(C_TEXT_MUTED); 
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 36));

        // Memberikan padding/margin pada tiap sel tabel agar teks tidak nempel di garis
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for(int i=0; i<table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Action Double Click
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    String val = (String) table.getValueAt(table.getSelectedRow(), 0);
                    int idJual = Integer.parseInt(val.replace("#", ""));
                    Frame parent = (Frame) SwingUtilities.getWindowAncestor(PanelDashboard.this);
                    new DialogDetailTransaksi(parent, idJual).setVisible(true);
                }
            }
        });

        // Ukuran kolom
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ── Stok Menipis ──────────────────────────────────────────
    private JPanel buildStokMenipis() {
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

        JPanel header = new JPanel(null);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        JLabel lblTitle = new JLabel("Peringatan Stok");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);
        lblTitle.setBounds(20, 18, 160, 20);
        header.add(lblTitle);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        String sql = "SELECT nama_barang, stok FROM tb_barang WHERE stok <= 10 ORDER BY stok ASC LIMIT 8";
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            boolean ada = false;
            while (rs.next()) {
                ada = true;
                int stok = rs.getInt("stok");
                Color dotColor = stok <= 3 ? new Color(239, 68, 68) : new Color(245, 158, 11);
                Color bgDot = stok <= 3 ? new Color(254, 226, 226) : new Color(254, 243, 199);

                JPanel item = new JPanel(null);
                item.setBackground(Color.WHITE);
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
                item.setPreferredSize(new Dimension(250, 52));
                item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249))); // Garis pemisah halus

                // Indikator Modern (Bulatan warna transparan dengan dot kecil di tengah)
                JPanel dotBox = new JPanel(null) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(bgDot);
                        g2.fillOval(0, 0, getWidth(), getHeight());
                        g2.setColor(dotColor);
                        g2.fillOval(getWidth()/2 - 4, getHeight()/2 - 4, 8, 8);
                        g2.dispose();
                    }
                };
                dotBox.setBounds(0, 10, 32, 32);
                dotBox.setOpaque(false);

                JLabel lblNama = new JLabel(rs.getString("nama_barang"));
                lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblNama.setForeground(C_TEXT_MAIN);
                lblNama.setBounds(46, 6, 200, 20);

                JLabel lblStok = new JLabel("Sisa stok: " + stok + " pcs");
                lblStok.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblStok.setForeground(stok <= 3 ? dotColor : C_TEXT_MUTED);
                lblStok.setBounds(46, 26, 200, 16);

                item.add(dotBox);
                item.add(lblNama);
                item.add(lblStok);

                listPanel.add(item);
            }
            if (!ada) {
                JLabel lbl = new JLabel("✅ Semua stok barang aman.");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setForeground(new Color(22, 163, 74));
                listPanel.add(lbl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ── Helper DB ─────────────────────────────────────────────
    private int ambilInt(String sql) {
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            return r.next() ? r.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    private double ambilDouble(String sql) {
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            return r.next() ? r.getDouble(1) : 0;
        } catch (SQLException e) { return 0; }
    }
    
    // ── Method buat Refresh Data Otomatis ─────────────────────
    public void refreshData() {
        removeAll();
        
        add(buildTopbar(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(buildKonten());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
}
