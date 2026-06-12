/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DialogKalender extends JDialog {

    private String tanggalDipilih = null;
    private YearMonth bulanAktif;
    private JPanel panelTanggal;
    private JLabel lblBulanTahun;

    private static final Color C_DARK = new Color(30, 42, 58);
    private static final Color C_BLUE = new Color(37, 99, 235);
    private static final Color C_GRAY = new Color(240, 242, 245);
    private static final Color C_BORDER = new Color(229, 231, 235);

    public DialogKalender(Frame parent) {
        super(parent, true);
        setTitle("Pilih Tanggal");
        setSize(320, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        bulanAktif = YearMonth.now();

        // ── Header (Tombol Prev/Next & Label Bulan) ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnPrev = buatTombolNavigasi("<");
        JButton btnNext = buatTombolNavigasi(">");
        
        lblBulanTahun = new JLabel("", SwingConstants.CENTER);
        lblBulanTahun.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBulanTahun.setForeground(C_DARK);

        btnPrev.addActionListener(e -> geserBulan(-1));
        btnNext.addActionListener(e -> geserBulan(1));

        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblBulanTahun, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        // ── Body (Grid Kalender) ──
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);

        // Nama-nama hari
        JPanel panelHari = new JPanel(new GridLayout(1, 7));
        panelHari.setBackground(C_GRAY);
        panelHari.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, C_BORDER));
        String[] hari = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
        for (String h : hari) {
            JLabel l = new JLabel(h, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setForeground(C_DARK);
            l.setPreferredSize(new Dimension(0, 30));
            panelHari.add(l);
        }

        panelTanggal = new JPanel(new GridLayout(0, 7, 2, 2));
        panelTanggal.setBackground(Color.WHITE);
        panelTanggal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        body.add(panelHari, BorderLayout.NORTH);
        body.add(panelTanggal, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);

        renderKalender();
    }

    private void renderKalender() {
        panelTanggal.removeAll();
        
        // Update Label Header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        lblBulanTahun.setText(bulanAktif.format(formatter));

        LocalDate hariPertama = bulanAktif.atDay(1);
        int panjangBulan = bulanAktif.lengthOfMonth();
        
        // Cari tau hari pertama jatuh di hari apa (Senin=1, Minggu=7)
        int offset = hariPertama.getDayOfWeek().getValue() - 1;

        // Bikin kotak kosong buat tanggal sebelum hari pertama
        for (int i = 0; i < offset; i++) {
            panelTanggal.add(new JLabel(""));
        }

        // Bikin tombol untuk setiap tanggal
        LocalDate hariIni = LocalDate.now();
        for (int i = 1; i <= panjangBulan; i++) {
            int tgl = i;
            JButton btnTgl = new JButton(String.valueOf(tgl));
            btnTgl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnTgl.setFocusPainted(false);
            btnTgl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnTgl.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

            // Tandai hari ini
            if (hariIni.equals(bulanAktif.atDay(tgl))) {
                btnTgl.setBackground(new Color(254, 226, 226)); // Warm red
                btnTgl.setForeground(new Color(220, 38, 38));
                btnTgl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                btnTgl.setBackground(Color.WHITE);
                btnTgl.setForeground(C_DARK);
            }

            // Event pas tanggal diklik
            btnTgl.addActionListener(e -> {
                LocalDate dipilih = bulanAktif.atDay(tgl);
                DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                tanggalDipilih = dipilih.format(outFmt);
                dispose();
            });

            panelTanggal.add(btnTgl);
        }

        // Refresh UI
        panelTanggal.revalidate();
        panelTanggal.repaint();
    }

    private void geserBulan(int tambah) {
        bulanAktif = bulanAktif.plusMonths(tambah);
        renderKalender();
    }

    private JButton buatTombolNavigasi(String teks) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(C_DARK);
        btn.setBorder(BorderFactory.createLineBorder(C_BORDER));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(40, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public String getTanggalDipilih() {
        return tanggalDipilih;
    }
}
