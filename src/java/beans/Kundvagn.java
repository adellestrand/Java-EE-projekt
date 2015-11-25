/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author h12augde
 */
@ManagedBean
@SessionScoped
public class Kundvagn {
    private int kundID;
    private ArrayList<Vara> vara;
    
    /**
     * Creates a new instance of Kundvagn
     */
    public Kundvagn() {
        vara = new ArrayList();
    }

    public int getKundID() {
        return kundID;
    }

    public void setKundID(int kundID) {
        this.kundID = kundID;
    }

    public ArrayList<Vara> getVara() {
        return vara;
    }

    public void setVara(ArrayList<Vara> vara) {
        this.vara = vara;
    }
    
}
