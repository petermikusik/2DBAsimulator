/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automatSimulator;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Peter Mikušík
 */
public class NastaveniaFrame extends javax.swing.JFrame {
    private AutomatVlastnosti vlastnosti;
    private Automat automat;
    private DefaultTableModel bunkyModel;
    private DefaultTableModel pravidlaModel;
    private String[] hlavickaBunky = {"Názov", "Farba", "Počet v aktuálnej generácii", "% v aktuálnej generácii"};
    private String[] hlavickaPravidla = {"Zo stavu", "Na stav", "Pri počte", "Susedných buniek v stave", "Pravdepodobnosť"}; 
    private int indexEditovanejBunky = -1;
    private int indexEditovanejBunkyVTabulke = -1;
    private boolean editujemBunku = false;
    private int indexEditPravidla = -1;
    private int indexEditPravidlaVTabulke = -1;
    private boolean editujemPravidlo = false;
    private HashMap<Integer, Integer> pocetDanychBuniek;
    private boolean zmena = false;
    private boolean rozmeryNastavene = false;  //po nastaveni spinerov na rozmery sa prehodi na true v kon3truktore.. nasledne budu vyskakovat pri pohybe spinerov oznamenia
    private String[] options = {"Áno", "Nie"}; //volby v showOptionDialog-u
    
