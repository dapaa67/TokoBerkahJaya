/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tokoberkah;

import com.formdev.flatlaf.FlatLightLaf;
import tokoberkah.view.FormLogin;

import tokoberkah.util.DBUtil;

public class Main {
    public static void main(String[] args) {
        DBUtil.initDatabase();
        FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });
    }
}