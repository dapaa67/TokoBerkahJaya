/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.model;

public class Barang {
    private String idBarang, namaBarang, satuan;
    private int    idKategori, stok;
    private double hargaJual;

    public Barang() {}

    public Barang(String idBarang, int idKategori, String namaBarang,
                  String satuan, double hargaJual, int stok) {
        this.idBarang   = idBarang;
        this.idKategori = idKategori;
        this.namaBarang = namaBarang;
        this.satuan     = satuan;
        this.hargaJual  = hargaJual;
        this.stok       = stok;
    }

    public String getIdBarang()             { return idBarang; }
    public void   setIdBarang(String v)     { idBarang = v; }
    public int    getIdKategori()           { return idKategori; }
    public void   setIdKategori(int v)      { idKategori = v; }
    public String getNamaBarang()           { return namaBarang; }
    public void   setNamaBarang(String v)   { namaBarang = v; }
    public String getSatuan()               { return satuan; }
    public void   setSatuan(String v)       { satuan = v; }
    public double getHargaJual()            { return hargaJual; }
    public void   setHargaJual(double v)    { hargaJual = v; }
    public int    getStok()                 { return stok; }
    public void   setStok(int v)            { stok = v; }

    @Override
    public String toString() { return idBarang + " - " + namaBarang; }
}