/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe pour afficher la nouvelle
 * @author sylvain
 */
public class Event_eventJFrame extends Template implements Event_Subject, Controller_Observer{

    private final ArrayList<Event_Observer> observers; //liste des observateurs de nouvelles     

    private final Controller eventsList; //accède au contrôleur
    private final Feeds_feedsJFrame feedsJFrame; //accède à la vue du fil de nouvelles  
    private final Event_Composite eventComposite; //accède au composite de la vue de nouvelle
    
    private int index; //la position de la nouvelle dans le fil de nouvelles
    private String datePub; //la date de la nouvelle
    private boolean favori; //le statut favori
    private boolean lu = true; //le statut lu
    private boolean favoriMod = false; //le statut favori modifié
    private boolean luMod = false; //le statut lu modifié

    
    //mise en forme de l'affichage de la date    
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("en", "US"));
    
    /**
     * Constructeur de la vue_de la nouvelle
     * @param eventsList le fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     * @param feedsJFrame la vue du fil de nouvelles
     */
    public Event_eventJFrame(Controller eventsList, int index, Feeds_feedsJFrame feedsJFrame) {
        this.observers = new ArrayList();
        this.eventsList = eventsList;
        this.index = index;
        this.feedsJFrame = feedsJFrame;
        
        initComponents();
                
        //déclare feedsJFrame comme Leaf et applique un écouteur sur la fenêtre feedsJFrame
        Event_Leaf feedsLeaf = new Event_Leaf(feedsJFrame, 1);
        feedsJFrame.addWindowListener(feedsLeaf); 
        //déclare eventJFrame comme Composite et applique un écouteur sur la fenêtre eventJFrame
        eventComposite = new Event_Composite(this, 1);
        this.addWindowListener(eventComposite);        
        //ajoute feedsLeaf à la liste de eventComposite
        eventComposite.addComponent(feedsLeaf);        
        
        //applique la nouvelle dans la vue
        this.setEvent(eventsList, index);
               
        //affiche une image à côté d'un bouton
        this.setLabelIcon(jLabel_retour, jPanel_retour, "./src/images/retour.png");
        this.setLabelIcon(jLabel_modifier, jPanel_modifier, "./src/images/modifier.png");
        this.setLabelIcon(jLabel_supprimer, jPanel_supprimer, "./src/images/supprimer.png");       
        this.setLabelIcon(jLabel_lu, jPanel_lu, "./src/images/lu.png");
        jButton_header_toolbar_lu.setText("Marquer non-lu");
        jButton_header_toolbar_lu.setBorder(null);
        if(favori == true)
            this.setLabelIcon(jLabel_favori, jPanel_favori, "./src/images/favori.png");
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
     * Applique le statut lu ou non-lu en ajustant le texte et l'icône
     */
    private void setLuState(){
        if(lu == false){
            setLabelIcon(jLabel_lu, jPanel_lu, "./src/images/lu.png");
            jButton_header_toolbar_lu.setText("Marquer non-lu");
            jButton_header_toolbar_lu.setBorder(null);            
            this.lu = true;
        }
        else {
            setLabelIcon(jLabel_lu, jPanel_lu, "");
            jButton_header_toolbar_lu.setText("Marquer lu");
            jButton_header_toolbar_lu.setBorder(null); 
            this.lu = false;
            setLabelIcon(jLabel_favori, jPanel_favori, "");
            this.favori = false;
        }
        this.luMod = true;
    }
    
    /**
     * Applique le statut favori en ajustant le texte et l'icône
     */
    private void setFavoriState(){
        if(favori == false){
            setLabelIcon(jLabel_favori, jPanel_favori, "./src/images/favori.png");
            favori = true;
        }
        else {            
            setLabelIcon(jLabel_favori, jPanel_favori, "");
            favori = false;
        }
        this.favoriMod = true;
    }
    
    /**
     * Accessibilité des boutons de navigation
     */
    private void buttonAccess(){
        //si le fil de nouvelle est au début, ajuster l'accessibilité du bouton en conséquences
        if(index > 0){
            this.setButtonIcon(jButton_navigate_backward, jPanel_navigate_backward, "./src/images/backward.png");
            jButton_navigate_backward.setEnabled(true);
        }else{
            this.setButtonIcon(jButton_navigate_backward, jPanel_navigate_backward, "./src/images/backward_off.png"); 
            jButton_navigate_backward.setEnabled(false);
        }
        //si le fil de nouvelle est à la fin, ajuster l'accessibilité du bouton en conséquences
        if(index < eventsList.getEventList().size()-1){
            this.setButtonIcon(jButton_navigate_forward, jPanel_navigate_forward, "./src/images/forward.png");
            jButton_navigate_forward.setEnabled(true);
        }else{
            this.setButtonIcon(jButton_navigate_forward, jPanel_navigate_forward, "./src/images/forward_off.png"); 
            jButton_navigate_forward.setEnabled(false);
        }     
    }    
        
    
    /**
     * Applique la nouvelle dans la vue
     * @param eventsList le fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     */
    protected final void setEvent(Controller eventsList, int index){
        this.datePub = eventsList.getEventList().get(index).getDatePub();
        this.favori = eventsList.getEventList().get(index).getFavori();
        if(favori){
            setLabelIcon(jLabel_favori, jPanel_favori, "./src/images/favori.png");
        }
        else {            
            setLabelIcon(jLabel_favori, jPanel_favori, "");
        }        
        this.lu = eventsList.getEventList().get(index).getLu();
        if(!lu){
            this.lu = true;
            this.luMod = true;
        }
        this.index = index;
        //si le titre est trop long, on coupe
        if(eventsList.getEventList().get(index).getTitre().length() > 80)
            jLabel_header_title.setText(eventsList.getEventList().get(index).getTitre().substring(0, 80) + "...");  
        else jLabel_header_title.setText(eventsList.getEventList().get(index).getTitre());        
        jTextField_event_date.setText(eventsList.getEventList().get(index).getDatePub()); 
        jTextField_event_titre.setText(eventsList.getEventList().get(index).getTitre());  
        jTextArea_event_desc.setText(eventsList.getEventList().get(index).getDescription());
        jTextArea_event_desc.setCaretPosition(0);
        jTextField_event_url.setText(eventsList.getEventList().get(index).getURL());        

        //ajustement des flèches de navigation en rapport avec la position dans le fil de nouvelle
        this.buttonAccess();
    }
    
    private void updateEvent(ArrayList<Model_events> eventsList, int index){
        this.datePub = eventsList.get(index).getDatePub();
        this.favori = eventsList.get(index).getFavori();
        this.lu = eventsList.get(index).getLu();
        this.index = index;
        //si le titre est trop long, on coupe
        if(eventsList.get(index).getTitre().length() > 80)
            jLabel_header_title.setText(eventsList.get(index).getTitre().substring(0, 80) + "...");  
        else jLabel_header_title.setText(eventsList.get(index).getTitre());        
        jTextField_event_date.setText(eventsList.get(index).getDatePub()); 
        jTextField_event_titre.setText(eventsList.get(index).getTitre());  
        jTextArea_event_desc.setText(eventsList.get(index).getDescription());
        jTextArea_event_desc.setCaretPosition(0);
        jTextField_event_url.setText(eventsList.get(index).getURL());
    }
    
    /**
     * Sauvegarde les modifications de la nouvelle
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 = top | 4 = actual | 5 = précédente | 6 = suivante
     */
    private void saveEvent(int selection){
        //applique les modifications sans changer la date de publication
        if(luMod | favoriMod){
            Model_events modEvent = new Model_events();
            modEvent.setTitre(jTextField_event_titre.getText());
            modEvent.setDescription(jTextArea_event_desc.getText());
            try {
                modEvent.setDatePub(sdf.parse(datePub));
            } catch (ParseException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            modEvent.setURL(jTextField_event_url.getText());
            modEvent.setLu(lu);
            modEvent.setFavori(favori);

            this.notifyEventObserver(modEvent, index, selection);
        }
        //transmet simplement l'information actuel pour une mise à jour du tableau
        else 
           this.notifyEventObserver(eventsList.getEventList().get(index), index, selection);
    }
    
    /**
     * Ouvre l'hyperlien de la nouvelle dans un browser
     */
    private void openBrowser(){
        try{
            URI url = new URI(jTextField_event_url.getText());
            Desktop dt = Desktop.getDesktop();
            dt.browse(url.resolve(url));
        }catch(URISyntaxException | IOException ex){
            Logger.getLogger(Event_eventJFrame.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel_eventMain = new javax.swing.JPanel();
        jPanel_header = new javax.swing.JPanel();
        jLabel_header_title = new javax.swing.JLabel();
        jPanel_header_toolbar = new javax.swing.JPanel();
        jButton_header_toolbar_retour = new javax.swing.JButton();
        jButton_header_toolbar_lu = new javax.swing.JButton();
        jButton_header_toolbar_fav = new javax.swing.JButton();
        jButton_header_toolbar_mod = new javax.swing.JButton();
        jButton_header_toolbar_supp = new javax.swing.JButton();
        jPanel_retour = new javax.swing.JPanel();
        jLabel_retour = new javax.swing.JLabel();
        jPanel_lu = new javax.swing.JPanel();
        jLabel_lu = new javax.swing.JLabel();
        jPanel_favori = new javax.swing.JPanel();
        jLabel_favori = new javax.swing.JLabel();
        jPanel_modifier = new javax.swing.JPanel();
        jLabel_modifier = new javax.swing.JLabel();
        jPanel_supprimer = new javax.swing.JPanel();
        jLabel_supprimer = new javax.swing.JLabel();
        jPanel_bottom = new javax.swing.JPanel();
        jPanel_event = new javax.swing.JPanel();
        jTextField_event_date = new javax.swing.JTextField();
        jTextField_event_titre = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea_event_desc = new javax.swing.JTextArea();
        jTextField_event_url = new javax.swing.JTextField();
        jPanel_navigate = new javax.swing.JPanel();
        jPanel_navigate_backward = new javax.swing.JPanel();
        jButton_navigate_backward = new javax.swing.JButton();
        jPanel_navigate_forward = new javax.swing.JPanel();
        jButton_navigate_forward = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel_header.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_header_title.setFont(new java.awt.Font("Lucida Grande", 1, 20)); // NOI18N
        jLabel_header_title.setForeground(new java.awt.Color(0, 153, 204));
        jLabel_header_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_header_title.setText("Nouvelle");

        jPanel_header_toolbar.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_header_toolbar.setPreferredSize(new java.awt.Dimension(508, 33));

        jButton_header_toolbar_retour.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_retour.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_retour.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_retour.setText("Retour");
        jButton_header_toolbar_retour.setBorder(null);
        jButton_header_toolbar_retour.setBorderPainted(false);
        jButton_header_toolbar_retour.setContentAreaFilled(false);
        jButton_header_toolbar_retour.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_retour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_retourActionPerformed(evt);
            }
        });

        jButton_header_toolbar_lu.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_lu.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_lu.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_lu.setText("Marquer non-lu");
        jButton_header_toolbar_lu.setBorder(null);
        jButton_header_toolbar_lu.setBorderPainted(false);
        jButton_header_toolbar_lu.setContentAreaFilled(false);
        jButton_header_toolbar_lu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_lu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton_header_toolbar_lu.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton_header_toolbar_lu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_luActionPerformed(evt);
            }
        });

        jButton_header_toolbar_fav.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_fav.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_fav.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_fav.setText("Favori");
        jButton_header_toolbar_fav.setBorder(null);
        jButton_header_toolbar_fav.setBorderPainted(false);
        jButton_header_toolbar_fav.setContentAreaFilled(false);
        jButton_header_toolbar_fav.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_fav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_favActionPerformed(evt);
            }
        });

        jButton_header_toolbar_mod.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_mod.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_mod.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_mod.setText("Modifier");
        jButton_header_toolbar_mod.setBorder(null);
        jButton_header_toolbar_mod.setBorderPainted(false);
        jButton_header_toolbar_mod.setContentAreaFilled(false);
        jButton_header_toolbar_mod.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_mod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_modActionPerformed(evt);
            }
        });

        jButton_header_toolbar_supp.setBackground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_supp.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton_header_toolbar_supp.setForeground(new java.awt.Color(255, 255, 255));
        jButton_header_toolbar_supp.setText("Supprimer");
        jButton_header_toolbar_supp.setBorder(null);
        jButton_header_toolbar_supp.setBorderPainted(false);
        jButton_header_toolbar_supp.setContentAreaFilled(false);
        jButton_header_toolbar_supp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_header_toolbar_supp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_header_toolbar_suppActionPerformed(evt);
            }
        });

        jPanel_retour.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_retour.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_retour.setLayout(new java.awt.BorderLayout());

        jLabel_retour.setIconTextGap(0);
        jLabel_retour.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_retour.add(jLabel_retour, java.awt.BorderLayout.CENTER);

        jPanel_lu.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_lu.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_lu.setLayout(new java.awt.BorderLayout());

        jLabel_lu.setIconTextGap(0);
        jLabel_lu.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_lu.add(jLabel_lu, java.awt.BorderLayout.CENTER);

        jPanel_favori.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_favori.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_favori.setLayout(new java.awt.BorderLayout());

        jLabel_favori.setIconTextGap(0);
        jLabel_favori.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_favori.add(jLabel_favori, java.awt.BorderLayout.CENTER);

        jPanel_modifier.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_modifier.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_modifier.setLayout(new java.awt.BorderLayout());

        jLabel_modifier.setIconTextGap(0);
        jLabel_modifier.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_modifier.add(jLabel_modifier, java.awt.BorderLayout.CENTER);

        jPanel_supprimer.setBackground(new java.awt.Color(0, 153, 204));
        jPanel_supprimer.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_supprimer.setLayout(new java.awt.BorderLayout());

        jLabel_supprimer.setIconTextGap(0);
        jLabel_supprimer.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel_supprimer.add(jLabel_supprimer, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel_header_toolbarLayout = new javax.swing.GroupLayout(jPanel_header_toolbar);
        jPanel_header_toolbar.setLayout(jPanel_header_toolbarLayout);
        jPanel_header_toolbarLayout.setHorizontalGroup(
            jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel_retour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_retour)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_lu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_lu)
                .addGap(25, 25, 25)
                .addComponent(jPanel_favori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_fav)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel_modifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_mod)
                .addGap(25, 25, 25)
                .addComponent(jPanel_supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_header_toolbar_supp)
                .addGap(25, 25, 25))
        );
        jPanel_header_toolbarLayout.setVerticalGroup(
            jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_header_toolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_modifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_favori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_lu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel_retour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_header_toolbar_retour)
                    .addComponent(jPanel_supprimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_header_toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_header_toolbar_supp)
                        .addComponent(jButton_header_toolbar_mod)
                        .addComponent(jButton_header_toolbar_fav)
                        .addComponent(jButton_header_toolbar_lu)))
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
            .addComponent(jPanel_header_toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        jPanel_headerLayout.setVerticalGroup(
            jPanel_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_headerLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel_header_title, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel_header_toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel_bottom.setBackground(new java.awt.Color(255, 255, 255));

        jPanel_event.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_event.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        jTextField_event_date.setEditable(false);
        jTextField_event_date.setFont(new java.awt.Font("Lucida Grande", 2, 12)); // NOI18N
        jTextField_event_date.setBorder(null);

        jTextField_event_titre.setEditable(false);
        jTextField_event_titre.setFont(new java.awt.Font("Lucida Grande", 1, 15)); // NOI18N
        jTextField_event_titre.setBorder(null);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea_event_desc.setEditable(false);
        jTextArea_event_desc.setLineWrap(true);
        jTextArea_event_desc.setRows(5);
        jTextArea_event_desc.setWrapStyleWord(true);
        jTextArea_event_desc.setAutoscrolls(false);
        jTextArea_event_desc.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextArea_event_desc.setMargin(new java.awt.Insets(0, -5, 0, 0));
        jScrollPane1.setViewportView(jTextArea_event_desc);

        jTextField_event_url.setEditable(false);
        jTextField_event_url.setForeground(new java.awt.Color(0, 102, 204));
        jTextField_event_url.setBorder(null);
        jTextField_event_url.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTextField_event_url.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField_event_urlMouseClicked(evt);
            }
        });

        jPanel_navigate.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_navigate.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        jPanel_navigate_backward.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_navigate_backward.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_navigate_backward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel_navigate_backwardMouseClicked(evt);
            }
        });

        jButton_navigate_backward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backward.png"))); // NOI18N
        jButton_navigate_backward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_navigate_backward.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backward_off.png"))); // NOI18N
        jButton_navigate_backward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_navigate_backwardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_navigate_backwardLayout = new javax.swing.GroupLayout(jPanel_navigate_backward);
        jPanel_navigate_backward.setLayout(jPanel_navigate_backwardLayout);
        jPanel_navigate_backwardLayout.setHorizontalGroup(
            jPanel_navigate_backwardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_navigate_backwardLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton_navigate_backward, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel_navigate_backwardLayout.setVerticalGroup(
            jPanel_navigate_backwardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton_navigate_backward, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel_navigate.add(jPanel_navigate_backward);

        jPanel_navigate_forward.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_navigate_forward.setPreferredSize(new java.awt.Dimension(15, 15));
        jPanel_navigate_forward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel_navigate_forwardMouseClicked(evt);
            }
        });

        jButton_navigate_forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/forward.png"))); // NOI18N
        jButton_navigate_forward.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton_navigate_forward.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/images/forward_off.png"))); // NOI18N
        jButton_navigate_forward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_navigate_forwardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_navigate_forwardLayout = new javax.swing.GroupLayout(jPanel_navigate_forward);
        jPanel_navigate_forward.setLayout(jPanel_navigate_forwardLayout);
        jPanel_navigate_forwardLayout.setHorizontalGroup(
            jPanel_navigate_forwardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_navigate_forwardLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButton_navigate_forward, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel_navigate_forwardLayout.setVerticalGroup(
            jPanel_navigate_forwardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_navigate_forwardLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButton_navigate_forward, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel_navigate.add(jPanel_navigate_forward);

        javax.swing.GroupLayout jPanel_eventLayout = new javax.swing.GroupLayout(jPanel_event);
        jPanel_event.setLayout(jPanel_eventLayout);
        jPanel_eventLayout.setHorizontalGroup(
            jPanel_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_eventLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField_event_url)
                    .addComponent(jScrollPane1)
                    .addComponent(jTextField_event_titre)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel_eventLayout.createSequentialGroup()
                        .addComponent(jTextField_event_date, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
            .addGroup(jPanel_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_eventLayout.createSequentialGroup()
                    .addContainerGap(651, Short.MAX_VALUE)
                    .addComponent(jPanel_navigate, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)))
        );
        jPanel_eventLayout.setVerticalGroup(
            jPanel_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_eventLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jTextField_event_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jTextField_event_titre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addGap(30, 30, 30)
                .addComponent(jTextField_event_url, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addGroup(jPanel_eventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_eventLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(jPanel_navigate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(433, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel_bottomLayout = new javax.swing.GroupLayout(jPanel_bottom);
        jPanel_bottom.setLayout(jPanel_bottomLayout);
        jPanel_bottomLayout.setHorizontalGroup(
            jPanel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottomLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel_event, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel_bottomLayout.setVerticalGroup(
            jPanel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_bottomLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel_event, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanel_eventMainLayout = new javax.swing.GroupLayout(jPanel_eventMain);
        jPanel_eventMain.setLayout(jPanel_eventMainLayout);
        jPanel_eventMainLayout.setHorizontalGroup(
            jPanel_eventMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_eventMainLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel_eventMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel_eventMainLayout.setVerticalGroup(
            jPanel_eventMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_eventMainLayout.createSequentialGroup()
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
            .addComponent(jPanel_eventMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_eventMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Bouton pour sauvegarder les modifications et retourner à la fenêtre précédente, en l'occurence la fenêtre du fil de nouvelles
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_retourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_retourActionPerformed
        this.saveEvent(4);
        this.dispose();
    }//GEN-LAST:event_jButton_header_toolbar_retourActionPerformed

    /**
     * bouton pour changer le statut lu
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_luActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_luActionPerformed
        this.setLuState();
    }//GEN-LAST:event_jButton_header_toolbar_luActionPerformed

    /**
     * Bouton pour changer le statut favori
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_favActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_favActionPerformed
        this.setFavoriState();
    }//GEN-LAST:event_jButton_header_toolbar_favActionPerformed

    /**
     * Bouton pour ouvrir une vue de modification de la nouvelle
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_modActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_modActionPerformed
        //construit une vue de modification de nouvelle
        Event_addModJDialog addModJDialog = new Event_addModJDialog(eventsList, index);        
        //ajoute le contrôleur à la liste d'observateur de addModJDialog
        addModJDialog.addEventObserver(eventsList);
        //ajuste la largeur de la vue par rapport au titre
        addModJDialog.resizeFrame(jTextField_event_titre.getText().length());
                
        //déclare addModJDialog comme Composite et applique un écouteur sur la fenêtre addModJDialog
        Event_Composite modComposite = new Event_Composite(addModJDialog, 1);
        addModJDialog.addWindowListener(modComposite);
        //ajoute eventComposite à la liste de modComposite
        modComposite.addComponent(eventComposite);         
    }//GEN-LAST:event_jButton_header_toolbar_modActionPerformed

    /**
     * Bouton pour supprimer la nouvelle
     * @param evt l'action produite
     */
    private void jButton_header_toolbar_suppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_header_toolbar_suppActionPerformed
        //construit une vue de confirmation de suppression
        Event_confirmSuppJDialog confirmJDialog = new Event_confirmSuppJDialog(eventsList, index, this);
        //ajoute le contrôleur à la liste d'observateurs de confirmJDialog
        confirmJDialog.addEventObserver(eventsList);
        //initialise la largeur de la vue
        confirmJDialog.initFrame();   
        
        //déclare confirmJDialog comme Composite et applique un écouteur sur la fenêtre confirmJDialog
        Event_Composite confirmComposite = new Event_Composite(confirmJDialog, 2);
        confirmJDialog.addWindowListener(confirmComposite);
        //ajoute eventComposite à la liste de confirmComposite
        confirmComposite.addComponent(eventComposite);         
    }//GEN-LAST:event_jButton_header_toolbar_suppActionPerformed

    /**
     * Ouvre l'hyperlien par un click de souris
     * @param evt l'action produite
     */
    private void jTextField_event_urlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField_event_urlMouseClicked
        this.openBrowser();
    }//GEN-LAST:event_jTextField_event_urlMouseClicked


    private void jPanel_navigate_backwardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel_navigate_backwardMouseClicked

    }//GEN-LAST:event_jPanel_navigate_backwardMouseClicked


    private void jPanel_navigate_forwardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel_navigate_forwardMouseClicked
           
    }//GEN-LAST:event_jPanel_navigate_forwardMouseClicked

    /**
     * Bouton de navigation pour sauvegarder les modifications et passer à la nouvelle précédente
     * @param evt l'action produite
     */    
    private void jButton_navigate_backwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_navigate_backwardActionPerformed
        this.saveEvent(5);
        if(index > 0)
            this.setEvent(eventsList, index-1);
    }//GEN-LAST:event_jButton_navigate_backwardActionPerformed

    /**
     * Bouton de navigation pour sauvegarder les modifications et passer à la nouvelle suivante
     * @param evt l'action produite
     */    
    private void jButton_navigate_forwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_navigate_forwardActionPerformed
        this.saveEvent(6);
        if(index < eventsList.getEventList().size()-1)
            this.setEvent(eventsList, index+1);
    }//GEN-LAST:event_jButton_navigate_forwardActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_header_toolbar_fav;
    private javax.swing.JButton jButton_header_toolbar_lu;
    private javax.swing.JButton jButton_header_toolbar_mod;
    private javax.swing.JButton jButton_header_toolbar_retour;
    private javax.swing.JButton jButton_header_toolbar_supp;
    private javax.swing.JButton jButton_navigate_backward;
    private javax.swing.JButton jButton_navigate_forward;
    private javax.swing.JLabel jLabel_favori;
    private javax.swing.JLabel jLabel_header_title;
    private javax.swing.JLabel jLabel_lu;
    private javax.swing.JLabel jLabel_modifier;
    private javax.swing.JLabel jLabel_retour;
    private javax.swing.JLabel jLabel_supprimer;
    private javax.swing.JPanel jPanel_bottom;
    private javax.swing.JPanel jPanel_event;
    private javax.swing.JPanel jPanel_eventMain;
    private javax.swing.JPanel jPanel_favori;
    private javax.swing.JPanel jPanel_header;
    private javax.swing.JPanel jPanel_header_toolbar;
    private javax.swing.JPanel jPanel_lu;
    private javax.swing.JPanel jPanel_modifier;
    private javax.swing.JPanel jPanel_navigate;
    private javax.swing.JPanel jPanel_navigate_backward;
    private javax.swing.JPanel jPanel_navigate_forward;
    private javax.swing.JPanel jPanel_retour;
    private javax.swing.JPanel jPanel_supprimer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea_event_desc;
    private javax.swing.JTextField jTextField_event_date;
    private javax.swing.JTextField jTextField_event_titre;
    private javax.swing.JTextField jTextField_event_url;
    // End of variables declaration//GEN-END:variables

    /**
     * Ajoute un objet dans liste d'observateurs de nouvelles
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
        observers.remove(observer);
    }

    /**
     * Notifie les observateurs de nouvelles (en l'occurence le contrôleur)
     * @param event la nouvelle
     * @param index l'index de la nouvelle
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 = top | 4 = actual | 5 = précédente | 6 = suivante
     */
    @Override
    public void notifyEventObserver(Model_events event, int index, int selection) {
        for(Event_Observer observer : observers) {
            observer.updateController(event, index, selection);
        }
    }

    /**
     * Pour être notifié de tout changement provenant de la source (en l'occurence le contrôleur)
     * @param eventsList le fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 = top | 4 = actual | 5 = précédente | 6 = suivante
     */
    @Override
    public void updateViews(ArrayList<Model_events> eventsList, int index, int selection) {
        if(index !=eventsList.size())
                this.updateEvent(eventsList, index);
    }
}
