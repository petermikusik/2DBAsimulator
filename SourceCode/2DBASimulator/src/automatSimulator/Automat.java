/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatSimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Peter Mikušík
 */
public class Automat extends javax.swing.JFrame implements ActionListener{
    private int riadky, stlpce;
    private int[][] currentMove;
    private int[][] nextMove;
    private boolean play = false;
    private Graphics graphics;
    private Image grid;
    private Timer casovac;
    private AutomatVlastnosti vlastnosti;
    private HashMap<Integer, Color> farbyBunky;
    private int zvolenyTypeCell = -1;
    private int generacia = 0;
    private Random random = new Random();
    private String[] options = {"Áno", "Nie"}; //volby v showOptionDialog-u
    /**
     * Creates new form Automat
     * @param pVlastnosti
     */
    public Automat(AutomatVlastnosti pVlastnosti) {
        initComponents();
        this.vlastnosti = pVlastnosti;        
        this.casovac = new Timer(200, this);
        nastavButtony(false);
        this.jProgressBar1.setVisible(false);
    }
    
    public void poNastaveni(boolean zmena){
        this.farbyBunky = vlastnosti.getFarbyBuniek();
        this.naplnComboBox(); 
        if(!this.vlastnosti.isIsNastaveny() || zmena){
            this.stlpce = this.vlastnosti.getStlpce();
            this.riadky = this.vlastnosti.getRiadky();
            this.currentMove = new int[riadky][stlpce];
            this.nextMove = new int[riadky][stlpce];
            this.grid = createImage(Panel.getWidth(), Panel.getHeight());
            this.graphics = grid.getGraphics();
            if(this.vlastnosti.getIndexVychodzejBunky() > -1){
                this.nastavVychodziuBunku();  
                this.vlastnosti.setIsNastaveny(true);
                nastavButtony(true);                   
            } else {
                this.vlastnosti.setIsNastaveny(false);
                nastavButtony(false);
            }
        } 
        Panel.setBounds(Panel.getX(), Panel.getY(), Panel.getWidth()+1, Panel.getHeight());
    }
    
    private void nastavButtony(boolean ako){
        this.playPauseBtn.setEnabled(ako);
        this.resetBtn.setEnabled(ako);    
        this.nextGenBtn.setEnabled(ako);
        this.preskocGenSpinner.setEnabled(ako);
        this.preskocBtn.setEnabled(ako);
        this.cellTypeComboBox.setEnabled(ako);
    }
    
    public void prekresliPanel(){
        if(this.vlastnosti.isIsNastaveny()){
            this.grid = createImage(Panel.getWidth(), Panel.getHeight());
            this.graphics = this.grid.getGraphics();
            this.vykresliGrid();   
        }  
    }
    
    private void vykresliGrid() {
        graphics.setColor(Panel.getBackground());
        graphics.fillRect(0, 0, Panel.getWidth(), Panel.getHeight());
        if(this.vlastnosti.getIndexVychodzejBunky() != -1){
            for (int r = 0; r < this.riadky; r++) {
                for (int s = 0; s < this.stlpce; s++) {                
                    graphics.setColor(farbyBunky.get(currentMove[r][s]));
                    int x = s * Panel.getWidth() / this.stlpce;
                    int y = r * Panel.getHeight() / this.riadky;                    
                    graphics.fillRect(x, y, Panel.getWidth()/this.stlpce + 1, Panel.getHeight()/this.riadky + 1);
                }
            }
        }
        graphics.setColor(Color.BLACK);
        for (int i = 1; i < this.riadky; i++) {
            int y = i * Panel.getHeight() / this.riadky;
            graphics.drawLine(0,y,Panel.getWidth(),y);
        }
        for (int i = 1; i < this.stlpce; i++) {
            int x = i * Panel.getWidth() / this.stlpce;
            graphics.drawLine(x,0,x,Panel.getHeight());
        }      
        Panel.getGraphics().drawImage(grid, 0, 0, Panel);
    }
    
    private void nastavVychodziuBunku() {
        for (int i = 0; i < riadky; i++) {
            for (int j = 0; j < stlpce; j++) {
                currentMove[i][j] = vlastnosti.getIndexVychodzejBunky();
            }
        }                   
        vykresliGrid();
        this.generacia = 0;
        this.generaciaLabel.setText(this.generacia + "");
    }
    
