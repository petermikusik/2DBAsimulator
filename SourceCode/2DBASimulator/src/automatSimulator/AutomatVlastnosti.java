/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatSimulator;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Peter Mikušík
 */
public class AutomatVlastnosti {
    private int stlpce = 100, riadky = 50;
    private ArrayList<StavBunky> cellTypes = new ArrayList<StavBunky>();
    private ArrayList<Pravidlo> rules = new ArrayList<Pravidlo>();
    private int indexVychodzejBunky = -1;
    private boolean mooroveOkolie = true;
    private boolean isNastaveny = false;
    

    public AutomatVlastnosti() {
    }

    public HashMap<Integer, Color> getFarbyBuniek(){
        HashMap<Integer, Color> farby = new HashMap<Integer, Color>();
        for(int i = 0; i < cellTypes.size(); i++) {
            farby.put(cellTypes.get(i).getIndex(), cellTypes.get(i).getFarba());
        }
        return farby;
    }

    public boolean isIsNastaveny() {
        return isNastaveny;
    }

    public void setIsNastaveny(boolean isNastaveny) {
        this.isNastaveny = isNastaveny;
    }
    
    

    public void setMooroveOkolie(boolean mooroveOkolie) {
        this.mooroveOkolie = mooroveOkolie;
    }

    public boolean isMooroveOkolie() {
        return mooroveOkolie;
    }
    
    
    public void zmazBunku(String nazov, boolean edit){
        int indexBunky = -1;
        int indexvListe = -1;
        ArrayList<Integer> indexPravidiel = new ArrayList<>();
        for (int i = 0; i < this.cellTypes.size(); i++) {
            if (cellTypes.get(i).getNazov().equals(nazov)) {
                indexBunky = cellTypes.get(i).getIndex();
                indexvListe = i;
            }
        }
        
        
        if(edit == false && indexBunky == this.indexVychodzejBunky){
            this.indexVychodzejBunky = -1;
        }
        
        
        if(indexvListe > -1){
            this.cellTypes.remove(indexvListe);
            if(edit == false){
                for (int i = 0; i < this.rules.size(); i++) {      
                    Pravidlo rule = rules.get(i);
                    if (rule.getFromIndex() == indexBunky || rule.getToIndex() == indexBunky || rule.getIndexSusednej() == indexBunky) {
                        indexPravidiel.add(i);
                    }                
                }    
                if(indexPravidiel.size() > 0){
                    for (int i = indexPravidiel.size()-1; i >= 0; i--) {
                        this.rules.remove(indexPravidiel.get(i).intValue());
                    }
                }
            }
        }                  
    }    
    
    public void zmazPravidlo(int from, int to, int pocet, int indexSusednej, int pravdepodobnost){
        int index = -1;
        for (int i = 0; i < this.rules.size(); i++) {
            Pravidlo r = this.rules.get(i);
            if(r.getFromIndex() == from && r.getToIndex() == to && r.getPocet() == pocet && r.getIndexSusednej() == indexSusednej && r.getPravdepodobnost() == pravdepodobnost){
                index = i;
            }
        }
        if(index > -1){
            this.rules.remove(index);
        }
    }

    public int getIndexVychodzejBunky() {
        return indexVychodzejBunky;
    }

    public void setIndexVychodzejBunky(int indexVychodzejBunky) {
        this.indexVychodzejBunky = indexVychodzejBunky;
    }
    
    
    public int getStlpce() {
        return stlpce;
    }

    public int getRiadky() {
        return riadky;
    }

    public void setStlpce(int stlpce) {
        this.stlpce = stlpce;
    }

    public void setRiadky(int riadky) {
        this.riadky = riadky;
    }
    
    public boolean duplicitaBunky(String nazov){
        for (StavBunky cellType : cellTypes) {
            if(cellType.getNazov().equals(nazov)){
                return true;
            }
        }
        return false;
    }
    
    public boolean pridajCellType(StavBunky cell){
        boolean existuje = duplicitaBunky(cell.getNazov());
        
        if(existuje){
            return false;
        } else {
            this.cellTypes.add(cell);
            return true;
        }
    }
    
    public boolean pridajCellTypeIndex(StavBunky cell, int index){
        boolean existuje = duplicitaBunky(cell.getNazov());
        
        if(existuje){
            return false;
        } else {
            this.cellTypes.add(index, cell);
            return true;
        }
    }
    
    public boolean duplicitaPravidla(int from, int to, int pocetSusedov, int neighbor) {
        for (Pravidlo rule : rules) {
            if(rule.getFromIndex() == from && rule.getToIndex() == to & rule.getPocet() == pocetSusedov && rule.getIndexSusednej() == neighbor){
                return true;
            }
        }
        return false;
    }
    
