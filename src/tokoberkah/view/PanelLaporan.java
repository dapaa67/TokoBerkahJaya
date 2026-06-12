package tokoberkah.view;

import tokoberkah.dao.CustomerDAO;
import tokoberkah.dao.LaporanDAO;
import tokoberkah.model.Customer;
import tokoberkah.util.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.File;

public class PanelLaporan extends JPanel {

    // ── Komponen Filter ───────────────────────────────────────
    private JTextField     txtDari, txtSampai;
    private JComboBox<String> cmbCustomer;
    private JButton        btnTampilkan, btnReset;

    // ── Stat Labels ───────────────────────────────────────────
    private JLabel lblValTransaksi  = new JLabel("0");
    private JLabel lblValPendapatan = new JLabel("Rp 0");
    private JLabel lblValItem       = new JLabel("0 pcs");
    private JLabel lblValRataRata   = new JLabel("Rp 0");

    // ── Tabel ─────────────────────────────────────────────────
    private JTable            tabel;
    private DefaultTableModel modelTabel;
    private JButton           btnDetail;

    // ── DAO ───────────────────────────────────────────────────
    private final LaporanDAO  laporanDAO  = new LaporanDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    // ── Warna Modern ──────────────────────────────────────────
    private static final Color C_BG         = new Color(248, 250, 252); // Slate 50
    private static final Color C_BRAND      = new Color(37, 99, 235);   // Blue 600
    private static final Color C_BRAND_HOVER= new Color(29, 78, 216);   // Blue 700
    private static final Color C_TEXT_MAIN  = new Color(15, 23, 42);    // Slate 900
    private static final Color C_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    private static final Color C_BORDER     = new Color(226, 232, 240); // Slate 200
    private static final Color C_CARD_BG    = Color.WHITE;

    public PanelLaporan() {
        setLayout(new BorderLayout());
        setBackground(C_BG);
        add(buildTopbar(),  BorderLayout.NORTH);
        add(buildKonten(), BorderLayout.CENTER);
        muatCustomerFilter();
        tampilkanLaporan();
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

        JLabel lblJudul = new JLabel("Laporan Penjualan");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(C_TEXT_MAIN);
        lblJudul.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Pantau performa penjualan, riwayat transaksi, dan statistik pendapatan.");
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
        JPanel konten = new JPanel(new BorderLayout(0, 24));
        konten.setBackground(C_BG);
        konten.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // PnlAtas menampung filter dan stats
        JPanel pnlAtas = new JPanel();
        pnlAtas.setLayout(new BoxLayout(pnlAtas, BoxLayout.Y_AXIS));
        pnlAtas.setOpaque(false);
        
        JPanel filter = buildFilterPanel();
        filter.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlAtas.add(filter);
        pnlAtas.add(Box.createVerticalStrut(16));
        
        JPanel stats = buildStatCards();
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlAtas.add(stats);

        konten.add(pnlAtas, BorderLayout.NORTH);
        konten.add(buildTabelPanel(), BorderLayout.CENTER);

        return konten;
    }

