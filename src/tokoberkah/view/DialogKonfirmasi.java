/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogKonfirmasi extends JDialog {
    
    private boolean isConfirmed = false;

    public DialogKonfirmasi(Frame parent, String namaCustomer, List<Object[]> keranjang, double grandTotal) {
        super(parent, true);
        // Ini kuncinya: Ngilangin title bar bawaan OS biar bisa desain full custom
        setUndecorated(true); 
        
        // Panel Utama
        JPanel panelUtama = new JPanel(new BorderLayout());
        panelUtama.setBackground(Color.WHITE);
        
        // Bikin border biru biar rapi dan nyambung sama tema
        panelUtama.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(37, 99, 235), 2), 
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // Desain HTML Struk Kasir
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 320px; font-family: Segoe UI;'>");
        sb.append("<h2 style='color: #1e293b; margin-top: 0; margin-bottom: 5px; text-align: center;'>Konfirmasi Pesanan</h2>");
        sb.append("<p style='color: #64748b; font-size: 12px; margin-top: 0; text-align: center;'>Customer: <b style='color: #1e293b;'>").append(namaCustomer).append("</b></p>");
        
        sb.append("<hr style='border-top: 1px dashed #cbd5e1; margin: 10px 0;'>");
        
        sb.append("<table style='width: 100%; font-size: 12px; color: #334155;'>");
        
        // --- Header dengan 4 Kolom ---
        sb.append("<tr style='color: #94a3b8; font-size: 10px; text-transform: uppercase;'>");
        sb.append("<th style='text-align: left; padding-bottom: 6px; font-weight: normal;'>Nama Barang</th>");
        sb.append("<th style='text-align: right; padding-bottom: 6px; font-weight: normal;'>Harga</th>"); // Kolom Baru
        sb.append("<th style='text-align: right; padding-bottom: 6px; font-weight: normal;'>Jml</th>");
        sb.append("<th style='text-align: right; padding-bottom: 6px; font-weight: normal;'>Subtotal</th>");
        sb.append("</tr>");

        // --- Isi Tabel ---
        for (Object[] item : keranjang) {
            sb.append("<tr>");
            sb.append("<td style='padding: 4px 0;'>").append(item[1]).append("</td>");
            
            // Format Harga Satuan (item[3])
            sb.append("<td style='text-align: right; padding: 4px 0; color: #64748b;'>").append(String.format("%,.0f", (double) item[3]).replace(",", ".")).append("</td>");
            
            sb.append("<td style='text-align: right; padding: 4px 0;'>x").append(item[2]).append("</td>");
            sb.append("<td style='text-align: right; padding: 4px 0; font-weight: bold;'>Rp ").append(String.format("%,.0f", (double) item[4]).replace(",", ".")).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        
        sb.append("<hr style='border-top: 1px dashed #cbd5e1; margin: 10px 0;'>");
        
        sb.append("<h2 style='color: #dc2626; text-align: right; margin-bottom: 20px; margin-top: 5px;'>Total: Rp ")
          .append(String.format("%,.0f", grandTotal).replace(",", "."))
          .append("</h2>");
        sb.append("</body></html>");

        panelUtama.add(new JLabel(sb.toString()), BorderLayout.CENTER);

        // Panel Tombol Kustom
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelTombol.setBackground(Color.WHITE);

        // Tombol Simpan
        JButton btnYes = new JButton("Simpan Transaksi");
        btnYes.setBackground(new Color(37, 99, 235)); // C_BLUE
        btnYes.setForeground(Color.WHITE);
        btnYes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnYes.setFocusPainted(false);
        btnYes.setBorderPainted(false);
        btnYes.setPreferredSize(new Dimension(140, 38));
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol Batal
        JButton btnNo = new JButton("Batal");
        btnNo.setBackground(new Color(248, 250, 252));
        btnNo.setForeground(new Color(15, 23, 42)); // C_DARK
        btnNo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNo.setFocusPainted(false);
        btnNo.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        btnNo.setPreferredSize(new Dimension(100, 38));
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action Listeners
        btnYes.addActionListener(e -> { isConfirmed = true; dispose(); });
        btnNo.addActionListener(e -> { isConfirmed = false; dispose(); });

        panelTombol.add(btnNo);
        panelTombol.add(btnYes);

        panelUtama.add(panelTombol, BorderLayout.SOUTH);

        add(panelUtama);
        pack(); // Biar ukurannya otomatis nyesuain isi
        setLocationRelativeTo(parent); // Biar muncul persis di tengah layar
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}
