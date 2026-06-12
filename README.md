# Toko Berkah Jaya - Sistem Manajemen Penjualan

Aplikasi Point of Sales (Kasir) & Manajemen Inventori modern berbasis **Java Swing** dengan antarmuka dinamis menggunakan **FlatLaf**. Dibuat untuk memenuhi kebutuhan pengelolaan data transaksi, stok barang, pelanggan, dan pembuatan laporan secara terkomputerisasi.

## Fitur Utama
- **Dashboard Interaktif**: Ringkasan data penjualan, total barang terjual, pendapatan kotor, dan jumlah pelanggan secara *real-time*.
- **Manajemen Inventori (Barang & Kategori)**: Mengelola master data barang (stok, harga jual, dan kategori) dengan sistem pencarian pintar.
- **Manajemen Customer**: Pencatatan data pelanggan (baik pelanggan tetap maupun umum).
- **Sistem Kasir (Point of Sales)**: Fitur transaksi keranjang belanja yang cepat, lengkap dengan kalkulasi kembalian dan pemotongan stok otomatis.
- **Laporan Penjualan & Cetak PDF**: Rekapitulasi riwayat transaksi dengan filter periode tanggal (Dari - Sampai) dan filter nama pelanggan. Laporan dapat langsung **diekspor ke format PDF** (didukung oleh *iText*).


## Teknologi yang Digunakan
- **Bahasa Pemrograman**: Java (JDK 8 / 11+)
- **IDE**: Apache NetBeans
- **GUI Framework**: Java Swing
- **Tema / Desain**: [FlatLaf](https://www.formdev.com/flatlaf/) (Modern Light UI)
- **Database**: MySQL (MariaDB via XAMPP)
- **Library Eksternal**: 
  - `mysql-connector-j` (Koneksi Database)
  - `iText 2.1.7` (Engine Export Laporan PDF)

## Cara Instalasi & Menjalankan Aplikasi
1. **Siapkan Database**:
   - Buka XAMPP Control Panel, lalu *Start* modul **Apache** dan **MySQL**.
   - Buka browser dan masuk ke `http://localhost/phpmyadmin`.
   - Buat database baru dengan nama `db_berkah_jaya`.
   - Lakukan **Import** menggunakan file `db_berkah_jaya.sql` yang tersedia di dalam folder project ini.
2. **Buka Project di NetBeans**:
   - Buka Apache NetBeans.
   - Pilih *File > Open Project* dan pilih folder `TokoBerkahJaya`.
3. **Pastikan Library Siap**:
   - Cek folder `Libraries` di panel *Projects*. Pastikan *FlatLaf*, *MySQL Connector*, dan *iText* sudah di-load (tidak berwarna merah).
4. **Jalankan Aplikasi**:
   - Cari file utama / Login form (misalnya `Login.java` atau `MainApp.java`).
   - Klik kanan -> **Run File** (atau `Shift+F6`).
5. **Akses Default**:
   - **Username**: `admin`
   - **Password**: `admin123`
  

## Developer
- Muhammad Daffa | 221011400800 | 06tplp014


