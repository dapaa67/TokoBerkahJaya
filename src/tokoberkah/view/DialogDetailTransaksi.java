package tokoberkah.view;

import tokoberkah.dao.LaporanDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;

public class DialogDetailTransaksi extends JDialog {

    private static final Color C_BRAND      = new Color(37, 99, 235);   // Blue 600
    private static final Color C_TEXT_MAIN  = new Color(15, 23, 42);    // Slate 900
    private static final Color C_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color C_BORDER     = new Color(226, 232, 240); // Slate 200
    private static final Color C_BG_MUTED   = new Color(248, 250, 252); // Slate 50
    private static final Color C_CARD_BG    = Color.WHITE;

    private final LaporanDAO dao    = new LaporanDAO();
    private final int        idJual;

    public DialogDetailTransaksi(Frame parent, int idJual) {
        super(parent, "Detail Transaksi #" + idJual, true);
        this.idJual = idJual;
        setUndecorated(true);
        buildUI();
        pack();
        // Set fixed width if needed, or rely on pack()
        setSize(580, 520);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(C_CARD_BG);
        mainPanel.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));

        String[] header = dao.getHeaderTransaksi(idJual);
        if (header == null) {
            JLabel lblKosong = new JLabel("Data tidak ditemukan", SwingConstants.CENTER);
            lblKosong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblKosong.setForeground(C_TEXT_MUTED);
            mainPanel.add(lblKosong, BorderLayout.CENTER);
            
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footer.setBackground(C_CARD_BG);
            footer.add(buatTombolTutup());
            mainPanel.add(footer, BorderLayout.SOUTH);
            
            getContentPane().add(mainPanel);
            return;
        }

        mainPanel.add(buildHeader(header), BorderLayout.NORTH);
        mainPanel.add(buildTabel(),        BorderLayout.CENTER);
        mainPanel.add(buildFooter(header[4]), BorderLayout.SOUTH);
        
        getContentPane().add(mainPanel);
    }

    // ── Header Info ───────────────────────────────────────────
    private JPanel buildHeader(String[] h) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(24, 28, 20, 28)
        ));

        JLabel lblTitle = new JLabel("Detail Transaksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(C_TEXT_MAIN);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel gridInfo = new JPanel(new GridLayout(2, 2, 20, 16));
        gridInfo.setOpaque(false);
        gridInfo.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        String[][] info = {
            {"ID Transaksi", "#" + h[0]},
            {"Tanggal",      h[1]},
            {"Customer",     h[2]},
            {"Kasir",        h[3]}
        };

        for (String[] data : info) {
            JPanel p = new JPanel(new BorderLayout(0, 4));
            p.setOpaque(false);
            
            JLabel lblKey = new JLabel(data[0]);
            lblKey.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblKey.setForeground(C_TEXT_MUTED);
            
            JLabel lblVal = new JLabel(data[1]);
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblVal.setForeground(C_TEXT_MAIN);
            
            p.add(lblKey, BorderLayout.NORTH);
            p.add(lblVal, BorderLayout.CENTER);
            gridInfo.add(p);
        }

        panel.add(gridInfo, BorderLayout.CENTER);
        return panel;
    }

    // ── Tabel Detail Item ─────────────────────────────────────
    private JPanel buildTabel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(C_CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        JLabel lblSub = new JLabel("Daftar Barang Dibeli");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(C_TEXT_MAIN);

        DefaultTableModel model = dao.getDetailTransaksi(idJual);

        JTable tabel = new JTable(model);
        tabel.setRowHeight(40);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabel.setForeground(C_TEXT_MAIN);
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(C_BORDER);
        tabel.setSelectionBackground(new Color(239, 246, 255));
        tabel.setSelectionForeground(C_BRAND);
        tabel.setEnabled(false); // tidak perlu bisa diseleksi untuk detail

        JTableHeader th = tabel.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(C_BG_MUTED);
        th.setForeground(C_TEXT_MUTED);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        tabel.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(130);

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        sp.getViewport().setBackground(Color.WHITE);

        panel.add(lblSub, BorderLayout.NORTH);
        panel.add(sp,     BorderLayout.CENTER);
        return panel;
    }

    // ── Footer Total + Tombol Tutup ───────────────────────────
    private JPanel buildFooter(String totalBayar) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(C_BG_MUTED);
        panel.setPreferredSize(new Dimension(0, 76));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
            BorderFactory.createEmptyBorder(0, 28, 0, 28)
        ));

        JLabel lblTotal = new JLabel("Total Bayar:");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(C_TEXT_MUTED);

        JLabel lblNilai = new JLabel(totalBayar);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNilai.setForeground(C_BRAND);

        panel.add(lblTotal);
        panel.add(Box.createHorizontalStrut(12));
        panel.add(lblNilai);
        panel.add(Box.createHorizontalGlue());

        panel.add(buatTombolTutup());

        return panel;
    }
    
    private JButton buatTombolTutup() {
        JButton btn = new JButton("Tutup");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(C_TEXT_MAIN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(C_BORDER));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setMaximumSize(new Dimension(100, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(C_BG_MUTED); }
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        
        btn.addActionListener(e -> dispose());
        return btn;
    }
}
