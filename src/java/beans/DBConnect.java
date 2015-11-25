/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adell_000 kopplingen mot databasen
 */
public class DBConnect {

    String db = "jdbc:mysql://adellestrand.ddns.net:3306/inlamningsuppgift2?user=admin&password=admin";
    Connection con;
    Statement st;

    //konstruktor

    public DBConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.con = DriverManager.getConnection(db);

        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void newUser(User user) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO kund (fnamn, enamn, email, password) VALUES (?, ?, ?, ?)");
        ps.setString(1, user.getfNamn());
        ps.setString(2, user.geteNamn());
        ps.setString(3, user.geteMail());
        ps.setString(4, user.getPassword());
        ps.executeUpdate();
    }

    public User login(User user) throws SQLException {
        st = con.createStatement();
        User usern = new User();
        ResultSet rs = st.executeQuery("SELECT * FROM kund WHERE password='" + user.getPassword() + "' AND email='" + user.geteMail() + "'");
        if (rs.next()) {
            usern.setUserID(rs.getInt("kundID"));
            usern.seteMail(rs.getString("email"));
            usern.setLoggedin(true);
        }
        return usern;
    }

    public ArrayList<Kategorier> getKategorier() throws SQLException {
        ArrayList<Kategorier> kategori = new ArrayList();
        st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM kategori");
        while (rs.next()) {
            Kategorier kategorin = new Kategorier();
            kategorin.setId(rs.getInt("kategoriID"));
            kategorin.setNamn(rs.getString("kategoriNamn"));
            kategori.add(kategorin);
        }
        return kategori;
    }

    public ArrayList<Vara> getVarorByKategori(int kategoriID) throws SQLException {
        ArrayList<Vara> result = new ArrayList();
        st = con.createStatement();
        try (ResultSet rs = st.executeQuery("SELECT * FROM varor WHERE kategoriID="+kategoriID+"")) {
            while (rs.next()) {
                Vara varor = new Vara();
                varor.setNamn(rs.getString("varuNamn"));
                varor.setId(rs.getInt("varuID"));
                varor.setPris(rs.getInt("pris"));
                result.add(varor);
            }
        }
        return result;
    }

    public ArrayList<Vara> getVaror() throws SQLException {
        ArrayList<Vara> result = new ArrayList();
        st = con.createStatement();
        try (ResultSet rs = st.executeQuery("SELECT * FROM varor")) {
            while (rs.next()) {
                Vara varor = new Vara();
                varor.setNamn(rs.getString("varuNamn"));
                varor.setId(rs.getInt("varuID"));
                varor.setPris(rs.getInt("pris"));
                result.add(varor);
            }
        }
        return result;
    }

    public void deleteVara(Vara vara) throws SQLException {
        PreparedStatement ps = con.prepareStatement("DELETE FROM varor WHERE id=?");
        System.out.println(vara.getId());
        ps.setInt(1, vara.getId());
        ps.executeUpdate();
    }

    public void addVara(Vara vara) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO varor (namn, antal) VALUES (?, ?)");
        ps.setString(1, vara.getNamn());
        ps.executeUpdate();

    }

    public void updateVara(Vara vara) throws SQLException {
        PreparedStatement ps = con.prepareStatement("UPDATE varor SET namn=?, antal=?, editable=? WHERE id=?");
        ps.setString(1, vara.getNamn());

        if (vara.isEditable()) {
            ps.setInt(3, 1);
        } else {
            ps.setInt(3, 0);
        }
        ps.setInt(4, vara.getId());
        ps.executeUpdate();
    }

    public ArrayList<Orders> getOrders(User user) throws SQLException {
        ArrayList<Orders> orders = new ArrayList();
        st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM `order` WHERE kundID=" + user.getUserID() + "");
        while (rs.next()) {
            Orders order = new Orders();
            order.setKundID(rs.getInt("kundID"));
            order.setOrderID(rs.getInt("orderID"));
            order.setDatum(rs.getDate("datum").toString());
            orders.add(order);
        }
        return orders;
    }

    public ArrayList<Kundorder> getOrderHistorik(int orderID) throws SQLException {
        ArrayList<Kundorder> orderHistorik = new ArrayList();
        st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM kundorder WHERE orderID =" + orderID + "");
        while (rs.next()) {
            Kundorder kundorder = new Kundorder();
            kundorder.setAntal(rs.getInt("antal"));
            kundorder.setSumma(rs.getInt("summa"));
            kundorder.setVaruID(rs.getInt("varuID"));
            kundorder.setNamn(getVaruNamn(rs.getInt("varuID")));
            orderHistorik.add(kundorder);
        }
        return orderHistorik;
    }

    public String getVaruNamn(int varuID) throws SQLException {
        st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM varor WHERE varuID=" + varuID + "");
        String varuNamn = "";
        while (rs.next()) {
            varuNamn = rs.getString("varuNamn");
        }
        return varuNamn;
    }

    public void createOrder(Kundvagn kundvagn) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO `order` (kundID) VALUES (?)");
        ps.setInt(1, kundvagn.getKundID());
        ps.executeUpdate();
        st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM `order` WHERE kundID =" + kundvagn.getKundID() + " ORDER BY datum DESC LIMIT 1");
        int orderID = 0;
        while (rs.next()) {
            orderID = rs.getInt("orderID");
        }
        ps = con.prepareStatement("INSERT INTO kundorder (orderID, varuID, antal, summa) VALUES (?, ?, ?, ?)");
        for (Vara v : kundvagn.getVara()) {
            ps.setInt(1, orderID);
            ps.setInt(2, v.getId());
            ps.setInt(3, v.getAntal());
            int summa = v.getAntal() * v.getPris();
            ps.setInt(4, summa);
            ps.executeUpdate();
        }
    }

    public void close() {
        try {
            con.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
