/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * Classe pour afficher le fil de nouvelles sous forme de tableau
 * @author sylvain
 */
public class Feeds_feedsJFrame extends Template implements Controller_Observer{

    private final Controller eventsList; //accède au contrôleur
        
    private String feedTitle = "Fil de nouvelles"; //le titre du fil de nouvelles
    
    /**
     * Constructeur de la vue du fil de nouvelles
     * @param eventsList le fil de nouvelles
     */
    public Feeds_feedsJFrame(Controller eventsList) {
        this.eventsList = eventsList;
                
        initComponents();                 
                        
        //construction d'un objet de type tableau
        AbstractTableModel model = new AbstractTableModel() {
            
            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "Titre";
                    case 1:
                        return "Date de publication";
                    case 2:
                        return "Lu boolean";
                    case 3:
                        return "Favori boolean";
                    case 4:
                        return "Lu";
                    case 5:
                        return "Favori";
                    case 6:
                        return "Link";
                    default:
                        return "Description";
                }
            }
            
            @Override
            public int getRowCount() {
                return eventsList.getEventList().size();
            }

            @Override
            public int getColumnCount() {
                return 8;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                switch(column) {
                    case 4: return ImageIcon.class;
                    case 5: return ImageIcon.class;                    
                    default: return Object.class;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                ImageIcon icon;
                Image img;
                Image newimg;
                                
                switch (columnIndex) {
                    case 0:
                        return eventsList.getEventList().get(rowIndex).getTitre();
                    case 1:
                        return eventsList.getEventList().get(rowIndex).getDatePub();
                    case 2:
                        return eventsList.getEventList().get(rowIndex).getLu();
                    case 3:
                        return eventsList.getEventList().get(rowIndex).getFavori();
                    case 4:
                        //insertion d'une icône dans la cellule
                        icon = new ImageIcon("./src/images/lu_table.png");         
                        img = icon.getImage();
                        newimg = img.getScaledInstance(17, 17,  java.awt.Image.SCALE_SMOOTH);
                        if(eventsList.getEventList().get(rowIndex).getLu() == true)
                            icon = new ImageIcon(newimg);
                        else 
                            icon = new ImageIcon();
                        return icon;
                    case 5:
                        //insertion d'une icône dans la cellule
                        icon = new ImageIcon("./src/images/favori_table.png");         
                        img = icon.getImage();
                        newimg = img.getScaledInstance(17, 17,  java.awt.Image.SCALE_SMOOTH);
                        if(eventsList.getEventList().get(rowIndex).getFavori() == true)
                            icon = new ImageIcon(newimg);
                        else 
                            icon = new ImageIcon();
                        return icon;
                    case 6:
                        return eventsList.getEventList().get(rowIndex).getURL();
                    default:
                        return eventsList.getEventList().get(rowIndex).getDescription();
                }
            }
        };
        jTable_feeds.setModel(model);         
        
        //affiche une icône à côté d'un bouton au démarage
        this.setLabelIcon(jLabel_import, jPanel_import, "./src/images/importer.png");
        this.setLabelIcon(jLabel_export, jPanel_export, "./src/images/exporter.png");
        this.setLabelIcon(jLabel_nouveau, jPanel_nouveau, "./src/images/ajouter.png"); 
        
