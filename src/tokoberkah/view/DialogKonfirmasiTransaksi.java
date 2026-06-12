package tokoberkah.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DialogKonfirmasiTransaksi extends JDialog {

    private JTextField txtUangBayar;
    private JLabel     lblKembalian;
    private JButton    btnProses, btnBatal;

    private final double grandTotal;
    private boolean dikonfirmasi = false;

    // Warna Modern
    private static final Color C_BRAND      = new Color(37, 99, 235);   // Blue 600
    private static final Color C_BRAND_HOVER= new Color(29, 78, 216);   // Blue 700
    private static final Color C_GREEN      = new Color(22, 163, 74);   // Green 600
    private static final Color C_RED        = new Color(220, 38, 38);   // Red 600
    private static final Color C_TEXT_MAIN  = new Color(15, 23, 42);    // Slate 900
    private static final Color C_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color C_BORDER     = new Color(226, 232, 240); // Slate 200
    private static final Color C_BG_MUTED   = new Color(248, 250, 252); // Slate 50
    private static final Color C_CARD_BG    = Color.WHITE;

    public DialogKonfirmasiTransaksi(Frame parent, double grandTotal,
                                     String namaCustomer, String ringkasanItem) {
        super(parent, "Konfirmasi Transaksi", true);
        this.grandTotal = grandTotal;
        setUndecorated(true);
        buildUI(namaCustomer, ringkasanItem);
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI(String namaCustomer, String ringkasanItem) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(C_CARD_BG);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));

        // ── Header ──
        JLabel lblTitle = new JLabel("Konfirmasi Pembayaran");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(C_TEXT_MAIN);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // ── Body ──
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 24, 0));

        // Customer Info
        JPanel pnlCust = new JPanel(new BorderLayout());
        pnlCust.setOpaque(false);
        JLabel lblCustLabel = new JLabel("Customer:");
        lblCustLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCustLabel.setForeground(C_TEXT_MUTED);
        JLabel lblCustVal = new JLabel(namaCustomer);
        lblCustVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCustVal.setForeground(C_TEXT_MAIN);
        pnlCust.add(lblCustLabel, BorderLayout.WEST);
        pnlCust.add(lblCustVal, BorderLayout.EAST);
        
        // Items
        JLabel lblItemLabel = new JLabel("Ringkasan Item:");
        lblItemLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblItemLabel.setForeground(C_TEXT_MUTED);
        lblItemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtItem = new JTextArea(ringkasanItem);
        txtItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtItem.setForeground(C_TEXT_MAIN);
        txtItem.setEditable(false);
        txtItem.setBackground(C_BG_MUTED);
        txtItem.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        JScrollPane scrollItem = new JScrollPane(txtItem);
        scrollItem.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        scrollItem.setPreferredSize(new Dimension(360, 100));
        scrollItem.setMaximumSize(new Dimension(360, 100));
        scrollItem.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Total
        JPanel pnlTotal = new JPanel(new BorderLayout());
        pnlTotal.setOpaque(false);
        JLabel lblTotalLabel = new JLabel("Total Belanja:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalLabel.setForeground(C_TEXT_MAIN);
        JLabel lblTotalVal = new JLabel("Rp " + String.format("%,.0f", grandTotal).replace(",", "."));
        lblTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotalVal.setForeground(C_BRAND);
        pnlTotal.add(lblTotalLabel, BorderLayout.WEST);
        pnlTotal.add(lblTotalVal, BorderLayout.EAST);

        // Uang Bayar
        JLabel lblBayarLabel = new JLabel("Nominal Bayar:");
        lblBayarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBayarLabel.setForeground(C_TEXT_MAIN);
        lblBayarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUangBayar = new JTextField();
        txtUangBayar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtUangBayar.setForeground(C_TEXT_MAIN);
        txtUangBayar.setPreferredSize(new Dimension(360, 46));
        txtUangBayar.setMaximumSize(new Dimension(360, 46));
        txtUangBayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUangBayar.putClientProperty("JTextField.placeholderText", "Masukkan angka...");
        
        txtUangBayar.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) e.consume();
            }
            public void keyReleased(KeyEvent e) {
                hitungKembalian();
            }
        });

        // Quick Buttons
        JPanel pnlQuick = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlQuick.setOpaque(false);
        pnlQuick.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton btnPas = buatTombolQuick("Uang Pas");
        btnPas.addActionListener(e -> {
            txtUangBayar.setText(String.valueOf((long) grandTotal));
            hitungKembalian();
        });
        pnlQuick.add(btnPas);
        
        long[] nominals = {50000, 100000};
        for (long nom : nominals) {
            String label = nom == 50000 ? "Rp 50rb" : "Rp 100rb";
            JButton btnNom = buatTombolQuick(label);
            btnNom.addActionListener(e -> {
                long bayar = nom;
                while (bayar < (long) grandTotal) bayar += nom;
                txtUangBayar.setText(String.valueOf(bayar));
                hitungKembalian();
            });
            pnlQuick.add(btnNom);
        }

        // Kembalian
        JPanel pnlKembali = new JPanel(new BorderLayout());
        pnlKembali.setOpaque(false);
        JLabel lblKembaliLabel = new JLabel("Kembalian:");
        lblKembaliLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKembaliLabel.setForeground(C_TEXT_MAIN);
        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblKembalian.setForeground(C_GREEN);
        pnlKembali.add(lblKembaliLabel, BorderLayout.WEST);
        pnlKembali.add(lblKembalian, BorderLayout.EAST);

        // Add to Body
        bodyPanel.add(pnlCust);
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(buatGaris());
        bodyPanel.add(Box.createVerticalStrut(12));
        bodyPanel.add(lblItemLabel);
        bodyPanel.add(Box.createVerticalStrut(6));
        bodyPanel.add(scrollItem);
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(pnlTotal);
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(buatGaris());
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(lblBayarLabel);
        bodyPanel.add(Box.createVerticalStrut(6));
        bodyPanel.add(txtUangBayar);
        bodyPanel.add(Box.createVerticalStrut(8));
        bodyPanel.add(pnlQuick);
        bodyPanel.add(Box.createVerticalStrut(16));
        bodyPanel.add(pnlKembali);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        // ── Footer ──
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footerPanel.setOpaque(false);

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBatal.setBackground(new Color(241, 245, 249));
        btnBatal.setForeground(C_TEXT_MAIN);
        btnBatal.setFocusPainted(false);
        btnBatal.setBorderPainted(false);
        btnBatal.setOpaque(true);
        btnBatal.setPreferredSize(new Dimension(100, 40));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBatal.setBackground(new Color(226, 232, 240)); }
            public void mouseExited(MouseEvent e) { btnBatal.setBackground(new Color(241, 245, 249)); }
        });
        btnBatal.addActionListener(e -> dispose());

        btnProses = new JButton("Proses Transaksi");
        btnProses.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProses.setBackground(C_BRAND);
        btnProses.setForeground(Color.WHITE);
        btnProses.setFocusPainted(false);
        btnProses.setBorderPainted(false);
        btnProses.setOpaque(true);
        btnProses.setPreferredSize(new Dimension(160, 40));
        btnProses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProses.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btnProses.isEnabled()) btnProses.setBackground(C_BRAND_HOVER); }
            public void mouseExited(MouseEvent e) { if (btnProses.isEnabled()) btnProses.setBackground(C_BRAND); }
        });
        btnProses.addActionListener(e -> prosesKonfirmasi());

        footerPanel.add(btnBatal);
        footerPanel.add(btnProses);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private void hitungKembalian() {
        try {
            long uangBayar = Long.parseLong(txtUangBayar.getText().trim());
            long kembalian = uangBayar - (long) grandTotal;

            if (kembalian < 0) {
                lblKembalian.setText("Kurang: Rp " + String.format("%,.0f", (double) Math.abs(kembalian)).replace(",", "."));
                lblKembalian.setForeground(C_RED);
                btnProses.setEnabled(false);
            } else {
                lblKembalian.setText("Rp " + String.format("%,.0f", (double) kembalian).replace(",", "."));
                lblKembalian.setForeground(C_GREEN);
                btnProses.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Rp 0");
            lblKembalian.setForeground(C_TEXT_MUTED);
            btnProses.setEnabled(false);
        }
    }

    private void prosesKonfirmasi() {
        String input = txtUangBayar.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah uang yang dibayarkan!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtUangBayar.requestFocus();
            return;
        }
        dikonfirmasi = true;
        dispose();
    }

    public boolean isDikonfirmasi() { return dikonfirmasi; }

    public long getUangBayar() {
        try {
            return Long.parseLong(txtUangBayar.getText().trim());
        } catch (NumberFormatException e) { return 0; }
    }

    public long getKembalian() {
        return getUangBayar() - (long) grandTotal;
    }

    // ── Helpers ──
    private JPanel buatGaris() {
        JPanel g = new JPanel();
        g.setBackground(C_BORDER);
        g.setPreferredSize(new Dimension(360, 1));
        g.setMaximumSize(new Dimension(360, 1));
        g.setAlignmentX(Component.LEFT_ALIGNMENT);
        return g;
    }

    private JButton buatTombolQuick(String teks) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(C_BG_MUTED);
        btn.setForeground(C_TEXT_MAIN);
        btn.setBorder(BorderFactory.createLineBorder(C_BORDER));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 32));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(C_BORDER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(C_BG_MUTED); }
        });
        return btn;
    }
}
