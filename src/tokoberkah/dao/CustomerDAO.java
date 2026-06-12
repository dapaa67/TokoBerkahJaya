/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah.dao;

import tokoberkah.model.Customer;
import tokoberkah.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_customer ORDER BY id_customer";
        try (Connection c = DBUtil.getConnection();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Customer(
                    r.getString("id_customer"),
                    r.getString("nama_customer"),
                    r.getString("alamat"),
                    r.getString("telepon")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Customer getById(String id) {
        String sql = "SELECT * FROM tb_customer WHERE id_customer = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                return new Customer(
                    r.getString("id_customer"),
                    r.getString("nama_customer"),
                    r.getString("alamat"),
                    r.getString("telepon")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Customer c) {
        String sql = "INSERT INTO tb_customer VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getIdCustomer());
            ps.setString(2, c.getNamaCustomer());
            ps.setString(3, c.getAlamat());
            ps.setString(4, c.getTelepon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Customer c) {
        String sql = "UPDATE tb_customer SET nama_customer=?, alamat=?, " +
                     "telepon=? WHERE id_customer=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNamaCustomer());
            ps.setString(2, c.getAlamat());
            ps.setString(3, c.getTelepon());
            ps.setString(4, c.getIdCustomer());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM tb_customer WHERE id_customer=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
