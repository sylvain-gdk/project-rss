/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

/**
 * Classe pour ajouter ou modifier les nouvelles
 * @author Sylvain
 */
public class Event_addModJDialog extends Template implements Event_Subject{
    
    private final ArrayList<Event_Observer> observers; //liste des observateurs de nouvelles
        
    private Stack<Event_Command> annulerStack = new Stack(); //pile de commandes annuler
    private Stack<Event_Command> refaireStack = new Stack(); //pile de commandes refaire
   
    private final Controller eventsList; //accède au contrôleur
    private Event_eventJFrame eventJFrame; //accède à la fenêtre de la nouvelle
     
    private int index = -1; //l'index de la nouvelle est à -1 s'il n'existe pas
    private String datePub; //la date de la nouvelle
    private boolean favori = false; //le statut favori
    private boolean lu = true; //le statut lu    
    
    //mise en forme de l'affichage de la date    
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("en", "US"));
       
    /**
     * Construit une vue pour ajouter une nouvelle
     * @param eventsList le fil de nouvelles
     */
    public Event_addModJDialog(Controller eventsList) {
        this.observers = new ArrayList();
        this.eventsList = eventsList;
        
        initComponents();        
        
        //affiche une icône à côté d'un bouton
        this.setLabelIcon(jLabel_undo, jPanel_undo, "./src/images/undo.png");
        this.setLabelIcon(jLabel_redo, jPanel_redo, "./src/images/redo.png");

        //accessibilité des bouton undo et redo
        this.buttonAccess();        
    }    
    
    /**
     * Construit une vue pour modifier une nouvelle existante
     * @param eventsList le fil de nouvelles
     * @param index l'index de la nouvelle
     */
     public Event_addModJDialog(Controller eventsList, int index) {
        this.observers = new ArrayList();
        this.eventsList = eventsList;        
        this.index = index;
        
        initComponents(); 
             
        //applique la nouvelle dans la vue
        this.setEventMod(eventsList, index);
        
        //affiche une icône à côté d'un bouton
        this.setLabelIcon(jLabel_undo, jPanel_undo, "./src/images/undo.png");
        this.setLabelIcon(jLabel_redo, jPanel_redo, "./src/images/redo.png");

        //accessibilité des bouton undo et redo
        this.buttonAccess();        
    }      
    
    /**
     * Applique la nouvelle dans la vue
     * @param eventsList le fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     */
    protected final void setEventMod(Controller eventsList, int index){
        this.datePub = eventsList.getEventList().get(index).getDatePub();
        this.favori = eventsList.getEventList().get(index).getFavori();
        this.lu = true;
        this.index = index;
        //si le titre est trop long, on coupe
        if(eventsList.getEventList().get(index).getTitre().length() > 80)
            jLabel_header_title.setText(eventsList.getEventList().get(index).getTitre().substring(0, 80) + "...");  
        else jLabel_header_title.setText(eventsList.getEventList().get(index).getTitre());        
        jTextField_event_titre.setText(eventsList.getEventList().get(index).getTitre());  
        jTextArea_event_desc.setText(eventsList.getEventList().get(index).getDescription());
        jTextArea_event_desc.setCaretPosition(0);
        jTextField_event_url.setText(eventsList.getEventList().get(index).getURL()); 
    }   
    
    /**
     * Sauvegarde la nouvelle
     */
    private void saveEvent(){
        Date now = new Date();
        Model_events newEvent = new Model_events();
        newEvent.setTitre(jTextField_event_titre.getText());
        newEvent.setDescription(jTextArea_event_desc.getText());
        newEvent.setDatePub(now);
        newEvent.setURL(jTextField_event_url.getText());
        newEvent.setLu(lu);
        newEvent.setFavori(favori);
        //notifie les observateurs de nouvelles (en l'occurence le contrôleur)
        //si la nouvelle existe déjà
        if(index >= 0){
            this.notifyEventObserver(newEvent, index, 3);
        }
        else       
            this.notifyEventObserver(newEvent, index, 1);
    }
    
    /**
     * Retourne la largeur du titre avec un ajustement
     * @param titleWidth la largeur du titre
     * @return la largeur du titre ajusté
     */ 
    @Override
    int titleWidth(int titleWidth) {
        return titleWidth * 3;
    }
    
    /**
     * Accessibilité des bouton undo et redo
     */
    private void buttonAccess(){
        //si les piles sont vide, ajuster l'accessibilité des boutons en conséquences
        if(annulerStack.empty()) {
            jButton_header_toolbar_undo.setEnabled(false);
            jButton_header_toolbar_undo.setForeground(new Color(128, 204, 230));
            this.setLabelIcon(jLabel_undo, jPanel_undo, "./src/images/undo_off.png");
        }
        else {
            jButton_header_toolbar_undo.setEnabled(true);
            jButton_header_toolbar_undo.setForeground(Color.WHITE);
            this.setLabelIcon(jLabel_undo, jPanel_undo, "./src/images/undo.png");
        }

        if(refaireStack.empty()) {
            jButton_header_toolbar_redo.setEnabled(false);
            jButton_header_toolbar_redo.setForeground(new Color(128, 204, 230));
            this.setLabelIcon(jLabel_redo, jPanel_redo, "./src/images/redo_off.png");
        }
        else {
            jButton_header_toolbar_redo.setEnabled(true);  
            jButton_header_toolbar_redo.setForeground(Color.WHITE);        
            this.setLabelIcon(jLabel_redo, jPanel_redo, "./src/images/redo.png");
        }       
    }  
    
    /**
     * Conserve une trace des entrées les plus récentes
     */
    private void keepTrace(){
        //conserve le titre, la description et l'hyperlien dans l'objet store
        Event_CommandStore store = new Event_CommandStore(jTextField_event_titre.getText(), jTextArea_event_desc.getText(), jTextField_event_url.getText(), jTextField_event_titre, jTextArea_event_desc, jTextField_event_url);       
        //ajoute l'objet store à la pile annulerStack pour conserver une trace
        annulerStack.push(store);
        //conserve la dernière entrée du titre
        store.setAfter(jTextField_event_titre.getText(), jTextArea_event_desc.getText(), jTextField_event_url.getText());
        //contrôle l'accessibilité des boutons undo et redo
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

        jPanel_header = new javax.swing.JPanel();
        jLabel_header_title = new javax.swing.JLabel();
        jPanel_header_toolbar = new javax.swing.JPanel();
        jButton_header_toolbar_undo = new javax.swing.JButton();
        jButton_header_toolbar_redo = new javax.swing.JButton();
        jPanel_undo = new javax.swing.JPanel();
        jLabel_undo = new javax.swing.JLabel();
        jPanel_redo = new javax.swing.JPanel();
        jLabel_redo = new javax.swing.JLabel();
        jPanel_bottom = new javax.swing.JPanel();
        jPanel_bottom_event = new javax.swing.JPanel();
        jLabel_events_title = new javax.swing.JLabel();
        jTextField_event_titre = new javax.swing.JTextField();
        jLabel_events_desc = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea_event_desc = new javax.swing.JTextArea();
        jTextField_event_url = new javax.swing.JTextField();
        jLabel_events_url = new javax.swing.JLabel();
        jPanel_bottom_toolbar = new javax.swing.JPanel();
        jButton_sauvegarder = new javax.swing.JButton();
        jButton_annuler = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel_header.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_header_title.setFont(new java.awt.Font("Lucida Grande", 1, 20)); // NOI18N
        jLabel_header_title.setForeground(new java.awt.Color(0, 153, 204));
        jLabel_header_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_header_title.setText("Éditeur de nouvelle");

        jPanel_header_toolbar.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_header_toolbar.setPreferredSize(new java.awt.Dimension(800, 25));

        jButton_header_toolbar_undo.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_undo.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_undo.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_undo.setText("Annuler");
        jButton_header_toolbar_undo.setBorder(null);
        jButton_header_toolbar_undo.setBorderPainted(false);
        jButton_header_toolbar_undo.setContentAreaFilled(false);
        jButton_header_toolbar_undo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_undoActionPerformed(evt);
            }
        });

        jButton_header_toolbar_redo.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_redo.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_redo.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_redo.setText("Refaire");
        jButton_header_toolbar_redo.setBorder(null);
        jButton_header_toolbar_redo.setBorderPainted(false);
        jButton_header_toolbar_redo.setContentAreaFilled(false);
        jButton_header_toolbar_redo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_redo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton_header_toolbar_redo.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton_header_toolbar_redo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_redoActionPerformed(evt);
            }
        });

        jPanel_undo.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_undo.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_undo.setLayout(new java.awt.BorderLayout());

        jLabel_undo.setIconTextGap(0);
        jLabel_undo.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_undo.add(jLabel_undo, java.awt.BorderLayout.CENTER);

        jPanel_redo.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_redo.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_redo.setLayout(new java.awt.BorderLayout());

        jLabel_redo.setIconTextGap(0);
        jLabel_redo.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_redo.add(jLabel_redo, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel_header_toolbarLayout = new javax.swing.GroupLayout(jPanel_header_toolbar);
        jPanel_header_toolbar.setLayout(jPanel_header_toolbarLayout);
        jPanel_header_toolbarLayout.setHorizontalGroup(
            jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel_undo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_undo)
                .addGap(25, 25, 25)
                .addComponent(jPanel_redo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_redo)
                .addGap(482, 482, 482))
        );
        jPanel_header_toolbarLayout.setVerticalGroup(
            jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_redo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_undo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_header_toolbar_undo)
                    .addComponent(jButton_header_toolbar_redo))
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
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel_header_title, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_header_toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel_bottom.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_bottom.setPreferredSize(new java.awt.Dimension(800, 435));

        jPanel_bottom_event.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_bottom_event.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        jLabel_events_title.setText("Titre:");

        jTextField_event_titre.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jTextField_event_titre.setPreferredSize(new java.awt.Dimension(730, 26));
        jTextField_event_titre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField_event_titreMouseClicked(evt);
            }
        });

        jLabel_events_desc.setText("Description:");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea_event_desc.setLineWrap(true);
        jTextArea_event_desc.setWrapStyleWord(true);
        jTextArea_event_desc.setBorder(null);
        jTextArea_event_desc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea_event_descMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTextArea_event_desc);

        jTextField_event_url.setPreferredSize(new java.awt.Dimension(730, 26));
        jTextField_event_url.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField_event_urlMouseClicked(evt);
            }
        });

        jLabel_events_url.setText("Hyperlien:");

        javax.swing.GroupLayout jPanel_bottom_eventLayout = new javax.swing.GroupLayout(jPanel_bottom_event);
        jPanel_bottom_event.setLayout(jPanel_bottom_eventLayout);
        jPanel_bottom_eventLayout.setHorizontalGroup(
            jPanel_bottom_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottom_eventLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel_bottom_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel_events_title)
                    .addComponent(jLabel_events_desc)
                    .addComponent(jTextField_event_titre, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTextField_event_url, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel_events_url))
                .addGap(15, 15, 15))
        );
        jPanel_bottom_eventLayout.setVerticalGroup(
            jPanel_bottom_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_bottom_eventLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel_events_title)
                .addGap(4, 4, 4)
                .addComponent(jTextField_event_titre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_events_desc)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel_events_url)
                .addGap(4, 4, 4)
                .addComponent(jTextField_event_url, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel_bottomLayout = new javax.swing.GroupLayout(jPanel_bottom);
        jPanel_bottom.setLayout(jPanel_bottomLayout);
        jPanel_bottomLayout.setHorizontalGroup(
            jPanel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_bottomLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel_bottom_event, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel_bottomLayout.setVerticalGroup(
            jPanel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottomLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel_bottom_event, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jPanel_bottom_toolbar.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_bottom_toolbar.setPreferredSize(new java.awt.Dimension(800, 60));
        jPanel_bottom_toolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        jButton_sauvegarder.setText("Sauvegarder");
        jButton_sauvegarder.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_sauvegarder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_sauvegarderActionPerformed(evt);
            }
        });
        jPanel_bottom_toolbar.add(jButton_sauvegarder);

        jButton_annuler.setText("Annuler");
        jButton_annuler.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_annuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_annulerActionPerformed(evt);
            }
        });
        jPanel_bottom_toolbar.add(jButton_annuler);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_header, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel_bottom_toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel_bottom_toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Bouton de sauvegarde
     * @param evt l'action produite
     */
    private void jButton_sauvegarderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_sauvegarderActionPerformed
        this.saveEvent();
        this.dispose();
    }//GEN-LAST:event_jButton_sauvegarderActionPerformed

    /**
     * Bouton pour annuler
     * @param evt l'action produite
     */
    private void jButton_annulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_annulerActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton_annulerActionPerformed
    
    /**
     * Bouton pour annuler la dernière commande et conserver une trace pour la refaire
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_undoActionPerformed
        //annule la commande en prenant le premier item de la pile annulerStack
        Event_Command command = annulerStack.pop();
        //conserve les dernières entrées modifiées
        command.undo(jTextField_event_titre.getText(), jTextArea_event_desc.getText(), jTextField_event_url.getText());
        //ajoute la commande annulerStack dans la pile refaireStack pour conserver une trace
        refaireStack.push(command);
        //contrôle l'accessibilité des boutons undo et redo
        this.buttonAccess();
    }//GEN-LAST:event_jButton_header_toolbar_undoActionPerformed

    /**
     * Bouton pour refaire la dernière commande et conserver une trace pour l'annuler
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_redoActionPerformed
        //refait la commande en prenant le premier item de la pile refaireStack
        Event_Command command = refaireStack.pop();
        //conserve les dernières entrées modifiées
        command.redo(jTextField_event_titre.getText(), jTextArea_event_desc.getText(), jTextField_event_url.getText());
        //ajoute la commande refaireStack dans la pile annulerStack pour conserver une trace
        annulerStack.push(command);
        //contrôle l'accessibilité des boutons undo et redo        
        this.buttonAccess();
    }//GEN-LAST:event_jButton_header_toolbar_redoActionPerformed

    /**
     * Conserve une trace du titre une fois le champs titre cliqué, 
     * @param evt l'action produite
     */
    private void jTextField_event_titreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField_event_titreMouseClicked
        this.keepTrace();
    }//GEN-LAST:event_jTextField_event_titreMouseClicked

    /**
     * Conserve une trace de la description une fois le champs description cliqué,
     * @param evt l'action produite
     */    
    private void jTextArea_event_descMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea_event_descMouseClicked
        this.keepTrace();
    }//GEN-LAST:event_jTextArea_event_descMouseClicked

    /**
     * Conserve une trace de l'hyperlien une fois le champs hyperlien cliqué,
     * @param evt l'action produite
     */    
    private void jTextField_event_urlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField_event_urlMouseClicked
        this.keepTrace();
    }//GEN-LAST:event_jTextField_event_urlMouseClicked

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_annuler;
    private javax.swing.JButton jButton_header_toolbar_redo;
    private javax.swing.JButton jButton_header_toolbar_undo;
    private javax.swing.JButton jButton_sauvegarder;
    private javax.swing.JLabel jLabel_events_desc;
    private javax.swing.JLabel jLabel_events_title;
    private javax.swing.JLabel jLabel_events_url;
    private javax.swing.JLabel jLabel_header_title;
    private javax.swing.JLabel jLabel_redo;
    private javax.swing.JLabel jLabel_undo;
    private javax.swing.JPanel jPanel_bottom;
    private javax.swing.JPanel jPanel_bottom_event;
    private javax.swing.JPanel jPanel_bottom_toolbar;
    private javax.swing.JPanel jPanel_header;
    private javax.swing.JPanel jPanel_header_toolbar;
    private javax.swing.JPanel jPanel_redo;
    private javax.swing.JPanel jPanel_undo;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea_event_desc;
    private javax.swing.JTextField jTextField_event_titre;
    private javax.swing.JTextField jTextField_event_url;
    // End of variables declaration//GEN-END:variables

    /**
     * Ajoute un objet à la liste d'observateurs de nouvelles
     * @param observer l'observateur
     */
    @Override
    public void addEventObserver(Event_Observer observer) {
        observers.add(observer);
    }

    /**
     * Retire un objet de la liste d'observateurs de nouvelles
     * @param observer l'observateur
     */
    @Override
    public void removeEventObserver(Event_Observer observer) {
        // should probably open an informative window here
        observers.remove(observer);
    }

    /**
     * Notifie les observateurs de nouvelles (en l'occurence le contrôleur)
     * @param event la nouvelle
     * @param index la position de la nouvelle dans le fil de nouvelles
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 = top | 4 = actual | 5 = précédente | 6 = suivante
     */
    @Override
    public void notifyEventObserver(Model_events event, int index, int selection) {
        for(Event_Observer observer : observers) {            
            observer.updateController(event, index, selection);
        }
    }   
}
