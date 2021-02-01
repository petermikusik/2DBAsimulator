/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatSimulator;

import java.awt.Color;

/**
 *
 * @author Peter Mikušík
 */
public class StavBunky {
    private static int counter;
    private String nazov;
    private Color farba;
    private int index;

    public StavBunky(String nazov, Color farba) {
        this.nazov = nazov;
        this.farba = farba;
        this.index = counter++;
    }
    
    public StavBunky(String nazov, Color farba, int index) {
        this.nazov = nazov;
        this.farba = farba;
        this.index = index;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public void setFarba(Color farba) {
        this.farba = farba;
    }
    
    public static void setCounter(int cislo){
        counter = cislo;
    }

    public String getNazov() {
        return nazov;
    }

    public Color getFarba() {
        return farba;
    }

    public int getIndex() {
        return index;
    }
}
