/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.model;

public class Customer {
    private String idCustomer, namaCustomer, alamat, telepon;

    public Customer() {}

    public Customer(String idCustomer, String namaCustomer,
                    String alamat, String telepon) {
        this.idCustomer   = idCustomer;
        this.namaCustomer = namaCustomer;
        this.alamat       = alamat;
        this.telepon      = telepon;
    }

    public String getIdCustomer()             { return idCustomer; }
    public void   setIdCustomer(String v)     { idCustomer = v; }
    public String getNamaCustomer()           { return namaCustomer; }
    public void   setNamaCustomer(String v)   { namaCustomer = v; }
    public String getAlamat()                 { return alamat; }
    public void   setAlamat(String v)         { alamat = v; }
    public String getTelepon()                { return telepon; }
    public void   setTelepon(String v)        { telepon = v; }

    @Override
    public String toString() { return idCustomer + " - " + namaCustomer; }
}
