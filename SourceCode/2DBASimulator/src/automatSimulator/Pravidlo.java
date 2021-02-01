/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatSimulator;

/**
 *
 * @author Peter Mikušík
 */
public class Pravidlo {
    private int fromIndex; //z ktorej bunky
    private int toIndex; // na ktoru bunku
    private int pocet; // pri pocte
    private int indexSusednej; // ktorej susednej bunky
    private int pravdepodobnost;

    public Pravidlo(int fromIndex, int toIndex, int pocet, int indexSusednej, int pPravdepodobnost) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.pocet = pocet;
        this.indexSusednej = indexSusednej;
        this.pravdepodobnost = pPravdepodobnost;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public void setPocet(int pocet) {
        this.pocet = pocet;
    }

    public void setIndexSusednej(int indexSusednej) {
        this.indexSusednej = indexSusednej;
    }

    public void setPravdepodobnost(int pravdepodobnost) {
        this.pravdepodobnost = pravdepodobnost;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public int getPocet() {
        return pocet;
    }

    public int getIndexSusednej() {
        return indexSusednej;
    }
    
    public int getPravdepodobnost(){
        return pravdepodobnost;
    }
}