    public boolean pridajRule(Pravidlo pRule){
        boolean existuje = false;
        for (Pravidlo rule : rules) {
            if (rule.getFromIndex() == pRule.getFromIndex() && rule.getToIndex() == pRule.getToIndex() && rule.getPocet() == pRule.getPocet() && rule.getIndexSusednej() == pRule.getIndexSusednej()) {
                existuje = true;
            }
        }
        if(existuje){
            return false;
        } else {
            this.rules.add(pRule);
            return true;
        }
    }
    
    public boolean pridajRuleOnIndex(Pravidlo pRule, int index){
        boolean existuje = false;
        for (Pravidlo rule : rules) {
            if (rule.getFromIndex() == pRule.getFromIndex() && rule.getToIndex() == pRule.getToIndex() && rule.getPocet() == pRule.getPocet() && rule.getIndexSusednej() == pRule.getIndexSusednej()) {
                existuje = true;
            }
        }
        if(existuje){
            return false;
        } else {
            this.rules.add(index, pRule);
            return true;
        }
    }
    
    public String getCellName(int cellIndex){
        String meno = "";
        for (StavBunky cellType : cellTypes) {
            if(cellType.getIndex() == cellIndex){
                meno = cellType.getNazov();
            }
        }
        return meno;
    }
    
    public StavBunky getCellType(int indexVListe){
        return this.cellTypes.get(indexVListe);
    }
    
    public int getCellIndex(String nazov){
        for (StavBunky cellType : cellTypes) {
            if(cellType.getNazov().equals(nazov)){
                return cellType.getIndex();
            }
        }
        return -1;
    }
    
    public Pravidlo getRule(int index){
        return this.rules.get(index);
    }
    
    public int getCellTypeCount(){
        return this.cellTypes.size();
    }
    
    public int getRulesCount(){
        return this.rules.size();
    }
    
    public boolean nacitajZoSuboru(Automat automat){
        String userDir = System.getProperty("user.home");
        JFileChooser chooser = new JFileChooser(new File(userDir + "/Desktop"));
        chooser.setDialogTitle("Načítať zo súboru");
        chooser.setFileFilter(new FileTypeFilter(".txt", "Textový dokument"));
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(null);
        int pocetCellTypesZoSuboru = -1;
        int pocetRulesZoSuboru = -1;
        int generaciaZoSuboru = -1;
        int[][] currentMoveZoSuboru = new int[0][0];
        int rowCounterZoSuboru = 0;
        boolean currentMoveSetZoSuboru = false;
        int stlpceZoSuboru = -1;
        int riadkyZoSuboru = -1;
        ArrayList<StavBunky> cellTypesZoSuboru = new ArrayList<StavBunky>();
        ArrayList<Pravidlo> rulesZoSuboru = new ArrayList<Pravidlo>();
        int indexVychodzejZoSuboru = -1;
        boolean mooroveZoSuboru = false;
        boolean isDefault = false;
        if (result == JFileChooser.APPROVE_OPTION) {
            //nacitanie zo suboru            
            try {
                File fi = chooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(fi.getPath()));
                String line = "";
                //String s = "";
                while ((line = reader.readLine()) != null){
                    String[] data = line.split(";");                    
                    switch(data[0]){
                        case "COLS": 
                            stlpceZoSuboru = Integer.parseInt(data[1]);
                            break;
                        case "ROWS": 
                            riadkyZoSuboru = Integer.parseInt(data[1]);
                            break;                        
                        case "CELLTYPES": 
                            pocetCellTypesZoSuboru = Integer.parseInt(data[1]);
                            break;
                        case "CELLTYPE": 
                            cellTypesZoSuboru.add(new StavBunky(data[1], new Color(Integer.parseInt(data[2])), Integer.parseInt(data[3])));
                            break;
                        case "RULES": 
                            pocetRulesZoSuboru = Integer.parseInt(data[1]);
                            break;
                        case "RULE": 
                            rulesZoSuboru.add(new Pravidlo(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5])));
                            break;
                        case "indexDefaultCellType": 
                            indexVychodzejZoSuboru = Integer.parseInt(data[1]);
                            break;
                        case "mooreNeighborhood": 
                            if(Integer.parseInt(data[1]) == 0){
                                mooroveZoSuboru = false;  
                            } else {
                                mooroveZoSuboru = true;
                            }
                            break;
                        case "GENERATION": 
                            generaciaZoSuboru = Integer.parseInt(data[1]);
                            break;
                        case "ROW":
                            if(currentMoveSetZoSuboru == false){
                                if (riadkyZoSuboru > -1 && stlpceZoSuboru > -1) {
                                    currentMoveZoSuboru = new int[riadkyZoSuboru][stlpceZoSuboru];
                                    currentMoveSetZoSuboru = true;
                                }                                
                            }                            
                            for (int i = 0; i < stlpceZoSuboru; i++) {
                                currentMoveZoSuboru[rowCounterZoSuboru][i] = Integer.parseInt(data[i+1]);
                            }
                            rowCounterZoSuboru++;
                            break;
                        case "CURRENTMOVE":
                            break;
                        default: 
                            isDefault = true;
                            break;
                    }
                }
                if(reader != null){
                    reader.close();
                }
                