    private int pocitajNovuHodnotuBunky(int r, int s){
        Pravidlo pravidlo;
        int novaHodnotaBunky = -1;
        if(this.vlastnosti.getRulesCount() > 0){
            for (int i = 0; i < this.vlastnosti.getRulesCount(); i++) {
                pravidlo = this.vlastnosti.getRule(i);
                if(pravidlo.getFromIndex() == currentMove[r][s]){
                    if(pravidlo.getPocet() == this.getPocetSusedov(pravidlo.getIndexSusednej(), r, s)){
                        int rn = this.random.nextInt(101);
                        if(pravidlo.getPravdepodobnost() >= rn){
                            novaHodnotaBunky = pravidlo.getToIndex();
                        }
                    }
                }
            }
        } 
        return novaHodnotaBunky;        
    }
    
    
    //r = riadok a s = stlpec bunky, ktorej hladam pocet susedov, indexBunky je bunka, ktorej pocet v susedstve ma zaujima
    private int getPocetSusedov(int indexBunky, int r, int s){
        if(vlastnosti.isMooroveOkolie()){
            return this.getPocetSusedovMoore(indexBunky, r, s);
        } else {
            return this.getPocetSusedovVonNeumann(indexBunky, r, s);
        }
    }
    
    //r = riadok a s = stlpec bunky, ktorej hladam pocet susedov, indexBunky je bunka, ktorej pocet v susedstve ma zaujima
    private int getPocetSusedovMoore(int indexBunky, int r, int s){
        int pocetSusedov = this.getPocetSusedovVonNeumann(indexBunky, r, s);
        
        //lava horna
        if(r > 0 && s > 0 && currentMove[r-1][s-1] == indexBunky){ //cely grid okrem laveho stlpca alebo horneho riadku
            pocetSusedov++;
        }else if(r == 0 && s == 0 && currentMove[this.riadky - 1][this.stlpce - 1] == indexBunky){ //lavy horny okraj gridu
            pocetSusedov++;
        }else if(r > 0 && s == 0 && currentMove[r - 1][this.stlpce - 1] == indexBunky){ //lavy stlpec gridu
            pocetSusedov++;
        }else if(r == 0 && s > 0 && currentMove[this.riadky - 1][s - 1] == indexBunky){ //prvy riadok
            pocetSusedov++;
        }
        
        //prava horna
        if(r > 0 && s + 1 < this.stlpce && currentMove[r - 1][s + 1] == indexBunky){ //cely grid okrem praveho stlpca alebo horneho riadku
            pocetSusedov++;
        }else if(r == 0 && s == this.stlpce - 1 && currentMove[this.riadky - 1][0] == indexBunky){ // pravy horny okraj gridu
            pocetSusedov++;
        }else if(r > 0 && s == this.stlpce - 1 && currentMove[r - 1][0] == indexBunky){ // pravy stlpec gridu
            pocetSusedov++;
        }else if(r == 0 && s < this.stlpce - 1 && currentMove[this.riadky - 1][s + 1] == indexBunky){ // prvy riadok 
            pocetSusedov++;
        }
        
        //lava spodna
        if (r + 1 < this.riadky && s > 0 && currentMove[r + 1][s - 1] == indexBunky) { //cely grid okrem laveho stlpca alebo spodneho riadka
            pocetSusedov++;
        }else if(r == this.riadky - 1 && s == 0 && currentMove[0][this.stlpce - 1] == indexBunky){// lavy dolny okraj
            pocetSusedov++;
        }else if(r + 1 < this.riadky && s == 0 && currentMove[r + 1][this.stlpce - 1] == indexBunky){// lavy stlpec
            pocetSusedov++;
        }else if(r == this.riadky - 1 && s > 0 && currentMove[0][s - 1] == indexBunky){// spodny riadok
            pocetSusedov++;
        }
        
        //prava spodna
        if(r + 1 < this.riadky && s + 1 < this.stlpce && currentMove[r + 1][s + 1] == indexBunky){ //cely grid okrem praveho stlpca alebo spodneho riadka
            pocetSusedov++;
        }else if(r == this.riadky - 1 && s == this.stlpce - 1 && currentMove[0][0] == indexBunky){ // pravy dolny okraj
            pocetSusedov++;
        }else if(r == this.riadky - 1 && s < this.stlpce - 1 && currentMove[0][s + 1] == indexBunky){ //spodny riadok
            pocetSusedov++;
        }else if(r < this.riadky - 1 && s == this.stlpce - 1 && currentMove[r + 1][0] == indexBunky){ // pravy stlpec
            pocetSusedov++;
        }
        
        return pocetSusedov;
    }
    