    /**
     * Creates new form nastaveniaFrame
     * @param pVlastnosti
     * @param pAutomat
     */
    public NastaveniaFrame(AutomatVlastnosti pVlastnosti, Automat pAutomat) {
        initComponents();
        this.automat = pAutomat;
        this.vlastnosti = pVlastnosti;
        this.okolieRadioGroup.add(this.vonNeumanRadio);
        this.okolieRadioGroup.add(this.mooreRadio);
        
        this.farbaButton.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e){
                Color c = JColorChooser.showDialog(null, "Zvoľ farbu stavu", Color.WHITE);
                farbaButton.setBackground(c);
            }
        });
        
        this.pocetDanychBuniek = this.automat.pocitajBunky();
        
        this.bunkyModel = new DefaultTableModel(null, this.hlavickaBunky);
        this.pravidlaModel = new DefaultTableModel(null, this.hlavickaPravidla);
        this.bunkyTable.setModel(bunkyModel);        
        
        this.bunkyTable.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnCellRenderer());
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        this.bunkyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        this.bunkyTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        this.bunkyTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        if(this.vlastnosti.getCellTypeCount() == 0){
            this.pridajPravidloBtn.setEnabled(false);
            this.nastavVychodziuBtn.setEnabled(false);
            this.zmazBunkuBtn.setEnabled(false);
            this.editBunkaBtn.setEnabled(false);
        }       
        
        this.pravidlaTable.setModel(pravidlaModel);
        this.pravidlaTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        this.pravidlaTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        this.pravidlaTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        this.pravidlaTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        this.pravidlaTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        this.naplnTableModely();   
        
        if (this.vlastnosti.isMooroveOkolie()) {
            this.okolieRadioGroup.setSelected(this.mooreRadio.getModel(), true);
            SpinnerModel model1 = new SpinnerNumberModel(0, 0, 8, 1);
            SpinnerModel model2 = new SpinnerNumberModel(0, 0, 8, 1);
            this.numberMINSpiner.setModel(model1);
            this.numberMAXspiner.setModel(model2);
        } else {
            this.okolieRadioGroup.setSelected(this.vonNeumanRadio.getModel(), true);
            SpinnerModel model1 = new SpinnerNumberModel(0, 0, 4, 1);
            SpinnerModel model2 = new SpinnerNumberModel(0, 0, 4, 1);
            this.numberMINSpiner.setModel(model1);
            this.numberMAXspiner.setModel(model2);
        }
        
        if(this.vlastnosti.getIndexVychodzejBunky() > -1){
            this.vychodziaBunkaLabel.setText(this.vlastnosti.getCellName(this.vlastnosti.getIndexVychodzejBunky()));
        }
        
        this.riadkySpiner.setValue(this.vlastnosti.getRiadky());
        this.stlpceSpiner.setValue(this.vlastnosti.getStlpce());
        this.zmena = false; // setValue na spineroch zmeni stav, preto treba zmenu dat na false
        this.rozmeryNastavene = true;
        this.naplnComboBoxy();        
    }
            
    private void naplnTableModely(){
        this.naplnTableModelBunky();
        this.naplnTableModelPravidla();
    }
    
    private void naplnTableModelBunky(){
        String nazov;
        Color farba;
        StavBunky bunka;
        int pocet;
        int percent;        
        int pocetBuniek = this.vlastnosti.getCellTypeCount();
        if(pocetBuniek > 0){
            for (int i = 0; i < pocetBuniek; i++) {
                bunka = this.vlastnosti.getCellType(i);
                nazov = bunka.getNazov();
                farba = bunka.getFarba();
                if(pocetDanychBuniek.get(bunka.getIndex()) != null){
                    pocet = pocetDanychBuniek.get(bunka.getIndex());
                    double pocetVsetkychBuniek = this.vlastnosti.getRiadky()*this.vlastnosti.getStlpce();
                    percent = (int)Math.round(pocet/pocetVsetkychBuniek * 100);                  
                } else {
                    pocet = 0;
                    percent = 0;
                }                
                Object[] riadok = {nazov, farba.getRGB(), pocet, percent};
                bunkyModel.addRow(riadok);     
            }
        }
    }
    
    private void naplnTableModelPravidla(){        
        Pravidlo pravidlo;
        int from, to, pocet, sused, pravdepodobnost;        
        int pocetPravidiel = this.vlastnosti.getRulesCount();
        if(pocetPravidiel > 0){
            for (int i = 0; i < pocetPravidiel; i++) {
                pravidlo = this.vlastnosti.getRule(i);
                from = pravidlo.getFromIndex();
                to = pravidlo.getToIndex();
                pocet = pravidlo.getPocet();
                sused = pravidlo.getIndexSusednej();
                pravdepodobnost = pravidlo.getPravdepodobnost();
                Object[] riadok = {this.vlastnosti.getCellName(from), this.vlastnosti.getCellName(to), pocet, this.vlastnosti.getCellName(sused), pravdepodobnost};
                pravidlaModel.addRow(riadok);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okolieRadioGroup = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        typyBuniekPanel = new javax.swing.JPanel();
        nazovBunkyField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        farbaButton = new javax.swing.JButton();
        pridajBunkuButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        bunkyTable = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        vychodziaBunkaLabel = new javax.swing.JLabel();
        nastavVychodziuBtn = new javax.swing.JButton();
        zmazBunkuBtn = new javax.swing.JButton();
        editBunkaBtn = new javax.swing.JButton();
        pravidlaPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        fromCombo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        toCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        numberMINSpiner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        neighborCombo = new javax.swing.JComboBox<>();
        pridajPravidloBtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        pravdepodobnostSpiner = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        numberMAXspiner = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        pravidlaTable = new javax.swing.JTable();
        zmazPravidloBtn = new javax.swing.JButton();
        editPravidloBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        vseobecnePanel = new javax.swing.JPanel();
        vonNeumanRadio = new javax.swing.JRadioButton();
        mooreRadio = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        riadkySpiner = new javax.swing.JSpinner();
        stlpceSpiner = new javax.swing.JSpinner();
        ulozitZavrietBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nastavenia");
        setMinimumSize(new java.awt.Dimension(1300, 700));
        setResizable(false);

        nazovBunkyField.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        jLabel2.setText("Názov:");

        farbaButton.setText("Farba");

        pridajBunkuButton.setText("Pridaj stav");
        pridajBunkuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pridajBunkuButtonActionPerformed(evt);
            }
        });

        bunkyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Názov", "Farba", "Počet v aktuálnej generácii", "% v aktuálnej generácii"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(bunkyTable);

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel11.setText("Východiskový stav:");

        vychodziaBunkaLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        vychodziaBunkaLabel.setText("nedefinovaný");

        nastavVychodziuBtn.setText("Nastav ako východiskový");
        nastavVychodziuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nastavVychodziuBtnActionPerformed(evt);
            }
        });

        zmazBunkuBtn.setText("Zmaž stav");
        zmazBunkuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zmazBunkuBtnActionPerformed(evt);
            }
        });

        editBunkaBtn.setText("Uprav stav");
        editBunkaBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBunkaBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout typyBuniekPanelLayout = new javax.swing.GroupLayout(typyBuniekPanel);
        typyBuniekPanel.setLayout(typyBuniekPanelLayout);
        typyBuniekPanelLayout.setHorizontalGroup(
            typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typyBuniekPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(typyBuniekPanelLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vychodziaBunkaLabel))
                    .addGroup(typyBuniekPanelLayout.createSequentialGroup()
                        .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(typyBuniekPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(nazovBunkyField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(farbaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pridajBunkuButton))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nastavVychodziuBtn)
                            .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(editBunkaBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(zmazBunkuBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(411, Short.MAX_VALUE))
        );
        typyBuniekPanelLayout.setVerticalGroup(
            typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typyBuniekPanelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(vychodziaBunkaLabel))
                .addGap(18, 18, 18)
                .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nazovBunkyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(farbaButton)
                    .addComponent(pridajBunkuButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(typyBuniekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                    .addGroup(typyBuniekPanelLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(nastavVychodziuBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zmazBunkuBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editBunkaBtn)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Stavy buniek", typyBuniekPanel);

        jLabel3.setText("Zo:");

        jLabel4.setText("Na:");

        jLabel5.setText("Pri počte");

        numberMINSpiner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 8, 1));

        jLabel6.setText("Susedov:");

        pridajPravidloBtn.setText("Pridaj pravidlo");
        pridajPravidloBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pridajPravidloBtnActionPerformed(evt);
            }
        });

        jLabel7.setText("Pravdepodobnosť:");

        pravdepodobnostSpiner.setModel(new javax.swing.SpinnerNumberModel(100, 1, 100, 1));

        jLabel12.setText("minimálne");

        jLabel13.setText("maximálne:");

        numberMAXspiner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 8, 1));

        pravidlaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(pravidlaTable);

        zmazPravidloBtn.setText("Zmaž pravidlo");
        zmazPravidloBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zmazPravidloBtnActionPerformed(evt);
            }
        });

        editPravidloBtn.setText("Uprav pravidlo");
        editPravidloBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPravidloBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("%");

        javax.swing.GroupLayout pravidlaPanelLayout = new javax.swing.GroupLayout(pravidlaPanel);
        pravidlaPanel.setLayout(pravidlaPanelLayout);
        pravidlaPanelLayout.setHorizontalGroup(
            pravidlaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pravidlaPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pravidlaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pravidlaPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fromCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberMINSpiner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberMAXspiner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel6))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pravidlaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(zmazPravidloBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(neighborCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 130, Short.MAX_VALUE)
                    .addComponent(editPravidloBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pravdepodobnostSpiner, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(pridajPravidloBtn)
                .addContainerGap(66, Short.MAX_VALUE))
        );
        pravidlaPanelLayout.setVerticalGroup(
            pravidlaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pravidlaPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pravidlaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(fromCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(toCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(numberMINSpiner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(neighborCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pridajPravidloBtn)
                    .addComponent(jLabel7)
                    .addComponent(pravdepodobnostSpiner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(numberMAXspiner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(pravidlaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                    .addGroup(pravidlaPanelLayout.createSequentialGroup()
                        .addComponent(zmazPravidloBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editPravidloBtn)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Prechodové pravidlá", pravidlaPanel);

        vonNeumanRadio.setText("Von Neumannovo okolie");
        vonNeumanRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vonNeumanRadioActionPerformed(evt);
            }
        });

        mooreRadio.setText("Moorovo okolie");
        mooreRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mooreRadioActionPerformed(evt);
            }
        });

        jLabel8.setText("Okolie:");

        jLabel9.setText("Počet riadkov");

        jLabel10.setText("Počet stĺpcov");

        riadkySpiner.setModel(new javax.swing.SpinnerNumberModel(50, 10, 300, 1));
        riadkySpiner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                riadkySpinerStateChanged(evt);
            }
        });

        stlpceSpiner.setModel(new javax.swing.SpinnerNumberModel(100, 10, 300, 1));
        stlpceSpiner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                stlpceSpinerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout vseobecnePanelLayout = new javax.swing.GroupLayout(vseobecnePanel);
        vseobecnePanel.setLayout(vseobecnePanelLayout);
        vseobecnePanelLayout.setHorizontalGroup(
            vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vseobecnePanelLayout.createSequentialGroup()
                .addGroup(vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vseobecnePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1))
                    .addGroup(vseobecnePanelLayout.createSequentialGroup()
                        .addGroup(vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vseobecnePanelLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(mooreRadio)
                                    .addComponent(vonNeumanRadio)))
                            .addGroup(vseobecnePanelLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(vseobecnePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(riadkySpiner))
                                    .addGroup(vseobecnePanelLayout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addGap(18, 18, 18)
                                        .addComponent(stlpceSpiner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 1075, Short.MAX_VALUE)))
                .addContainerGap())
        );
        vseobecnePanelLayout.setVerticalGroup(
            vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vseobecnePanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vonNeumanRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mooreRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(riadkySpiner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(vseobecnePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(stlpceSpiner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(414, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Všeobecné nastavenia", vseobecnePanel);

        ulozitZavrietBtn.setText("Uložiť a zavrieť nastavenia");
        ulozitZavrietBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ulozitZavrietBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ulozitZavrietBtn)
                .addContainerGap())
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ulozitZavrietBtn)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void pridajBunkuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pridajBunkuButtonActionPerformed
        if(editujemBunku == false){
            String nazov = nazovBunkyField.getText();
            Color c = farbaButton.getBackground();                
            if(nazov.length() > 0){
                StavBunky typ = new StavBunky(nazov, c);
                boolean pridaloSa = this.vlastnosti.pridajCellType(typ);
                if (pridaloSa) {
                    nazovBunkyField.setText("");
                    farbaButton.setBackground(null);  
                    naplnComboBoxy();
                    this.pridajPravidloBtn.setEnabled(true);
                    this.nastavVychodziuBtn.setEnabled(true);
                    this.zmazBunkuBtn.setEnabled(true);
                    this.editBunkaBtn.setEnabled(true);

                    Object[] riadok = {nazov, c.getRGB(), 0, 0};
                    bunkyModel.addRow(riadok);  
                } else {
                    JOptionPane.showMessageDialog(null, "Bunka s týmto názvom už existuje!", "Chyba", JOptionPane.ERROR_MESSAGE);
                }              
            } else {
                JOptionPane.showMessageDialog(null, "Zadaj názov!", "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            StavBunky editovana = this.vlastnosti.getCellType(this.indexEditovanejBunkyVTabulke);
            this.vlastnosti.zmazBunku(editovana.getNazov(), true);
            
            String noveMeno = this.nazovBunkyField.getText();
            Color newColor = this.farbaButton.getBackground();
            boolean duplicita = this.vlastnosti.duplicitaBunky(noveMeno);
            if(duplicita){
                this.vlastnosti.pridajCellTypeIndex(editovana, this.indexEditovanejBunkyVTabulke);
                JOptionPane.showMessageDialog(null,"Bunka s takýmto názvom už existuje! Editovanie nie je možné.", "Duplicita", JOptionPane.INFORMATION_MESSAGE);
            } else {
                editovana.setNazov(noveMeno);
                editovana.setFarba(newColor);
                this.vlastnosti.pridajCellTypeIndex(editovana, this.indexEditovanejBunkyVTabulke);
            }
            
            //CellType bunka = this.vlastnosti.updateCellType(noveMeno, newColor, this.indexEditovanejBunky);
            
            
            
            int pocet = Integer.parseInt(this.bunkyModel.getValueAt(this.indexEditovanejBunkyVTabulke, 2).toString());
            int percent = Integer.parseInt(this.bunkyModel.getValueAt(this.indexEditovanejBunkyVTabulke, 3).toString());
            this.bunkyModel.removeRow(this.indexEditovanejBunkyVTabulke);
            Object[] riadok = new Object[] {editovana.getNazov(), editovana.getFarba().getRGB(), pocet, percent};
            this.bunkyModel.insertRow(this.indexEditovanejBunkyVTabulke, riadok);
            
            this.buttonyWhenEditCell(true); 
            this.editujemBunku = false;
            nazovBunkyField.setText("");
            farbaButton.setBackground(null); 
            if(this.vlastnosti.getIndexVychodzejBunky() > -1){
                this.vychodziaBunkaLabel.setText(this.vlastnosti.getCellName(this.vlastnosti.getIndexVychodzejBunky()));
            }
            this.pridajBunkuButton.setText("Pridaj typ bunky");
            
            TableCellRenderer centerRenderer = this.pravidlaTable.getColumnModel().getColumn(0).getCellRenderer();
            
            this.pravidlaModel = new DefaultTableModel(null, this.hlavickaPravidla);
            this.pravidlaTable.setModel(pravidlaModel);
            
            this.pravidlaTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            this.pravidlaTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            this.pravidlaTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            this.pravidlaTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            this.pravidlaTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            
            this.naplnTableModelPravidla();
            this.naplnComboBoxy();
        }
    }//GEN-LAST:event_pridajBunkuButtonActionPerformed

    private void pridajPravidloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pridajPravidloBtnActionPerformed
        if(this.editujemPravidlo == false){
            int pocitadloDuplicit = 0;
            String from = (String)this.fromCombo.getSelectedItem();
            String to = (String)this.toCombo.getSelectedItem();
            String neighbor = (String)this.neighborCombo.getSelectedItem();
            int minPocetSusedov = (Integer)this.numberMINSpiner.getValue();
            int maxPocetSusedov = (Integer)this.numberMAXspiner.getValue();
            int pravdepodobnost = (Integer)this.pravdepodobnostSpiner.getValue();

            if(minPocetSusedov <= maxPocetSusedov){
                for (int i = minPocetSusedov; i <= maxPocetSusedov; i++) {
                    boolean pridaloSa;
                    Pravidlo pravidlo = new Pravidlo(this.vlastnosti.getCellIndex(from), this.vlastnosti.getCellIndex(to), i, this.vlastnosti.getCellIndex(neighbor), pravdepodobnost);
                    pridaloSa = this.vlastnosti.pridajRule(pravidlo);  
                    if (pridaloSa) {
                        Object[] riadok = {from, to, i, neighbor, pravdepodobnost};
                        pravidlaModel.addRow(riadok);
                    } else {
                        pocitadloDuplicit++;
                    }
                }
                if (pocitadloDuplicit > 0) {
                    JOptionPane.showMessageDialog(null,"Niektoré pravidlá neboli pridané, už existujú.\nPočet: "+ pocitadloDuplicit, "Duplicita", JOptionPane.INFORMATION_MESSAGE);
                }
                this.fromCombo.setSelectedIndex(0);
                this.toCombo.setSelectedIndex(0);
                this.neighborCombo.setSelectedIndex(0);
                this.numberMINSpiner.setValue(0);
                this.numberMAXspiner.setValue(0);
                this.pravdepodobnostSpiner.setValue(100);
            } else {
                JOptionPane.showMessageDialog(null, "Maximálny počet susedov nemôže byť menší ako minimálny počet!", "Chyba", JOptionPane.ERROR_MESSAGE);
            }       
        } else {
            int from = this.vlastnosti.getCellIndex(this.fromCombo.getSelectedItem().toString());
            int to = this.vlastnosti.getCellIndex(this.toCombo.getSelectedItem().toString());
            int neighbor = this.vlastnosti.getCellIndex(this.neighborCombo.getSelectedItem().toString());
            int pocetSusedov = (Integer)this.numberMINSpiner.getValue();
            int pravdepodobnost = (Integer)this.pravdepodobnostSpiner.getValue();
            Pravidlo r = this.vlastnosti.getRule(this.indexEditPravidla);
            this.vlastnosti.zmazPravidlo(r.getFromIndex(), r.getToIndex(), r.getPocet(), r.getIndexSusednej(), r.getPravdepodobnost());
            boolean existuje = this.vlastnosti.duplicitaPravidla(from, to, pocetSusedov, neighbor);
            if(existuje == false){
                //r = this.vlastnosti.getRule(this.indexEditPravidla);
                r.setFromIndex(from);
                r.setToIndex(to);
                r.setPocet(pocetSusedov);
                r.setIndexSusednej(neighbor);
                r.setPravdepodobnost(pravdepodobnost);
                this.vlastnosti.pridajRuleOnIndex(r, this.indexEditPravidla);
                this.pravidlaModel.removeRow(this.indexEditPravidlaVTabulke);
                Object[] riadok = {this.vlastnosti.getCellName(from), this.vlastnosti.getCellName(to), pocetSusedov, this.vlastnosti.getCellName(neighbor), pravdepodobnost};
                this.pravidlaModel.insertRow(this.indexEditPravidlaVTabulke, riadok);   
                
                
            } else {
                this.vlastnosti.pridajRuleOnIndex(r, this.indexEditPravidla);
                JOptionPane.showMessageDialog(null,"Takéto pravidlo už existuje! Editovanie nie je možné.", "Duplicita", JOptionPane.INFORMATION_MESSAGE);
            }
            
            this.fromCombo.setSelectedIndex(0);
            this.toCombo.setSelectedIndex(0);
            this.neighborCombo.setSelectedIndex(0);
            this.numberMINSpiner.setValue(0);
            this.numberMAXspiner.setValue(0);
            this.pravdepodobnostSpiner.setValue(100);
            this.buttonyWhenEditRule(true);
            this.editujemPravidlo = false;
            this.pridajPravidloBtn.setText("Pridaj pravidlo");
        }
    }//GEN-LAST:event_pridajPravidloBtnActionPerformed

    private void vonNeumanRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vonNeumanRadioActionPerformed
        this.vlastnosti.setMooroveOkolie(false);
        SpinnerModel model1 = new SpinnerNumberModel(0, 0, 4, 1);
        SpinnerModel model2 = new SpinnerNumberModel(0, 0, 4, 1);
        this.numberMINSpiner.setModel(model1);
        this.numberMAXspiner.setModel(model2);
    }//GEN-LAST:event_vonNeumanRadioActionPerformed

    private void mooreRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mooreRadioActionPerformed
        this.vlastnosti.setMooroveOkolie(true);
        SpinnerModel model1 = new SpinnerNumberModel(0, 0, 8, 1);
        SpinnerModel model2 = new SpinnerNumberModel(0, 0, 8, 1);
        this.numberMINSpiner.setModel(model1);
        this.numberMAXspiner.setModel(model2);
    }//GEN-LAST:event_mooreRadioActionPerformed

    private void ulozitZavrietBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ulozitZavrietBtnActionPerformed
        if (this.vlastnosti.getIndexVychodzejBunky() == -1) {
            JOptionPane.showMessageDialog(null, "Nie je nastavený východiskový stav.", "Varovanie", JOptionPane.WARNING_MESSAGE);
        }
        this.setVisible(false);        
        this.dispose(); 
        this.automat.setVisible(true);
        this.automat.poNastaveni(this.zmena); 
        this.automat.prekresliPanel();        
    }//GEN-LAST:event_ulozitZavrietBtnActionPerformed

    private void nastavVychodziuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nastavVychodziuBtnActionPerformed
        int volba = JOptionPane.YES_OPTION;
        if (this.vlastnosti.isIsNastaveny() && this.zmena == false) {
            volba = JOptionPane.showOptionDialog(null, "Po tejto zmene bude automat zresetovaný do generácie 0.\nNaozaj chcete pokračovať?", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, this.options, null);
        } 
        if(volba == JOptionPane.YES_OPTION){
            int zvolenyRiadok = bunkyTable.getSelectedRow();
            if (zvolenyRiadok > -1) {
                String nazovBunky = (String)bunkyTable.getValueAt(zvolenyRiadok, 0);
                this.vlastnosti.setIndexVychodzejBunky(this.vlastnosti.getCellIndex(nazovBunky));
                this.vychodziaBunkaLabel.setText(nazovBunky);
            } else {
                JOptionPane.showMessageDialog(null, "Vyber riadok v tabuľke!", "Chyba", JOptionPane.ERROR_MESSAGE);
            }
            this.zmena = true;        
        }
    }//GEN-LAST:event_nastavVychodziuBtnActionPerformed

    private void zmazBunkuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zmazBunkuBtnActionPerformed
        int zvolenyRiadok = bunkyTable.getSelectedRow();
        if (zvolenyRiadok == -1){
            JOptionPane.showMessageDialog(null, "Vyber riadok v tabuľke!", "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int volba = JOptionPane.YES_OPTION;;
        if (this.vlastnosti.isIsNastaveny() && this.zmena == false) {
            volba = JOptionPane.showOptionDialog(null, "Po tejto zmene bude automat zresetovaný do generácie 0.\nNaozaj chcete pokračovať?", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, this.options, null);
        }  
        if(volba == JOptionPane.YES_OPTION){
            int volbaZmazania = JOptionPane.showOptionDialog(null, "Pri zmazaní bunky sa zmažú aj pravidlá, ktoré typ tejto bunky obsahujú!", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, this.options, null);
            if (volbaZmazania == JOptionPane.YES_OPTION) {
                if (zvolenyRiadok > -1) {
                    this.vlastnosti.zmazBunku((String)bunkyTable.getValueAt(zvolenyRiadok, 0), false);
                    this.bunkyModel.removeRow(zvolenyRiadok);
                    this.zmena = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Vyber riadok v tabuľke!", "Chyba", JOptionPane.ERROR_MESSAGE);
                }
            }
            naplnComboBoxy();
            this.pravidlaModel = new DefaultTableModel(null, this.hlavickaPravidla);
            this.pravidlaTable.setModel(pravidlaModel);
            this.naplnTableModelPravidla();
            if(this.vlastnosti.getIndexVychodzejBunky() == -1){
                this.vychodziaBunkaLabel.setText("nedefinovaný");
            }
            if(this.vlastnosti.getCellTypeCount() == 0){
                this.pridajPravidloBtn.setEnabled(false);
                this.nastavVychodziuBtn.setEnabled(false);
                this.zmazBunkuBtn.setEnabled(false);
                this.editBunkaBtn.setEnabled(false);
            }            
        }       
    }//GEN-LAST:event_zmazBunkuBtnActionPerformed

    private void zmazPravidloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zmazPravidloBtnActionPerformed
        int zvolenyRiadok = pravidlaTable.getSelectedRow();
        int volba = JOptionPane.showOptionDialog(null, "Naozaj zmazať?", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, this.options, null);
        if (volba == JOptionPane.YES_OPTION) {
            if(zvolenyRiadok > -1){
                int from = vlastnosti.getCellIndex(pravidlaTable.getValueAt(zvolenyRiadok, 0).toString());
                int to = vlastnosti.getCellIndex(pravidlaTable.getValueAt(zvolenyRiadok, 1).toString());
                int pocet = (Integer)pravidlaTable.getValueAt(zvolenyRiadok, 2);
                int sused = vlastnosti.getCellIndex(pravidlaTable.getValueAt(zvolenyRiadok, 3).toString());
                int pravdepodobnost = (Integer)pravidlaTable.getValueAt(zvolenyRiadok, 4);
                this.vlastnosti.zmazPravidlo(from, to, pocet, sused, pravdepodobnost);
                this.pravidlaModel.removeRow(zvolenyRiadok);
            } else {
                JOptionPane.showMessageDialog(null, "Vyber riadok v tabuľke!", "Chyba", JOptionPane.ERROR_MESSAGE);
            }      
        }
    }//GEN-LAST:event_zmazPravidloBtnActionPerformed

    private void editBunkaBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBunkaBtnActionPerformed
        int zvolenyRiadok = bunkyTable.getSelectedRow();
        if (zvolenyRiadok > -1) {
            this.buttonyWhenEditCell(false);
            this.indexEditovanejBunky = this.vlastnosti.getCellIndex(bunkyTable.getValueAt(zvolenyRiadok, 0).toString());
            this.indexEditovanejBunkyVTabulke = zvolenyRiadok;
            Color color = new Color(Integer.parseInt(bunkyTable.getValueAt(zvolenyRiadok, 1).toString()));
            farbaButton.setBackground(color);                       
            this.nazovBunkyField.setText(bunkyTable.getValueAt(zvolenyRiadok, 0).toString()); 
            this.editujemBunku = true;
            this.pridajBunkuButton.setText("Ulož zmenu");
        } else {
            JOptionPane.showMessageDialog(null, "Vyber riadok v tabuľke!", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_editBunkaBtnActionPerformed

    private void editPravidloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPravidloBtnActionPerformed
        int zvolenyRiadok = pravidlaTable.getSelectedRow();
        if (zvolenyRiadok > -1) {
            String from = this.pravidlaTable.getValueAt(zvolenyRiadok, 0).toString();
            String to = this.pravidlaTable.getValueAt(zvolenyRiadok, 1).toString();
            int pocet = Integer.parseInt(this.pravidlaTable.getValueAt(zvolenyRiadok, 2).toString());
            String neig = this.pravidlaTable.getValueAt(zvolenyRiadok, 3).toString();
            int pravdepodobnost = Integer.parseInt(this.pravidlaTable.getValueAt(zvolenyRiadok, 4).toString());
            
            this.fromCombo.setSelectedItem(from);
            this.toCombo.setSelectedItem(to);
            this.numberMINSpiner.setValue(pocet);
            this.neighborCombo.setSelectedItem(neig);
            this.pravdepodobnostSpiner.setValue(pravdepodobnost);
            this.pridajPravidloBtn.setText("Ulož zmenu");
            this.buttonyWhenEditRule(false);
            this.indexEditPravidlaVTabulke = zvolenyRiadok;
            for (int i = 0; i < this.vlastnosti.getRulesCount(); i++) {
                Pravidlo r = this.vlastnosti.getRule(i);
                if(this.vlastnosti.getCellName(r.getFromIndex()).equals(from) && this.vlastnosti.getCellName(r.getToIndex()).equals(to) && r.getPocet() == pocet && this.vlastnosti.getCellName(r.getIndexSusednej()).equals(neig) && r.getPravdepodobnost() == pravdepodobnost){
                    this.indexEditPravidla = i;
                }
            }
            this.editujemPravidlo = true;
        } else {
            JOptionPane.showMessageDialog(null, "Vyber riadok v tabuľke!", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_editPravidloBtnActionPerformed

    private void riadkySpinerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_riadkySpinerStateChanged
        int volba = JOptionPane.YES_OPTION;
        if(this.rozmeryNastavene){
            if (this.vlastnosti.isIsNastaveny() && this.zmena == false) {
                volba = JOptionPane.showOptionDialog(null, "Po tejto zmene bude automat zresetovaný do generácie 0.\nNaozaj chcete pokračovať?", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, this.options, null);
            }
        }   
        if(volba == JOptionPane.YES_OPTION){
            this.vlastnosti.setRiadky((Integer)this.riadkySpiner.getValue()); 
            this.zmena = true;             
        } else {
            this.rozmeryNastavene = false;
            this.riadkySpiner.setValue(this.vlastnosti.getRiadky());
            this.rozmeryNastavene = true;
            this.zmena = false;
        }  
    }//GEN-LAST:event_riadkySpinerStateChanged

    private void stlpceSpinerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stlpceSpinerStateChanged
        int volba = JOptionPane.YES_OPTION;
        if(this.rozmeryNastavene){
            if (this.vlastnosti.isIsNastaveny() && this.zmena == false) {
                volba = JOptionPane.showOptionDialog(null, "Po tejto zmene bude automat zresetovaný do generácie 0.\nNaozaj chcete pokračovať?", "Varovanie", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, this.options, null);
            }
        } 
        if(volba == JOptionPane.YES_OPTION){
            this.vlastnosti.setStlpce((Integer)this.stlpceSpiner.getValue());
            this.zmena = true;            
        } else {
            this.rozmeryNastavene = false;  //aby sa nepytal
            this.stlpceSpiner.setValue(this.vlastnosti.getStlpce());  //nastane akoze zmena
            this.rozmeryNastavene = true; //aby sa zase pytal
            this.zmena = false; //upravi, lebo zmena nenastala, lebo hodnota spineru sa zvysila/znizila o 1 a nasledne sa vratila do povodneho stavu
        }
    }//GEN-LAST:event_stlpceSpinerStateChanged
    
    private void buttonyWhenEditRule(boolean enabled){
        this.jTabbedPane1.setEnabled(enabled);
        this.ulozitZavrietBtn.setEnabled(enabled);
        this.zmazPravidloBtn.setEnabled(enabled);
        this.editPravidloBtn.setEnabled(enabled);        
        this.numberMAXspiner.setEnabled(enabled);
    }
    
    private void buttonyWhenEditCell(boolean enabled){
        this.nastavVychodziuBtn.setEnabled(enabled);
        this.zmazBunkuBtn.setEnabled(enabled);
        this.editBunkaBtn.setEnabled(enabled);
        this.jTabbedPane1.setEnabled(enabled);
        this.ulozitZavrietBtn.setEnabled(enabled);
    }
    
    
    private void naplnComboBoxy(){
        this.fromCombo.removeAllItems();
        this.toCombo.removeAllItems();
        this.neighborCombo.removeAllItems();
        int pocetTypov = this.vlastnosti.getCellTypeCount();
        if(pocetTypov > 0){
            for (int i = 0; i < pocetTypov; i++) {                
                this.fromCombo.addItem(this.vlastnosti.getCellType(i).getNazov());
                this.toCombo.addItem(this.vlastnosti.getCellType(i).getNazov());
                this.neighborCombo.addItem(this.vlastnosti.getCellType(i).getNazov());
            }
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable bunkyTable;
    private javax.swing.JButton editBunkaBtn;
    private javax.swing.JButton editPravidloBtn;
    private javax.swing.JButton farbaButton;
    private javax.swing.JComboBox<String> fromCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton mooreRadio;
    private javax.swing.JButton nastavVychodziuBtn;
    private javax.swing.JTextField nazovBunkyField;
    private javax.swing.JComboBox<String> neighborCombo;
    private javax.swing.JSpinner numberMAXspiner;
    private javax.swing.JSpinner numberMINSpiner;
    private javax.swing.ButtonGroup okolieRadioGroup;
    private javax.swing.JSpinner pravdepodobnostSpiner;
    private javax.swing.JPanel pravidlaPanel;
    private javax.swing.JTable pravidlaTable;
    private javax.swing.JButton pridajBunkuButton;
    private javax.swing.JButton pridajPravidloBtn;
    private javax.swing.JSpinner riadkySpiner;
    private javax.swing.JSpinner stlpceSpiner;
    private javax.swing.JComboBox<String> toCombo;
    private javax.swing.JPanel typyBuniekPanel;
    private javax.swing.JButton ulozitZavrietBtn;
    private javax.swing.JRadioButton vonNeumanRadio;
    private javax.swing.JPanel vseobecnePanel;
    private javax.swing.JLabel vychodziaBunkaLabel;
    private javax.swing.JButton zmazBunkuBtn;
    private javax.swing.JButton zmazPravidloBtn;
    // End of variables declaration//GEN-END:variables
}

class ColorColumnCellRenderer extends DefaultTableCellRenderer{    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        int hodnota = Integer.parseInt(table.getValueAt(row, 1).toString());
        c.setBackground(new Color(hodnota));
        c.setForeground(new Color(hodnota));
        return c;    
    }
}