                if (isDefault == false) {
                    this.stlpce = stlpceZoSuboru;
                    this.riadky = riadkyZoSuboru;
                    this.cellTypes = cellTypesZoSuboru;
                    this.rules = rulesZoSuboru;
                    this.indexVychodzejBunky = indexVychodzejZoSuboru;
                    this.mooroveOkolie = mooroveZoSuboru;
                    automat.setGeneracia(generaciaZoSuboru);
                    automat.setCurrentMove(currentMoveZoSuboru); 
                    int maxIndex = -1;
                    for (StavBunky cellType : this.cellTypes) {
                        if(cellType.getIndex() > maxIndex){
                            maxIndex = cellType.getIndex();
                        }
                    }
                    StavBunky.setCounter(maxIndex + 1);
                    return true;
                } else {
                    return false;
                }                
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }            
        } else {
            return false;
        }        
    }   
    
    public void ulozDoSuboru(int[][] currentMove, int generacia){
        String uloz = "";
        uloz += "COLS;" + this.stlpce +"\r\n"; //toto
        uloz += "ROWS;" + this.riadky +"\r\n"; //toto
        uloz += "CELLTYPES;" + this.getCellTypeCount() + "\r\n";
        for (StavBunky cellType : this.cellTypes) {
            uloz += "CELLTYPE;";
            uloz += cellType.getNazov() +";";
            uloz += cellType.getFarba().getRGB() +";";
            uloz += cellType.getIndex()+"\r\n";
        }
        uloz += "RULES;" + this.getRulesCount() + "\r\n";
        for (Pravidlo rule : this.rules) {
            uloz += "RULE;";
            uloz += rule.getFromIndex() + ";";
            uloz += rule.getToIndex() + ";";
            uloz += rule.getPocet() + ";";
            uloz += rule.getIndexSusednej() + ";";
            uloz += rule.getPravdepodobnost() + ";\r\n";
        }
        uloz += "indexDefaultCellType;" + this.indexVychodzejBunky + "\r\n";   //toto
        if (this.mooroveOkolie) {
            uloz += "mooreNeighborhood;1" + "\r\n"; //toto
        } else {
            uloz += "mooreNeighborhood;0" + "\r\n"; //toto
        }
        uloz += "GENERATION;" + generacia + "\r\n"; //toto
        uloz += "CURRENTMOVE;\r\n";
        for (int i = 0; i < currentMove.length; i++) {
            uloz += "ROW;";
            for (int j = 0; j < currentMove[i].length; j++) {
                uloz += currentMove[i][j] + ";";
            }
            uloz += "\r\n";
        }
        
        String pripona = ".txt";
        String userDir = System.getProperty("user.home");
        JFileChooser chooser = new JFileChooser(new File(userDir + "/Desktop"));
        chooser.setDialogTitle("Uložiť do súboru");
        chooser.setFileFilter(new FileTypeFilter(pripona, "Textový súbor"));
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            //ulozenie do suboru
            String content = uloz;
            File fi = chooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(fi.getPath() + pripona); //prida priponu do nazvu suboru
                fw.write(content);
                fw.flush();
                fw.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public StavBunky updateCellType(String noveMeno, Color newColor, int indexEditovanejBunky) {
        for (StavBunky cellType : cellTypes) {
            if (cellType.getIndex() == indexEditovanejBunky) {
                cellType.setNazov(noveMeno);
                cellType.setFarba(newColor);
                return cellType;
            }
        }
        return null;
    }
}

class FileTypeFilter extends FileFilter{
    private final String extension;
    private final String description;

    public FileTypeFilter(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }
    
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()){
            return true;            
        }
        return file.getName().endsWith(extension);
    }

    @Override
    public String getDescription() {
        return description + " " + extension;
    }    
}