        //ajuste la largeur des colonnes 
        this.setColumnWidth();      
        //Accessibilité du bouton export 
        this.buttonAccess();  
    }
    
    /**
     * Applique le titre du fil de nouvelles avec le nombre de nouvelles qu'il contient
     * @param title le titre du fil de nouvelles
     * @return le titre du fil de nouvelles avec le nombre de nouvelles qu'il contient
     */
    protected String setFeedTitle(String title){
        feedTitle = title;
        return title + " [" + eventsList.getEventList().size() + "]";
    }    
        
    /**
     * Ajuste la largeur des colonnes dans le tableau
     */
    private void setColumnWidth(){
        TableColumn column;
        for(int i = 0; i < 8; i++){
            column = jTable_feeds.getColumnModel().getColumn(i);
            if(i == 1){
                column.setMinWidth(230);
                column.setMaxWidth(230);
            }
            else if(i == 4 | i == 5)
                column.setMaxWidth(50);
            else if(i == 2 | i == 3 | i == 6 | i == 7){
                 column.setMinWidth(0);                 
                 column.setMaxWidth(0); 
            }
        }
        this.repaint();
    }

    /**
     * Accessibilité du bouton export
     */
    final void buttonAccess(){
        //si le fil de nouvelle est vide, ajuster l'accessibilité du bouton en conséquences
        if(eventsList.getEventList().isEmpty()) {
            jButton_header_toolbar_export.setEnabled(false);
            jButton_header_toolbar_export.setForeground(new Color(128, 204, 230));
            this.setLabelIcon(jLabel_export, jPanel_export, "./src/images/exporter_off.png");
        }
        else {
            jButton_header_toolbar_export.setEnabled(true);
            jButton_header_toolbar_export.setForeground(Color.WHITE);
            this.setLabelIcon(jLabel_export, jPanel_export, "./src/images/exporter.png");
        }      
    } 
    
    /**
     * Retourne la largeur du titre avec un ajustement
     * @param titleWidth la largeur du titre
     * @return la largeur du titre ajusté
     */    
    @Override
    public int titleWidth(int titleWidth){
        return titleWidth * 2;
    }      
    
    /**
     * Mise à jour des informations contenu dans le tableau, sélection de la rangée appropriée et 
     * ajustement du nombre de nouvelles contenu dans le fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 or 4 = actual | 5 = précédente | 6 = suivante
     */
    private void updateFeedTable(int index, int selection) {
        jLabel_header_title.setText(this.setFeedTitle(feedTitle));
        jTable_feeds.updateUI();
        if(selection == 1 | selection == 3 | selection == 4) //sélectionne la nouvelle selon sa position dans le fil de nouvelles
            jTable_feeds.setRowSelectionInterval(index, index);
        if(selection == 2){ //aucune sélection
            jTable_feeds.setRowSelectionInterval(0, 0);
            jTable_feeds.clearSelection();
        }
        if(selection == 5) //sélectionne la nouvelle précédente
            jTable_feeds.setRowSelectionInterval(index-1, index-1); 
        if(selection == 6) //sélectionne la nouvelle suivante
            jTable_feeds.setRowSelectionInterval(index+1, index+1);           
         
        //Accessibilité du bouton export 
        this.buttonAccess(); 
    } 
     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_feedsMain = new javax.swing.JPanel();
        jPanel_header = new javax.swing.JPanel();
        jLabel_header_title = new javax.swing.JLabel();
        jPanel_header_toolbar = new javax.swing.JPanel();
        jButton_header_toolbar_export = new javax.swing.JButton();
        jButton_header_toolbar_import = new javax.swing.JButton();
        jButton_header_toolbar_nouv = new javax.swing.JButton();
        jPanel_export = new javax.swing.JPanel();
        jLabel_export = new javax.swing.JLabel();
        jPanel_import = new javax.swing.JPanel();
        jLabel_import = new javax.swing.JLabel();
        jPanel_nouveau = new javax.swing.JPanel();
        jLabel_nouveau = new javax.swing.JLabel();
        jPanel_bottom = new javax.swing.JPanel();
        jScrollPane_feeds = new javax.swing.JScrollPane();
        jTable_feeds = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel_header.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_header_title.setFont(new java.awt.Font("Lucida Grande", 1, 20)); // NOI18N
        jLabel_header_title.setForeground(new java.awt.Color(0, 153, 204));
        jLabel_header_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_header_title.setText("Fil de nouvelles");

        jPanel_header_toolbar.setBackground(new java.awt.Color(0, 153, 204));

        jButton_header_toolbar_export.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_export.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_export.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_export.setText("Exporter");
        jButton_header_toolbar_export.setBorder(null);
        jButton_header_toolbar_export.setBorderPainted(false);
        jButton_header_toolbar_export.setContentAreaFilled(false);
        jButton_header_toolbar_export.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_exportActionPerformed(evt);
            }
        });

        jButton_header_toolbar_import.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_import.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_import.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_import.setText("Importer");
        jButton_header_toolbar_import.setBorder(null);
        jButton_header_toolbar_import.setBorderPainted(false);
        jButton_header_toolbar_import.setContentAreaFilled(false);
        jButton_header_toolbar_import.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_importActionPerformed(evt);
            }
        });

        jButton_header_toolbar_nouv.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_nouv.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_nouv.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_nouv.setText("Nouveau");
        jButton_header_toolbar_nouv.setBorder(null);
        jButton_header_toolbar_nouv.setBorderPainted(false);
        jButton_header_toolbar_nouv.setContentAreaFilled(false);
        jButton_header_toolbar_nouv.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_nouv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_nouvActionPerformed(evt);
            }
        });

        jPanel_export.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_export.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_export.setLayout(new java.awt.BorderLayout());

        jLabel_export.setIconTextGap(0);
        jLabel_export.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_export.add(jLabel_export, java.awt.BorderLayout.CENTER);

        jPanel_import.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_import.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_import.setLayout(new java.awt.BorderLayout());

        jLabel_import.setIconTextGap(0);
        jLabel_import.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_import.add(jLabel_import, java.awt.BorderLayout.CENTER);

        jPanel_nouveau.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_nouveau.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_nouveau.setLayout(new java.awt.BorderLayout());

        jLabel_nouveau.setIconTextGap(0);
        jLabel_nouveau.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_nouveau.add(jLabel_nouveau, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel_header_toolbarLayout = new javax.swing.GroupLayout(jPanel_header_toolbar);
        jPanel_header_toolbar.setLayout(jPanel_header_toolbarLayout);
        jPanel_header_toolbarLayout.setHorizontalGroup(
            jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                .addContainerGap(299, Short.MAX_VALUE)
                .addComponent(jPanel_import, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_import)
                .addGap(25, 25, 25)
                .addComponent(jPanel_export, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_export)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 199, Short.MAX_VALUE)
                .addComponent(jPanel_nouveau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_nouv)
                .addGap(25, 25, 25))
        );
        jPanel_header_toolbarLayout.setVerticalGroup(
            jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel_export, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_header_toolbar_export))
                    .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel_import, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_header_toolbar_import))
                        .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton_header_toolbar_nouv)
                            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jPanel_nouveau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout jPanel_headerLayout = new javax.swing.GroupLayout(jPanel_header);
        jPanel_header.setLayout(jPanel_headerLayout);
        jPanel_headerLayout.setHorizontalGroup(
            jPanel_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_headerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_header_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel_header_toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel_headerLayout.setVerticalGroup(
            jPanel_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_headerLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel_header_title, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_header_toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel_bottom.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane_feeds.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 3));
        jScrollPane_feeds.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTable_feeds.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", null, "", null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Titre", "Date de publication", "Lu boolean", "Favori boolean", "Lu", "Favori", "Link", "Description"
            }
        ));
        jTable_feeds.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTable_feeds.setGridColor(new java.awt.Color(153, 153, 153));
        jTable_feeds.setRowHeight(50);
        jTable_feeds.setSelectionBackground(new java.awt.Color(153, 153, 153));
        jTable_feeds.setShowGrid(true);
        jTable_feeds.setShowVerticalLines(false);
        jTable_feeds.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_feedsMouseClicked(evt);
            }
        });
        jScrollPane_feeds.setViewportView(jTable_feeds);

        javax.swing.GroupLayout jPanel_bottomLayout = new javax.swing.GroupLayout(jPanel_bottom);
        jPanel_bottom.setLayout(jPanel_bottomLayout);
        jPanel_bottomLayout.setHorizontalGroup(
            jPanel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottomLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane_feeds, javax.swing.GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel_bottomLayout.setVerticalGroup(
            jPanel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottomLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane_feeds, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel_feedsMainLayout = new javax.swing.GroupLayout(jPanel_feedsMain);
        jPanel_feedsMain.setLayout(jPanel_feedsMainLayout);
        jPanel_feedsMainLayout.setHorizontalGroup(
            jPanel_feedsMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_feedsMainLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel_feedsMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel_feedsMainLayout.setVerticalGroup(
            jPanel_feedsMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_feedsMainLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_feedsMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_feedsMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Bouton pour ajouter une nouvelle
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_nouvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_nouvActionPerformed
        //construit une vue pour ajouter / modifier une nouvelle
        Event_addModJDialog addModJDialog = new Event_addModJDialog(eventsList);
        //ajoute le contrôleur à la liste d'observateurs de addModJDialog
        addModJDialog.addEventObserver(eventsList);        
        //conserve la largeur de la vue par défault
        addModJDialog.resizeFrame(1); 
        
        //déclare feedsJFrame comme Leaf et applique un écouteur sur la fenêtre feedsJFrame
        Event_Leaf feedsLeaf = new Event_Leaf(this, 1);
        this.addWindowListener(feedsLeaf);        
        //déclare addModJDialog comme Composite et applique un écouteur sur la fenêtre addModJDialog
        Event_Composite eventComposite = new Event_Composite(addModJDialog, 1);
        addModJDialog.addWindowListener(eventComposite);
        //ajoute feedsLeaf à la liste de eventComposite
        eventComposite.addComponent(feedsLeaf);        
    }//GEN-LAST:event_jButton_header_toolbar_nouvActionPerformed

    /**
     * Ouverture de la nouvelle en cliquant sur une rangée du tableau
     * @param evt l'action produite
     */
    private void jTable_feedsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_feedsMouseClicked
        String titre = jTable_feeds.getModel().getValueAt(jTable_feeds.getSelectedRow(), 0).toString();
        //construit une vue pour afficher la nouvelle        
        Event_eventJFrame eventJFrame = new Event_eventJFrame(eventsList, jTable_feeds.getSelectedRow(), this);
        //ajoute le contrôleur à la liste d'observateurs de eventJFrame
        eventJFrame.addEventObserver(eventsList);
        //ajoute le la vue eventJFrame à la liste d'observateurs du contrôleur
        eventsList.addControllerObserver(eventJFrame);
        //ajuste la vue par rapport au titre
        eventJFrame.resizeFrame(eventsList.getlargestTitle());         
    }//GEN-LAST:event_jTable_feedsMouseClicked

    /**
     * Bouton d'exportation vers un fichier XML
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_exportActionPerformed
        //affiche la vue pour choisir l'endroit de la sauvegarde
        Feeds_exportJDialog exportJDialog = new Feeds_exportJDialog(eventsList, feedTitle);
        exportJDialog.setLocationRelativeTo(null);
        exportJDialog.setVisible(true);
        
        //déclare feedsJFrame comme Leaf et applique un écouteur sur la fenêtre feedsJFrame
        Event_Leaf feedsLeaf = new Event_Leaf(this, 1);
        this.addWindowListener(feedsLeaf);        
        //déclare exportJDialog comme Leaf et applique un écouteur sur la fenêtre exportJDialog
        Event_Composite eventComposite = new Event_Composite(exportJDialog, 2);
        exportJDialog.addWindowListener(eventComposite);
        //ajoute feedsLeaf à la liste de eventComposite
        eventComposite.addComponent(feedsLeaf);
    }//GEN-LAST:event_jButton_header_toolbar_exportActionPerformed

    /**
     * Bouton d'importation d'un hyperlien (flux RSS ou fichier)
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_importActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_importActionPerformed
        //construit une vue pour importer un hyperlien ou un fichier      
        Feeds_importJDialog importjDialog = new Feeds_importJDialog(eventsList);
        importjDialog.setLocationRelativeTo(null);
        importjDialog.setVisible(true);
        
        //déclare feedsJFrame comme Leaf et applique un écouteur sur la fenêtre feedsJFrame
        Event_Leaf feedsLeaf = new Event_Leaf(this, 1);
        this.addWindowListener(feedsLeaf);        
        //déclare importjDialog comme Composite et applique un écouteur sur la fenêtre importjDialog
        Event_Composite eventComposite = new Event_Composite(importjDialog, 2);
        importjDialog.addWindowListener(eventComposite);
        //ajoute feedsLeaf à la liste de eventComposite
        eventComposite.addComponent(feedsLeaf);        
    }//GEN-LAST:event_jButton_header_toolbar_importActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_header_toolbar_export;
    private javax.swing.JButton jButton_header_toolbar_import;
    private javax.swing.JButton jButton_header_toolbar_nouv;
    private javax.swing.JLabel jLabel_export;
    private javax.swing.JLabel jLabel_header_title;
    private javax.swing.JLabel jLabel_import;
    private javax.swing.JLabel jLabel_nouveau;
    private javax.swing.JPanel jPanel_bottom;
    private javax.swing.JPanel jPanel_export;
    private javax.swing.JPanel jPanel_feedsMain;
    private javax.swing.JPanel jPanel_header;
    private javax.swing.JPanel jPanel_header_toolbar;
    private javax.swing.JPanel jPanel_import;
    private javax.swing.JPanel jPanel_nouveau;
    private javax.swing.JScrollPane jScrollPane_feeds;
    private javax.swing.JTable jTable_feeds;
    // End of variables declaration//GEN-END:variables

    /**
     * Pour être notifié de tout changement provenant de la source (en l'occurence le contrôleur)
     * @param eventsList le fil de nouvelles
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 or 4 = actual | 5 = précédente | 6 = suivante
     */
    @Override
    public void updateViews(ArrayList<Model_events> eventsList, int index, int selection) {
        updateFeedTable(index, selection);
    }
}
