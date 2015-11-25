/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author h12augde
 */
@ManagedBean
public class Kundorder {
    private int varuID, antal, summa;
    private String namn;
    /**
     * Creates a new instance of Kundorder
     */
    public Kundorder() {
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }
    
    public int getVaruID() {
        return varuID;
    }

    public void setVaruID(int varuID) {
        this.varuID = varuID;
    }

    public int getAntal() {
        return antal;
    }

    public void setAntal(int antal) {
        this.antal = antal;
    }

    public int getSumma() {
        return summa;
    }

    public void setSumma(int summa) {
        this.summa = summa;
    }
    
}