    //r = riadok a s = stlpec bunky, ktorej hladam pocet susedov, indexBunky je bunka, ktorej pocet v susedstve ma zaujima
    private int getPocetSusedovVonNeumann(int indexBunky, int r, int s){
        int pocetSusedov = 0;
        //Horna
        if((r > 0 && currentMove[r - 1][s] == indexBunky) || (r == 0 && currentMove[this.riadky - 1][s] == indexBunky)){
            pocetSusedov++;
        }
        //lava
        if((s > 0 && currentMove[r][s-1] == indexBunky) || (s == 0 && currentMove[r][this.stlpce - 1] == indexBunky)){
            pocetSusedov++;
        }
        //prava
        if((s + 1 < this.stlpce && currentMove[r][s + 1] == indexBunky) || (s + 1 == this.stlpce && currentMove[r][0] == indexBunky)){
            pocetSusedov++;
        }
        //spodna
        if((r + 1 < this.riadky && currentMove[r + 1][s] == indexBunky) || (r + 1 == this.riadky && currentMove[0][s] == indexBunky)){
            pocetSusedov++;
        }
        return pocetSusedov;
    }
    
    private void naplnComboBox(){
        this.cellTypeComboBox.removeAllItems();
        int pocetTypov = this.vlastnosti.getCellTypeCount();
        if(pocetTypov > 0){
            for (int i = 0; i < pocetTypov; i++) {
                this.cellTypeComboBox.addItem(this.vlastnosti.getCellType(i).getNazov());
            }
        }
    }
        
    private Thread vratVlakno(int spodnyLimit, int hornyLimit, CountDownLatch latch){
        Thread t = new Thread(){
            @Override
            public void run(){
                int novaHodnota;
                for (int i = spodnyLimit; i < hornyLimit; i++) {
                    for (int j = 0; j < stlpce; j++) {
                        novaHodnota = pocitajNovuHodnotuBunky(i, j);
                        if(novaHodnota > -1){
                            nextMove[i][j] = novaHodnota;
                        } else {
                            nextMove[i][j] = currentMove[i][j];
                        }
                    }
                }
                latch.countDown();
            }
        };
        return t;
    }
    
