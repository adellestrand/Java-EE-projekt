/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author h12augde
 */
@ManagedBean
@SessionScoped
public class VarorDB implements Serializable, ValueChangeListener {
    private DBConnect dbcon;
    private ArrayList<Vara> al;
    private Kundvagn kundvagn = new Kundvagn();
    private int orderID, kategoriID;
    double summa;
    private ArrayList<Kundorder> orderHistorik = new ArrayList();
    private User user = new User();
    /**
     * Creates a new instance of VarorDB
     */
    public VarorDB() throws SQLException {
        dbcon = new DBConnect();
        al = dbcon.getVaror();
    }
    public String addAction(Vara vara) throws SQLException{
        dbcon.addVara(vara);
        setAll();
        return null;
    }
    public String deleteAction(Vara vara) throws SQLException{
        for(Vara v : kundvagn.getVara()){
            if(vara.getId() == v.getId()){
                kundvagn.getVara().remove(v);
            }
        }
        setAll();
        return null;
    }
    public ArrayList<Vara> getAll() throws SQLException{
        
        return al;
    }
    public void setAll() throws SQLException{
        al = dbcon.getVaror();
    }
    public String addVaraToKundvagn(Vara vara) throws SQLException{
        if(isLogedin()){
            if(kundvagn.getVara().size()!=0){
            boolean index = true;
            for(int i = 0; i < kundvagn.getVara().size(); i++){
                if(kundvagn.getVara().get(i).getId() == vara.getId()){
                    index = false;
                    kundvagn.getVara().get(i).setAntal(kundvagn.getVara().get(i).getAntal()+1);
                }
            }
                if(index){
                    vara.setAntal(1);
                    kundvagn.getVara().add(vara);
                }
            
            }else{
                vara.setAntal(1);
                kundvagn.getVara().add(vara); 
            }
            return null;
        }else{
            return "index.xhtml";
        }
        
    }
    public ArrayList<Vara> getKundvagn() throws SQLException{
        setAll();
        return kundvagn.getVara();
    }
    public String deleteVara(Vara vara){
        if(vara.getAntal()==1){
           // for(int i = 0; i < kundvagn.size(); i++){
             //   if(kundvagn.get(i).getId() == vara.getId()){
               //     kundvagn.remove(i);
               // }
           // }
        }else{
            vara.setAntal(vara.getAntal()-1);
        }
        
        return null;
    }
    public String updateVara(ArrayList<Vara> varor){
        for(Vara vara : varor){
            if(vara.isEditable()){
                vara.setEditable(false);
            }
        }
        return null;
    }
    public String sendKundvagn() throws SQLException{
        
        dbcon.createOrder(kundvagn);
        String to = user.geteMail();
            String password = "JavaEnterprise2014";
            String from = "Javaiskolan";
            String host = "smtp.gmail.com";
            Properties props = System.getProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(to, password);
			}
		  });
            
            try{
                //skapar upp en medelande och sätter vart det kommer ifrån
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from,"h12augde"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to,"h12augde"));
                
                message.setSubject("Thanks for your order");
                StringBuilder sb = new StringBuilder();
                for(Vara v : kundvagn.getVara()){
                    sb.append(v.getNamn()+"\t");
                    sb.append(v.getAntal()+"\t");
                    sb.append(v.getPris()+"kr \n");
                }
                sb.append("Sum: "+getSum()+"kr");
                message.setText(sb.toString());
                //skrickar medeleandet
                Transport.send(message);
            }catch (MessagingException ex){
                ex.printStackTrace();;
            } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(VarorDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "paypal.xhtml";
    }
    public int getOrderName() throws SQLException{
        ArrayList<Orders> ord = dbcon.getOrders(user);
        int size = ord.size();
        return ord.get(size-1).getOrderID();
    }
    public double getSum(){
        int sum = 0;
        for(int i = 0; i < kundvagn.getVara().size(); i++){
            int antal = kundvagn.getVara().get(i).getAntal();
            int pris = kundvagn.getVara().get(i).getPris();
            sum = sum+(antal*pris);
        }
        summa = (double)sum;
        return summa;
    }
    public void setSum(double summa){
        this.summa = summa;
    }
    public String login(User user) throws SQLException{
        this.user = dbcon.login(user);
        kundvagn.setKundID(this.user.getUserID());
        if(this.user.isLoggedin()){
            return "admin.xhtml";
        }else{
            return null;
        }
    }
    public String newUser(User user) throws SQLException{
        dbcon.newUser(user);
        return "index.xhtml";
    }
    public boolean isLogedin(){
        return user.isLoggedin();
    }
    public ArrayList<Orders> getOrders() throws SQLException{
        return dbcon.getOrders(user);
    }
    //http://www.tutorialspoint.com/jsf/jsf_valuechangelistener_tag.htm
    public void orderHistory(ValueChangeEvent e) throws SQLException{
        //assign new value to country
        orderID = Integer.parseInt(e.getNewValue().toString());
        orderHistorik = dbcon.getOrderHistorik(orderID);
    }
    public void kategorierLista(ValueChangeEvent e) throws SQLException{
        //assign new value to country
        kategoriID = Integer.parseInt(e.getNewValue().toString());
        al = dbcon.getVarorByKategori(kategoriID);
    }
    public ArrayList<Kundorder> getOrderHistorik(){
        return orderHistorik;
    }
    public ArrayList<Kategorier> getKategorier() throws SQLException{
        return dbcon.getKategorier();
    }
    public String setEditable(Vara vara) throws SQLException{
        for(int i = 0; i < kundvagn.getVara().size(); i++){
            if(kundvagn.getVara().get(i).getId() == vara.getId()){
                kundvagn.getVara().get(i).setEditable(true);
            }
        }
        return null;
    }
    /*public void paypal() throws IOException{
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect("https://www.sandbox.paypal.com/cgi-bin/webscr");
        
    }*/
    public String logout(){
        user.setLoggedin(false);
        kundvagn = new Kundvagn();
        return "index.xhtml";
    }
    @Override
    public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
