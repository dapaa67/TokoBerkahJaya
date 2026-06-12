/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.model;

public class Penjualan {
    private int    idJual, jumlahBeli, idUser;
    private String tglTransaksi, idCustomer, idBarang;
    private double totalBayar;

    public Penjualan() {}

    public Penjualan(int idJual, String tglTransaksi, String idCustomer,
                     String idBarang, int jumlahBeli, double totalBayar, int idUser) {
        this.idJual       = idJual;
        this.tglTransaksi = tglTransaksi;
        this.idCustomer   = idCustomer;
        this.idBarang     = idBarang;
        this.jumlahBeli   = jumlahBeli;
        this.totalBayar   = totalBayar;
        this.idUser       = idUser;
    }

    public int    getIdJual()                { return idJual; }
    public void   setIdJual(int v)           { idJual = v; }
    public String getTglTransaksi()          { return tglTransaksi; }
    public void   setTglTransaksi(String v)  { tglTransaksi = v; }
    public String getIdCustomer()            { return idCustomer; }
    public void   setIdCustomer(String v)    { idCustomer = v; }
    public String getIdBarang()              { return idBarang; }
    public void   setIdBarang(String v)      { idBarang = v; }
    public int    getJumlahBeli()            { return jumlahBeli; }
    public void   setJumlahBeli(int v)       { jumlahBeli = v; }
    public double getTotalBayar()            { return totalBayar; }
    public void   setTotalBayar(double v)    { totalBayar = v; }
    public int    getIdUser()                { return idUser; }
    public void   setIdUser(int v)           { idUser = v; }
}
