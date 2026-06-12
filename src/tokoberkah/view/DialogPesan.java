/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import javax.swing.*;
import java.awt.*;

public class DialogPesan extends JDialog {

    public DialogPesan(Frame parent, String judul, String pesan, boolean isSukses) {
        super(parent, true);
        setUndecorated(true); // Ilangin border standar OS

        // Tentukan warna dan ikon berdasarkan status (Sukses/Gagal)
        Color warnaTema = isSukses ? new Color(22, 163, 74) : new Color(220, 38, 38);
        String iconSymbol = isSukses ? "✔" : "✖";

        JPanel panelUtama = new JPanel(new BorderLayout());
        panelUtama.setBackground(Color.WHITE);
        
        // Border luar menyesuaikan warna tema (Hijau/Merah)
        panelUtama.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(warnaTema, 2),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // ── Icon Besar di Atas ──
        JLabel lblIcon = new JLabel(iconSymbol, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 46));
        lblIcon.setForeground(warnaTema);

        // ── Teks Judul & Pesan (Pakai HTML biar rapi) ──
        JLabel lblPesan = new JLabel("<html><div style='text-align: center; font-family: Segoe UI;'>" +
            "<h2 style='color: #1e293b; margin: 10px 0 5px 0;'>" + judul + "</h2>" +
            "<p style='color: #64748b; font-size: 12px; margin: 0;'>" + pesan + "</p>" +
            "</div></html>", SwingConstants.CENTER);

        JPanel panelTengah = new JPanel(new BorderLayout());
        panelTengah.setBackground(Color.WHITE);
        panelTengah.add(lblIcon, BorderLayout.NORTH);
        panelTengah.add(lblPesan, BorderLayout.CENTER);

        // ── Tombol OKE ──
        JButton btnOk = new JButton("Oke");
        btnOk.setBackground(warnaTema);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnOk.setFocusPainted(false);
        btnOk.setBorderPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.setPreferredSize(new Dimension(120, 36));
        btnOk.addActionListener(e -> dispose()); // Tutup pop-up pas diklik

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        panelBawah.setBackground(Color.WHITE);
        panelBawah.add(btnOk);

        // ── Gabung Semua ──
        panelUtama.add(panelTengah, BorderLayout.CENTER);
        panelUtama.add(panelBawah, BorderLayout.SOUTH);

        add(panelUtama);
        pack();
        // Set minimal lebar biar gak kekecilan kalau teksnya pendek
        setSize(Math.max(300, getWidth()), getHeight()); 
        setLocationRelativeTo(parent); // Muncul di tengah layar
    }
}