    // ── Filter Panel ──────────────────────────────────────────
    private JPanel buildFilterPanel() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12)) {
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
        card.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Limit height for boxlayout

        JLabel lblDari = new JLabel("Dari:");
        lblDari.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDari.setForeground(C_TEXT_MAIN);

        txtDari = new JTextField(getTanggalAwalBulan());
        txtDari.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtDari.setPreferredSize(new Dimension(120, 36));
        txtDari.setEditable(false); 
        txtDari.setBackground(new Color(248, 250, 252));
        txtDari.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        txtDari.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Frame parent = (Frame) SwingUtilities.getWindowAncestor(PanelLaporan.this);
                DialogKalender dialog = new DialogKalender(parent);
                dialog.setVisible(true);
                if (dialog.getTanggalDipilih() != null) {
                    txtDari.setText(dialog.getTanggalDipilih());
                }
            }
        });

        JLabel lblSampai = new JLabel("Sampai:");
        lblSampai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSampai.setForeground(C_TEXT_MAIN);

        txtSampai = new JTextField(getTanggalHariIni());
        txtSampai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSampai.setPreferredSize(new Dimension(120, 36));
        txtSampai.setEditable(false); 
        txtSampai.setBackground(new Color(248, 250, 252));
        txtSampai.setCursor(new Cursor(Cursor.HAND_CURSOR));

        txtSampai.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Frame parent = (Frame) SwingUtilities.getWindowAncestor(PanelLaporan.this);
                DialogKalender dialog = new DialogKalender(parent);
                dialog.setVisible(true);
                if (dialog.getTanggalDipilih() != null) {
                    txtSampai.setText(dialog.getTanggalDipilih());
                }
            }
        });

        JLabel lblCust = new JLabel("Customer:");
        lblCust.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCust.setForeground(C_TEXT_MAIN);

        cmbCustomer = new JComboBox<>();
        cmbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCustomer.setPreferredSize(new Dimension(180, 36));
        cmbCustomer.setBackground(Color.WHITE);

        btnTampilkan = buatTombol("Tampilkan", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnTampilkan.setPreferredSize(new Dimension(110, 36));

        btnReset = buatTombol("Reset", new Color(241, 245, 249), C_TEXT_MAIN, new Color(226, 232, 240));
        btnReset.setPreferredSize(new Dimension(80, 36));
        btnReset.setBorder(BorderFactory.createLineBorder(C_BORDER));

        card.add(lblDari);
        card.add(txtDari);
        card.add(lblSampai);
        card.add(txtSampai);
        card.add(lblCust);
        card.add(cmbCustomer);
        card.add(btnTampilkan);
        card.add(btnReset);

        btnTampilkan.addActionListener(e -> tampilkanLaporan());
        btnReset.addActionListener(e     -> resetFilter());

        return card;
    }

    // ── 4 Stat Cards ──────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 96));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        JPanel c1 = buatStatCard("Total Transaksi", lblValTransaksi, new Color(219, 234, 254), new Color(37, 99, 235));
        JPanel c2 = buatStatCard("Total Pendapatan", lblValPendapatan, new Color(220, 252, 231), new Color(22, 163, 74));
        JPanel c3 = buatStatCard("Item Terjual", lblValItem, new Color(254, 243, 199), new Color(217, 119, 6));
        JPanel c4 = buatStatCard("Rata-rata / Trx", lblValRataRata, new Color(254, 226, 226), new Color(220, 38, 38));

        row.add(c1); 
        row.add(c2); 
        row.add(c3); 
        row.add(c4);
        return row;
    }

    private JPanel buatStatCard(String labelTitle, JLabel lblVal, Color bgDot, Color accent) {
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

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
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

        JLabel lblTitle = new JLabel("Riwayat Transaksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(C_TEXT_MAIN);

        btnDetail = buatTombol("Lihat Detail", C_BRAND, Color.WHITE, C_BRAND_HOVER);
        btnDetail.setPreferredSize(new Dimension(130, 36));
        btnDetail.setEnabled(false);

        JButton btnExport = buatTombol("Cetak", new Color(16, 185, 129), Color.WHITE, new Color(5, 150, 105)); // Emerald 500
        btnExport.setPreferredSize(new Dimension(130, 36));
        btnExport.setToolTipText("Cetak laporan atau simpan sebagai PDF");

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAksi.setOpaque(false);
        panelAksi.add(btnExport);
        panelAksi.add(btnDetail);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(panelAksi, BorderLayout.EAST);

        tabel = new JTable(modelTabel);
        tabel.setRowHeight(44);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabel.setForeground(C_TEXT_MAIN);
        tabel.setSelectionBackground(new Color(239, 246, 255));
        tabel.setSelectionForeground(C_BRAND);
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(C_BORDER);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader th = tabel.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(C_TEXT_MUTED);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        th.setPreferredSize(new Dimension(0, 40));

        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                btnDetail.setEnabled(tabel.getSelectedRow() >= 0);
                if (e.getClickCount() == 2 && tabel.getSelectedRow() >= 0) {
                    bukaDetail();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tabel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        btnDetail.addActionListener(e -> bukaDetail());
        btnExport.addActionListener(e -> exportKePDF());

        return card;
    }

    // ── Muat Customer ke ComboBox Filter ─────────────────────
    private void muatCustomerFilter() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("Semua");
        for (Customer c : customerDAO.getAll()) {
            cmbCustomer.addItem(c.getIdCustomer() + " - " + c.getNamaCustomer());
        }
    }

    // ── Tampilkan Laporan Sesuai Filter ───────────────────────
    private void tampilkanLaporan() {
        String dari   = txtDari.getText().trim();
        String sampai = txtSampai.getText().trim();

        if (dari.isEmpty() || sampai.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tanggal dari dan sampai wajib diisi!\nFormat: dd-MM-yyyy (contoh: 01-01-2025)",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dariSQL = dari;
        String sampaiSQL = sampai;
        try {
            SimpleDateFormat inFmt = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd");
            dariSQL = outFmt.format(inFmt.parse(dari));
            sampaiSQL = outFmt.format(inFmt.parse(sampai));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String selectedCustomer = (String) cmbCustomer.getSelectedItem();
        String idCustomer = "Semua";
        if (selectedCustomer != null && !selectedCustomer.equals("Semua")) {
            idCustomer = selectedCustomer.split(" - ")[0];
        }

        modelTabel = laporanDAO.getLaporan(dariSQL, sampaiSQL, idCustomer);
        tabel.setModel(modelTabel);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(180);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(140);
        tabel.getColumnModel().getColumn(6).setPreferredWidth(150);
        btnDetail.setEnabled(false);

        int    totalTrx  = laporanDAO.getTotalTransaksi(dariSQL, sampaiSQL);
        double totalPend = laporanDAO.getTotalPendapatan(dariSQL, sampaiSQL);
        int    totalItem = laporanDAO.getTotalItemTerjual(dariSQL, sampaiSQL);
        double rataRata  = totalTrx > 0 ? totalPend / totalTrx : 0;

        lblValTransaksi.setText(String.valueOf(totalTrx));
        lblValPendapatan.setText("Rp " + String.format("%,.0f", totalPend).replace(",", "."));
        lblValItem.setText(String.valueOf(totalItem) + " pcs");
        lblValRataRata.setText("Rp " + String.format("%,.0f", rataRata).replace(",", "."));
    }

    // ── Buka Dialog Detail ────────────────────────────────────
    private void bukaDetail() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        int idJual = (int) modelTabel.getValueAt(row, 1);
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogDetailTransaksi(parent, idJual).setVisible(true);
    }

    // ── Export PDF / Cetak Laporan ────────────────────────────
    private void exportKePDF() {
        if (tabel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diexport!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        fileChooser.setSelectedFile(new File("Laporan_Penjualan.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Judul
                com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph title = new Paragraph("Laporan Penjualan Toko Berkah Jaya\n\n", titleFont);
                title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                document.add(title);

                // Info Periode & Stat
                String dari = txtDari.getText().trim();
                String sampai = txtSampai.getText().trim();
                com.lowagie.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                document.add(new Paragraph("Periode: " + dari + " s/d " + sampai, infoFont));
                document.add(new Paragraph("Total Transaksi: " + lblValTransaksi.getText(), infoFont));
                document.add(new Paragraph("Total Pendapatan: " + lblValPendapatan.getText() + "\n\n", infoFont));

                // Tabel
                PdfPTable pdfTable = new PdfPTable(tabel.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Header Tabel
                com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
                for (int i = 0; i < tabel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tabel.getColumnName(i), headerFont));
                    cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }

                // Isi Tabel
                com.lowagie.text.Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                for (int i = 0; i < tabel.getRowCount(); i++) {
                    for (int j = 0; j < tabel.getColumnCount(); j++) {
                        Object val = tabel.getValueAt(i, j);
                        PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", rowFont));
                        cell.setPadding(5);
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "Berhasil export laporan ke PDF!\nDisimpan di: " + filePath, "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // Buka file otomatis kalau didukung OS
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal membuat PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Reset Filter ──────────────────────────────────────────
    private void resetFilter() {
        txtDari.setText(getTanggalAwalBulan());
        txtSampai.setText(getTanggalHariIni());
        cmbCustomer.setSelectedIndex(0);
        tampilkanLaporan();
    }

    // ── Helper Tanggal ────────────────────────────────────────
    private String getTanggalHariIni() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    private String getTanggalAwalBulan() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
    }

    // ── Helper Komponen ───────────────────────────────────────
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
}
