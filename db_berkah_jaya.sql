-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 12 Jun 2026 pada 10.57
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_berkah_jaya`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_barang`
--

CREATE TABLE `tb_barang` (
  `id_barang` varchar(10) NOT NULL,
  `id_kategori` int(11) DEFAULT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `satuan` varchar(20) DEFAULT NULL,
  `harga_jual` double NOT NULL,
  `stok` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_barang`
--

INSERT INTO `tb_barang` (`id_barang`, `id_kategori`, `nama_barang`, `satuan`, `harga_jual`, `stok`) VALUES
('B001', 1, 'Indomie Goreng', 'Pcs', 3500, 2),
('B002', 2, 'Aqua 600ml', 'Botol', 4000, 24),
('B003', 1, 'Beras 5kg', 'Karung', 65000, 8),
('B004', 3, 'Sabun Mandi Lifeboy', 'Pcs', 8000, 14),
('B005', 2, 'Teh Botol Sosro', 'Botol', 5000, 8),
('B006', 3, 'Deterjen Rinso 800g', 'Bungkus', 24000, 3),
('B007', 1, 'Susu Ultramilk', 'Botol', 6000, 0),
('B008', 1, 'Teh Pucuk Segar', 'Botol', 10000, 472),
('B009', 6, 'Pensil 2B', 'Pcs', 4000, 25),
('B010', 3, 'Gelas', 'Pcs', 10000, 100);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_customer`
--

CREATE TABLE `tb_customer` (
  `id_customer` varchar(10) NOT NULL,
  `nama_customer` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_customer`
--

INSERT INTO `tb_customer` (`id_customer`, `nama_customer`, `alamat`, `telepon`) VALUES
('C001', 'Budi Santoso', 'Jl. Mawar No. 12', '081234567890'),
('C002', 'Siti Rahayu', 'Jl. Melati No. 5', '082345678901'),
('C003', 'Andi Wijaya', 'Jl. Kenanga No. 8', '083456789012'),
('C004', 'Raka Sukirman', 'Jl. bandung No.23', '08237812399');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_detail_penjualan`
--

CREATE TABLE `tb_detail_penjualan` (
  `id_detail` int(11) NOT NULL,
  `id_jual` int(11) NOT NULL,
  `id_barang` varchar(10) NOT NULL,
  `jumlah_beli` int(11) NOT NULL,
  `harga_satuan` double NOT NULL,
  `subtotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_detail_penjualan`
--

INSERT INTO `tb_detail_penjualan` (`id_detail`, `id_jual`, `id_barang`, `jumlah_beli`, `harga_satuan`, `subtotal`) VALUES
(1, 1, 'B001', 12, 3500, 42000),
(2, 1, 'B002', 40, 4000, 160000),
(3, 2, 'B005', 50, 5000, 250000),
(4, 3, 'B007', 48, 6000, 288000),
(5, 4, 'B002', 12, 4000, 48000),
(6, 5, 'B001', 13, 3500, 45500),
(7, 6, 'B001', 5, 3500, 17500),
(8, 7, 'B004', 5, 8000, 40000),
(9, 8, 'B001', 12, 3500, 42000),
(10, 9, 'B001', 6, 3500, 21000),
(11, 10, 'B002', 2, 4000, 8000),
(12, 10, 'B006', 1, 24000, 24000),
(13, 11, 'B008', 32, 10000, 320000),
(14, 11, 'B004', 2, 8000, 16000),
(15, 12, 'B003', 12, 65000, 780000),
(16, 13, 'B004', 12, 8000, 96000),
(17, 13, 'B007', 2, 6000, 12000),
(18, 14, 'B002', 2, 4000, 8000),
(19, 14, 'B004', 2, 8000, 16000),
(20, 14, 'B005', 2, 5000, 10000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_kategori`
--

CREATE TABLE `tb_kategori` (
  `id_kategori` int(11) NOT NULL,
  `nama_kategori` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_kategori`
--

INSERT INTO `tb_kategori` (`id_kategori`, `nama_kategori`) VALUES
(1, 'Makanan'),
(2, 'Minuman'),
(3, 'Kebutuhan Rumah'),
(6, 'Alat Tulis');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_penjualan`
--

CREATE TABLE `tb_penjualan` (
  `id_jual` int(11) NOT NULL,
  `tgl_transaksi` date NOT NULL,
  `id_customer` varchar(10) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `total_bayar` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_penjualan`
--

INSERT INTO `tb_penjualan` (`id_jual`, `tgl_transaksi`, `id_customer`, `id_user`, `total_bayar`) VALUES
(1, '2026-05-04', 'C001', 1, 202000),
(2, '2026-05-04', 'C002', 1, 250000),
(3, '2026-05-04', 'C003', 1, 288000),
(4, '2026-05-04', 'C003', 1, 48000),
(5, '2026-05-04', 'C001', 1, 45500),
(6, '2026-05-05', 'C001', 2, 17500),
(7, '2026-05-05', 'C001', 2, 40000),
(8, '2026-05-07', 'C002', 2, 42000),
(9, '2026-05-17', NULL, 1, 21000),
(10, '2026-05-18', NULL, 1, 32000),
(11, '2026-05-18', NULL, 1, 336000),
(12, '2026-06-08', NULL, 1, 780000),
(13, '2026-06-08', NULL, 1, 108000),
(14, '2026-06-12', NULL, 1, 34000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_user`
--

CREATE TABLE `tb_user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(100) DEFAULT NULL,
  `level` enum('Admin','Petugas') DEFAULT 'Petugas'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_user`
--

INSERT INTO `tb_user` (`id_user`, `username`, `password`, `nama_lengkap`, `level`) VALUES
(1, 'admin', 'admin123', 'Administrator', 'Admin'),
(2, 'petugas', 'petugas123', 'Petugas Toko', 'Petugas');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `tb_barang`
--
ALTER TABLE `tb_barang`
  ADD PRIMARY KEY (`id_barang`),
  ADD KEY `id_kategori` (`id_kategori`);

--
-- Indeks untuk tabel `tb_customer`
--
ALTER TABLE `tb_customer`
  ADD PRIMARY KEY (`id_customer`);

--
-- Indeks untuk tabel `tb_detail_penjualan`
--
ALTER TABLE `tb_detail_penjualan`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `id_jual` (`id_jual`),
  ADD KEY `id_barang` (`id_barang`);

--
-- Indeks untuk tabel `tb_kategori`
--
ALTER TABLE `tb_kategori`
  ADD PRIMARY KEY (`id_kategori`);

--
-- Indeks untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  ADD PRIMARY KEY (`id_jual`),
  ADD KEY `id_customer` (`id_customer`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `tb_user`
--
ALTER TABLE `tb_user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `tb_detail_penjualan`
--
ALTER TABLE `tb_detail_penjualan`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT untuk tabel `tb_kategori`
--
ALTER TABLE `tb_kategori`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  MODIFY `id_jual` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT untuk tabel `tb_user`
--
ALTER TABLE `tb_user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `tb_barang`
--
ALTER TABLE `tb_barang`
  ADD CONSTRAINT `tb_barang_ibfk_1` FOREIGN KEY (`id_kategori`) REFERENCES `tb_kategori` (`id_kategori`);

--
-- Ketidakleluasaan untuk tabel `tb_detail_penjualan`
--
ALTER TABLE `tb_detail_penjualan`
  ADD CONSTRAINT `tb_detail_penjualan_ibfk_1` FOREIGN KEY (`id_jual`) REFERENCES `tb_penjualan` (`id_jual`),
  ADD CONSTRAINT `tb_detail_penjualan_ibfk_2` FOREIGN KEY (`id_barang`) REFERENCES `tb_barang` (`id_barang`);

--
-- Ketidakleluasaan untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  ADD CONSTRAINT `tb_penjualan_ibfk_1` FOREIGN KEY (`id_customer`) REFERENCES `tb_customer` (`id_customer`),
  ADD CONSTRAINT `tb_penjualan_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `tb_user` (`id_user`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
