/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Classe pour conserver une trace des entrées dans une nouvelle
 * @author Sylvain
 */
public class Event_CommandStore implements Event_Command{        
    private String titleBefore; //titre original
    private String titleAfter; //titre après modification
    private String descBefore; //description original
    private String descAfter; //description après modification
    private String urlBefore; //hyperlien original
    private String urlAfter; //hyperlien après modification
    private final JTextField jTextField_event_titre;
    private final JTextArea jTextArea_event_desc;
    private final JTextField jTextField_event_url;
   
    /**
     * Constructeur pour garder une trace des entrées originaux
     * @param title le titre de la nouvelle
     * @param desc la description de la nouvelle
     * @param url l'hyperlien de la nouvelle
     * @param jTextField_event_titre le champs du titre
     * @param jTextArea_event_desc le champs de la description
     * @param jTextField_event_url le champs de l'hyperlien
     */
    public Event_CommandStore(String title, String desc, String url, JTextField jTextField_event_titre, JTextArea jTextArea_event_desc, JTextField jTextField_event_url){
        this.titleBefore = title;
        this.descBefore = desc;
        this.urlBefore = url;
        this.jTextField_event_titre = jTextField_event_titre;
        this.jTextArea_event_desc = jTextArea_event_desc;
        this.jTextField_event_url = jTextField_event_url;
    }
    
    /**
     * Garde une trace du titre, description et hyperlien les plus récents
     * @param title le titre de la nouvelle 
     * @param desc la description de la nouvelle 
     * @param url l'hyperlien de la nouvelle 
     */
    protected void setAfter(String title, String desc, String url){
        this.titleAfter = title;
        this.descAfter = desc;
        this.urlAfter = url;
    }
    
    /**
     * Garde une trace du titre modifié
     * @param title le titre de la nouvelle 
     */
    private void setTitleAfter(String title) {
        this.titleAfter = title;
    }  

    /**
     * Retourne le titre précédent
     * @return le titre précédent
     */
    private String getTitleBefore(){
        return titleBefore;
    }

    /**
     * Retourne la description précédente
     * @return la description précédente
     */    
    private String getDescBefore(){
        return descBefore;
    }

    /**
     * Retourne l'hyperlien précédent
     * @return l'hyperlien précédent
     */    
    private String getUrlBefore(){
        return urlBefore;
    }
    
    /**
     * Retourne le titre suivant
     * @return le titre suivant
     */
    private String getTitleAfter(){
        return titleAfter;
    }

     /**
     * Retourne la description suivante
     * @return la description suivante
     */ 
    private String getDescAfter(){
        return descAfter;
    }

    /**
     * Retourne l'hyperlien suivant
     * @return l'hyperlien suivant
     */ 
    private String getUrlAfter(){
        return urlAfter;
    }

    /**
     * Garde une trace des entrées modifiées avant de revenir en arrière
     * @param title le titre de la nouvelle
     * @param desc la description de la nouvelle
     * @param url l'hyperlien de la nouvelle
     */
    @Override
    public void undo(String title, String desc, String url) {
        this.titleAfter = title;
        this.descAfter = desc;
        this.urlAfter = url;
        jTextField_event_titre.setText(getTitleBefore());
        jTextArea_event_desc.setText(getDescBefore());
        jTextField_event_url.setText(getUrlBefore());
    }

    /**
     * Garde une trace des entrées originaux avant de refaire la commande
     * @param title le titre de la nouvelle
     * @param desc la description de la nouvelle
     * @param url l'hyperlien de la nouvelle
     */
    @Override
    public void redo(String title, String desc, String url) {
        this.titleBefore = title;
        this.descBefore = desc;
        this.urlBefore = url;
        jTextField_event_titre.setText(getTitleAfter());
        jTextArea_event_desc.setText(getDescAfter());
        jTextField_event_url.setText(getUrlAfter());
    }

}
