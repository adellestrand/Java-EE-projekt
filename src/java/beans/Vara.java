/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.faces.bean.ManagedBean;

/**
 *
 * @author h12augde
 */
public class Vara{
    private String namn;
    private int id, kategoriID, pris, antal;
    private boolean editable;
    /**
     * Creates a new instance of Varor
     */
    public Vara() {
        this.editable = false;
    }
    public Vara(String namn, int id){
        this.id = id;
        this.namn = namn;
    }

    public int getAntal() {
        return antal;
    }

    public void setAntal(int antal) {
        this.antal = antal;
    }

    public int getPris() {
        return pris;
    }

    public void setPris(int pris) {
        this.pris = pris;
    }

    public int getKategoriID() {
        return kategoriID;
    }

    public void setKategoriID(int kategoriID) {
        this.kategoriID = kategoriID;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public boolean isEditable(){
        return editable;
    }
    public void setEditable(boolean editable){
        this.editable = editable;
    }
    
}
