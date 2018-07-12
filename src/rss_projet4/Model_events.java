/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Classe du modèle de nouvelle
 * @author Sylvain
 */
public class Model_events implements Comparable<Model_events> {  
    private Date pubDate; //date de publication
    private String titre; //titre de la nouvelle
    private String description; //description de la nouvelle
    private String url; //hyperlien de la nouvelle
    private boolean lu; //statut lu
    private boolean favori; //statut favori
    
    //mise en forme de l'affichage de la date
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("en", "US"));
    
    /**
     * Accesseur de date de publication
     * @return date de publication
     */
    public String getDatePub(){
        return sdf.format(pubDate);
    }    
    
    /**
     * Mutateur de date de publication
     * @param datePub date de publication
     */
    public void setDatePub(Date datePub){
        this.pubDate = datePub;
    }
    
    /**
     * Accesseur de titre de la nouvelle
     * @return titre de la nouvelle
     */
    public String getTitre(){
        return titre;
    }
    
    /**
     * Mutateur de titre de la nouvelle
     * @param titre titre de la nouvelle
     */
    public void setTitre(String titre){
        this.titre = titre;
    }
    
    /**
     * Accesseur de description de la nouvelle
     * @return description de la nouvelle
     */
    public String getDescription(){
        return description;
    }
    
    /**
     * Mutateur de description de la nouvelle
     * @param description description de la nouvelle
     */
    public void setDescription(String description){
        this.description = description;
    } 
    
    /**
     * Accesseur de l'hyperlien de la nouvelle
     * @return l'hyperlien de la nouvelle
     */
    public String getURL(){
        return url;
    }
    
    /**
     * Mutateur de l'hyperlien de la nouvelle
     * @param url l'hyperlien de la nouvelle
     */
    public void setURL(String url){
        this.url = url;
    } 
    
    /**
     * Accesseur de statut lu ou non-lu
     * @return statut lu ou non-lu
     */
    public boolean getLu(){
        return lu;
    }
    
    /**
     * Mutateur de statut lu ou non-lu
     * @param lu statut lu ou non-lu
     */
    public void setLu(boolean lu){
        this.lu = lu;
    } 
    
    /**
     * Accesseur de statut favori ou non
     * @return statut favori ou non
     */
    public boolean getFavori(){
        return favori;
    }
    
    /**
     * Mutateur de statut favori ou non
     * @param favori statut favori ou non
     */
    public void setFavori(boolean favori){
        this.favori = favori;
    }        

    /**
     * Compare la date avec celle passé en paramètre
     * @param event date de comparaison
     * @return la date est soit plus petite, plus grande ou identique
     */
    @Override
    public int compareTo(Model_events event) {
        return pubDate.compareTo(event.pubDate);
    }
}