    private void pocitajDalsiuGeneraciu(){
        int hranica1 = -1, hranica2 = -1, hranica3 = -1, hranica4 = -1;
        if(this.riadky % 4 == 0){
            hranica1 = this.riadky / 4;
            hranica2 = 2 * hranica1;
            hranica3 = 3 * hranica1;
            hranica4 = 4 * hranica1;
        } else if(this.riadky % 4 == 1){
            hranica1 = (this.riadky - 1) / 4;
            hranica2 = 2 * hranica1;
            hranica3 = 3 * hranica1;
            hranica4 = 4 * hranica1 + 1;
        } else if(this.riadky % 4 == 2){
            hranica1 = (this.riadky - 2) / 4;
            hranica2 = 2 * hranica1;
            hranica3 = 3 * hranica1;
            hranica4 = 4 * hranica1 + 2;
        } else if(this.riadky % 4 == 3){
            hranica1 = (this.riadky - 3) / 4;
            hranica2 = 2 * hranica1;
            hranica3 = 3 * hranica1;
            hranica4 = 4 * hranica1 + 3;
        }
                
        CountDownLatch latch = new CountDownLatch(4);        
        Thread t1 = this.vratVlakno(0, hranica1, latch);
        Thread t2 = this.vratVlakno(hranica1, hranica2, latch);
        Thread t3 = this.vratVlakno(hranica2, hranica3, latch);
        Thread t4 = this.vratVlakno(hranica3, hranica4, latch);        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Automat.class.getName()).log(Level.SEVERE, null, ex);
        }        
        for (int i = 0; i < this.riadky; i++) {
            for (int j = 0; j < this.stlpce; j++) {
                this.currentMove[i][j] = this.nextMove[i][j];
            }
        }
    }
    
    private void dalsiaGeneracia(){
        this.pocitajDalsiuGeneraciu();
        this.vykresliGrid();
        this.generacia++;
        this.generaciaLabel.setText(this.generacia + "");
    }
    
    public void setGeneracia(int gen){
        this.generacia = gen;
    }
    
    public void setCurrentMove(int[][] curr){
        this.currentMove = curr;
    }
    
    private void pauseCasovac(){
        if(this.play){
            this.playPauseBtn.setText("Spusť");
            casovac.stop();
            this.play = false;
            this.nextGenBtn.setEnabled(true);
            this.resetBtn.setEnabled(true);
        }
    }
    
    public HashMap<Integer, Integer> pocitajBunky(){
        HashMap<Integer, Integer> bunky = new HashMap<Integer, Integer>();
        if(!this.vlastnosti.isIsNastaveny()){
            return bunky;
        }
        for (int r = 0; r < this.riadky; r++) {
            for (int s = 0; s < this.stlpce; s++) {
                int b = this.currentMove[r][s];
                if(bunky.containsKey(b)){
                    int count = bunky.get(b);
                    bunky.put(b, ++count);
                } else {
                    bunky.put(b, 1);;
                }
            }
        }        
        return bunky;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playPauseBtn = new javax.swing.JButton();
        resetBtn = new javax.swing.JButton();
        nextGenBtn = new javax.swing.JButton();
        cellTypeComboBox = new javax.swing.JComboBox<>();
        Panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        generaciaLabel = new javax.swing.JLabel();
        rychlostSlider = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        preskocGenSpinner = new javax.swing.JSpinner();
        preskocBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        suborMenu = new javax.swing.JMenu();
        nacitajMenuItem = new javax.swing.JMenuItem();
        ulozitMenuItem = new javax.swing.JMenuItem();
        ulozitObrazokMenuItem = new javax.swing.JMenuItem();
        moznostiMenu = new javax.swing.JMenu();
        nastaveniaMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("2D BA simulátor");
        setMinimumSize(new java.awt.Dimension(1300, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        playPauseBtn.setText("Spusť");
        playPauseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPauseBtnActionPerformed(evt);
            }
        });

        resetBtn.setText("Reset");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });

        nextGenBtn.setText("Ďalšia generácia");
        nextGenBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextGenBtnActionPerformed(evt);
            }
        });

        cellTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cellTypeComboBoxActionPerformed(evt);
            }
        });

        Panel.setBackground(new java.awt.Color(102, 102, 102));
        Panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                PanelMouseDragged(evt);
            }
        });
        Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PanelMouseClicked(evt);
            }
        });
        Panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                PanelComponentResized(evt);
            }
        });

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1136, Short.MAX_VALUE)
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel1.setText("Generácia:");

        generaciaLabel.setText("0");

        rychlostSlider.setMaximum(1000);
        rychlostSlider.setMinimum(50);
        rychlostSlider.setMinorTickSpacing(50);
        rychlostSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        rychlostSlider.setPaintLabels(true);
        rychlostSlider.setPaintTicks(true);
        rychlostSlider.setToolTipText("Rýchlosť");
        rychlostSlider.setValue(200);
        rychlostSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rychlostSliderStateChanged(evt);
            }
        });

        jLabel2.setText("Rýchlosť:");

        jLabel3.setText("generácií");

        preskocGenSpinner.setModel(new javax.swing.SpinnerNumberModel(5, 5, 1000, 1));

        preskocBtn.setText("Preskočiť");
        preskocBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preskocBtnActionPerformed(evt);
            }
        });

        jLabel4.setText("Preskočiť");

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        jLabel5.setText("1s");

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("50ms");

        suborMenu.setText("Súbor");

        nacitajMenuItem.setText("Načítať zo súboru");
        nacitajMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nacitajMenuItemActionPerformed(evt);
            }
        });
        suborMenu.add(nacitajMenuItem);

        ulozitMenuItem.setText("Uložiť do súboru");
        ulozitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ulozitMenuItemActionPerformed(evt);
            }
        });
        suborMenu.add(ulozitMenuItem);

        ulozitObrazokMenuItem.setText("Uložiť ako obrázok");
        ulozitObrazokMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ulozitObrazokMenuItemActionPerformed(evt);
            }
        });
        suborMenu.add(ulozitObrazokMenuItem);

        jMenuBar1.add(suborMenu);

        moznostiMenu.setText("Možnosti");

        nastaveniaMenuItem.setText("Nastavenia");
        nastaveniaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nastaveniaMenuItemActionPerformed(evt);
            }
        });
        moznostiMenu.add(nastaveniaMenuItem);

        jMenuBar1.add(moznostiMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(playPauseBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(resetBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(nextGenBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(preskocBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator2)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(generaciaLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(preskocGenSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3))
                            .addComponent(jLabel4)
                            .addComponent(cellTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rychlostSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playPauseBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resetBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextGenBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(preskocGenSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(preskocBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(cellTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(generaciaLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rychlostSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(0, 78, Short.MAX_VALUE)))
                .addContainerGap())
        );

        preskocGenSpinner.getAccessibleContext().setAccessibleDescription("Počet generácií");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playPauseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPauseBtnActionPerformed
        this.play = !this.play;
        if(this.play){
            this.playPauseBtn.setText("Pauza");
            casovac.start();
            this.nextGenBtn.setEnabled(false); 
            this.resetBtn.setEnabled(false);
            this.preskocBtn.setEnabled(false);
        } else {
            this.playPauseBtn.setText("Spusť");
            casovac.stop();
            this.nextGenBtn.setEnabled(true); 
            this.resetBtn.setEnabled(true);
            this.preskocBtn.setEnabled(true);
        }
    }//GEN-LAST:event_playPauseBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        this.nastavVychodziuBunku();
        this.vykresliGrid();     
        this.pauseCasovac();
    }//GEN-LAST:event_resetBtnActionPerformed

    private void PanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_PanelComponentResized
        if(this.vlastnosti.isIsNastaveny()){
            this.grid = createImage(Panel.getWidth(), Panel.getHeight());
            this.graphics = this.grid.getGraphics();
            this.vykresliGrid();   
        }        
    }//GEN-LAST:event_PanelComponentResized

    
    private void cellTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellTypeComboBoxActionPerformed
        this.zvolenyTypeCell = this.vlastnosti.getCellIndex((String)this.cellTypeComboBox.getSelectedItem());
    }//GEN-LAST:event_cellTypeComboBoxActionPerformed

    private void PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelMouseClicked
        int r = this.riadky * evt.getY() / Panel.getHeight();
        int s = this.stlpce * evt.getX() / Panel.getWidth();
        this.currentMove[r][s] = this.zvolenyTypeCell;
        this.vykresliGrid();                
    }//GEN-LAST:event_PanelMouseClicked

    private void PanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelMouseDragged
        try {
            int r = this.riadky * evt.getY() / Panel.getHeight();
            int s = this.stlpce * evt.getX() / Panel.getWidth();
            if(SwingUtilities.isLeftMouseButton(evt)){
                this.currentMove[r][s] = this.zvolenyTypeCell;
                this.vykresliGrid();
            }
        } catch (Exception e) {
            //System.out.println("Kresli vo vnutri jPanelu");
        }
    }//GEN-LAST:event_PanelMouseDragged

    //po kliknuti v menu na moznosti -> nastavenia
    private void nastaveniaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nastaveniaMenuItemActionPerformed
        Panel.setBounds(Panel.getX(), Panel.getY(), Panel.getWidth()-1, Panel.getHeight()); //zmensim o 1 a po nastaveni zvacsim o 1 aby sa prekreslil jPanel, inak sa mu nechce
        this.pauseCasovac();
        this.setVisible(false);
        NastaveniaFrame nastavenia = new NastaveniaFrame(vlastnosti, this);
        nastavenia.setVisible(true);
    }//GEN-LAST:event_nastaveniaMenuItemActionPerformed

    private void nextGenBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextGenBtnActionPerformed
        if(!this.play){
            this.dalsiaGeneracia();
        }        
    }//GEN-LAST:event_nextGenBtnActionPerformed

    private void rychlostSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rychlostSliderStateChanged
        int hodnota = this.rychlostSlider.getValue();
        this.casovac.setDelay(hodnota);
    }//GEN-LAST:event_rychlostSliderStateChanged

    private void nacitajMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nacitajMenuItemActionPerformed
        this.pauseCasovac();
        int volba = JOptionPane.YES_OPTION;
        if (this.vlastnosti.isIsNastaveny()) {
            volba = JOptionPane.showOptionDialog(null, "Po načítaní zo súboru sa zmaže aktuálna konfigurácia.\nNaozaj chcete pokračovať?", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, this.options, null);
        }        
        if(volba == JOptionPane.YES_OPTION){            
            boolean nacitaloSa = this.vlastnosti.nacitajZoSuboru(this);
            if (nacitaloSa) {
                this.stlpce = this.vlastnosti.getStlpce();
                this.riadky = this.vlastnosti.getRiadky();
                this.nextMove = new int[riadky][stlpce];
                this.farbyBunky = vlastnosti.getFarbyBuniek();
                this.grid = createImage(Panel.getWidth(), Panel.getHeight());
                this.graphics = grid.getGraphics();
                this.naplnComboBox();                           
                if(this.vlastnosti.getIndexVychodzejBunky() > -1){
                    this.vlastnosti.setIsNastaveny(true);
                    this.nastavButtony(true);
                }
                this.prekresliPanel();                
                this.generaciaLabel.setText(this.generacia + "");
            } else {
                JOptionPane.showMessageDialog(null, "Nastala neočakávaná chyba", "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        }    
    }//GEN-LAST:event_nacitajMenuItemActionPerformed

    private void ulozitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ulozitMenuItemActionPerformed
        this.pauseCasovac();
        if (this.vlastnosti.isIsNastaveny()) {
            this.vlastnosti.ulozDoSuboru(this.currentMove, this.generacia);
        } else {
            JOptionPane.showMessageDialog(null, "Automat nie je nastavený! Nedá sa uložiť", "Varovanie", JOptionPane.WARNING_MESSAGE);
        } 
        this.prekresliPanel();
    }//GEN-LAST:event_ulozitMenuItemActionPerformed

    private void ulozitObrazokMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ulozitObrazokMenuItemActionPerformed
        this.pauseCasovac();
        if (this.vlastnosti.isIsNastaveny()) {
            int w = this.grid.getWidth(this);
            int h = this.grid.getHeight(this);
            String userDir = System.getProperty("user.home");
            JFileChooser chooser = new JFileChooser(new File(userDir + "/Desktop"));
            chooser.setDialogTitle("Uložiť ako obrázok");
            int result = chooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR); 
                Graphics g = bi.getGraphics();
                try {
                    File f = new File(chooser.getSelectedFile().getPath()+".jpg");
                    g.drawImage(this.grid, 0, 0, null);
                    ImageIO.write(bi, "jpg", f);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } 
        } else {
            JOptionPane.showMessageDialog(null, "Automat nie je nastavený! Nedá sa uložiť", "Varovanie", JOptionPane.WARNING_MESSAGE);
        }        
    }//GEN-LAST:event_ulozitObrazokMenuItemActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        this.prekresliPanel();
    }//GEN-LAST:event_formWindowActivated
    
    private void preskocBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preskocBtnActionPerformed
        int pocet = Integer.parseInt(this.preskocGenSpinner.getValue().toString());
        this.jProgressBar1.setVisible(true);
        this.jProgressBar1.setMaximum(pocet);
        this.jProgressBar1.setValue(0);
        this.nastavButtony(false);
        this.suborMenu.setEnabled(false);
        this.moznostiMenu.setEnabled(false);
        //long c1 = System.currentTimeMillis();
        Thread t = new Thread(){
                public void run(){
                    for (int i = 0; i < pocet; i++) {
                        pocitajDalsiuGeneraciu();
                        jProgressBar1.setValue(i);           
                    }
                    vykresliGrid();
                    generacia += pocet;
                    generaciaLabel.setText(generacia + "");   
                    
                    nastavButtony(true);
                    suborMenu.setEnabled(true);
                    moznostiMenu.setEnabled(true);
                    jProgressBar1.setVisible(false);
                    //long c2 = System.currentTimeMillis();
                    //System.out.println(c2-c1);
                }              
            };
        t.start();
    }//GEN-LAST:event_preskocBtnActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JComboBox<String> cellTypeComboBox;
    private javax.swing.JLabel generaciaLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenu moznostiMenu;
    private javax.swing.JMenuItem nacitajMenuItem;
    private javax.swing.JMenuItem nastaveniaMenuItem;
    private javax.swing.JButton nextGenBtn;
    private javax.swing.JButton playPauseBtn;
    private javax.swing.JButton preskocBtn;
    private javax.swing.JSpinner preskocGenSpinner;
    private javax.swing.JButton resetBtn;
    private javax.swing.JSlider rychlostSlider;
    private javax.swing.JMenu suborMenu;
    private javax.swing.JMenuItem ulozitMenuItem;
    private javax.swing.JMenuItem ulozitObrazokMenuItem;
    // End of variables declaration//GEN-END:variables

    
    //timer pri kazdom kroku
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (this.play) {
            this.dalsiaGeneracia();
        }
    }
}
