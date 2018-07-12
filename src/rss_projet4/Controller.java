/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Classe contrôleur qui sert d'intermédiaire entre le modèle et les vues
 * @author sylvain
 */
public class Controller implements Controller_Subject, Event_Observer{
    
    private final ArrayList<Controller_Observer> observers; //liste des observateurs du contrôleur     
    private final ArrayList<Model_events> eventsList; //Le fil de nouvelles
    
    private Model_events event; //accède au modèle (la nouvelle)
    private final Feeds_feedsJFrame feedsJFrame; //accède à la vue du fil de nouvelles
    private Event_eventJFrame eventJFrame; //accède à la vue de la nouvelle
    private Event_addModJDialog addModJDialog; //accède à la vue d'ajout et modification de la nouvelle
     
    private String feedTitle = "Fil de nouvelles"; //le titre du fil de nouvelles
    
    //mise en forme de l'affichage de la date
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("en", "US"));
    
    /**
     * Constructeur Controller - Construit et affichage un objet feedsJFrame (fil de nouvelles)
     */
    public Controller(){ 
        observers = new ArrayList();
        eventsList = new ArrayList();
        feedsJFrame = new Feeds_feedsJFrame(this);
        feedsJFrame.setLocationRelativeTo(null);
        feedsJFrame.setVisible(true); 
        //ajoute l'objet feedsJFrame à la liste d'observateur du contrôleur
        this.addControllerObserver(feedsJFrame);
    }     
    
    /**
     * Ajoute une nouvelle au fil de nouvelles
     * @param newEvent la nouvelle à ajouter
     */
    public void addEvent(Model_events newEvent){
        //ajoute la nouvelle à la liste
        eventsList.add(newEvent);
        //trie le fil de nouvelles (par date, par ordre décroissant)
        Collections.sort(eventsList, Collections.reverseOrder());
        //notifie les observateurs du contrôleur (en l'occurence, feedsJFrame)
        this.notifyControllerObserver(eventsList, 0, 1);
        
        //printInfo(newEvent, "new"); //imprime les informtions pour débugger
    }
    
    /**
     * Modifie une nouvelle existante du fil de nouvelles
     * @param modEvent la nouvelle à modifier
     * @param index la position de la nouvelle dans la liste
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 = top | 4 = actual | 5 = précédente | 6 = suivante
     */
    public void updateEventSameDate(Model_events modEvent, int index, int selection){
        //modifie la nouvelles dans la liste à l'index passé en paramètre sans faire de trie
        eventsList.set(index, modEvent);
        //notifie les observateurs du contrôleur (en l'occurence, feedsJFrame)
        this.notifyControllerObserver(eventsList, index, selection);
        
        //printInfo(modEvent, "update same"); //imprime les informtions pour débugger
    }
    
    /**
     * Modifie une nouvelle existante du fil de nouvelles
     * @param modEvent la nouvelle
     * @param index la position de la nouvelle dans la liste
     */
    public void updateEvent(Model_events modEvent, int index){
        //modifie la nouvelle dans la liste à l'index passé en paramètre avec un trie       
        eventsList.set(index, modEvent);
        //trie le fil de nouvelles par date, par ordre décroissant       
        Collections.sort(eventsList, Collections.reverseOrder());
        //notifie les observateurs du contrôleur (en l'occurence, feedsJFrame)
        this.notifyControllerObserver(eventsList, 0, 3);
        
        //printInfo(modEvent, "update new"); //imprime les informtions pour débugger
    }    
    
    /**
     * Supprime la nouvelle du fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     */
    public void removeEvent(int index){
        //retire la nouvelle du fil de nouvelles à l'index passé en paramètre        
        eventsList.remove(index);
        //notifie les observateurs du contrôleur (en l'occurence, feedsJFrame)
        this.notifyControllerObserver(eventsList, index, 2);
    }
    
    /**
     * Accède aux détails de la nouvelle
     * @return la nouvelle
     */
    public Model_events getEvent(){
        for(Model_events element : eventsList){
            element.getDatePub();
            element.getTitre();
            element.getDescription();
            element.getURL();
            element.getLu();
            element.getFavori();
        }
        return event;
    }
    
    /**
     * Accède au fil de nouvelles
     * @return le fil de nouvelles
     */
    public List<Model_events> getEventList(){
        return eventsList;
    }
    
    /**
     * Compare les titres pour éventuellement ajuster le cadre de la vue du fil de nouvelles selon le plus long titre
     * @return le titre le plus long
     */
    public int getlargestTitle(){
        int titleWidth = 0;
        for (int i = 0; i < eventsList.size(); i++) {
            if(eventsList.get(i).getTitre().length() > titleWidth)
                titleWidth = eventsList.get(i).getTitre().length();
        }
        return titleWidth;
    }
    
    /**
     * Importe un fil de nouvelles à partir d'un hyperlien de type RSS ou d'un fichier
     * @param url l'hyperlien du fil de nouvelles ou le fichier
     */
    public void importXML(String url){
        //création d'un objet SAXbuilder
        SAXBuilder builder = new SAXBuilder();    
        try { 
            //lecture des différent "tags" d'un fichier XML
            org.jdom2.Document readDoc = builder.build(url);
            
            Element root = readDoc.getRootElement();                                              
            Element channel = root.getChild("channel");
            
            //capture le titre du fil de nouvelles
            feedTitle = channel.getChildText("title");
            feedsJFrame.setFeedTitle(feedTitle);
            //parcours le fichier XML pour récupérer les informations dans le fil de nouvelles
            List<Element> items = channel.getChildren("item");
            for (int i = 0; i < items.size(); i++) {
                Model_events newEvent = new Model_events();
                newEvent.setTitre(items.get(i).getChildText("title"));
                newEvent.setDescription(items.get(i).getChildText("description"));
                newEvent.setDatePub(sdf.parse(items.get(i).getChildText("pubDate")));
                newEvent.setURL(items.get(i).getChildText("link"));
                newEvent.setFavori(false);
                newEvent.setLu(false);
                addEvent(newEvent);
            }            
            //réajuste le cadre de la vue selon la largeur des titres
            feedsJFrame.resizeFrame(getlargestTitle());
        } catch (JDOMException | IOException | ParseException ex) { 
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            this.notification("Une erreur s'est produite! (le fichier n'est peut-être pas compatible)", false);
        }   
    }
    
    /**
     * Exporte le fil de nouvelles dans un fichier XML
     * @param fileName le nom du fichier XML
     */
    public void exportXML(String fileName){
        //écriture des différent "tags" d'un fichier XML
        org.jdom2.Document writeDoc = new org.jdom2.Document();
        
        Element root = new Element("rss");
        writeDoc.setRootElement(root);
        
        Element channel = new Element("channel");
        root.addContent(channel);
        
        Element fTitle = new Element("title");
        fTitle.addContent(feedTitle);
        channel.addContent(fTitle);
        
        //parcour le fil de nouvelles pour récupérer les informations dans un fichier XML
        for (int i = 0; i < eventsList.size(); i++) {
            Element item = new Element("item");
            
            Element title = new Element("title");
            title.addContent(eventsList.get(i).getTitre());
            item.addContent(title);
            
            Element link = new Element("link");
            link.addContent(eventsList.get(i).getURL());
            item.addContent(link);            
           
            Element pubDate = new Element("pubDate");
            pubDate.addContent(eventsList.get(i).getDatePub());
            item.addContent(pubDate);      
            
            Element description = new Element("description");
            description.addContent(eventsList.get(i).getDescription());
            item.addContent(description);
           
            channel.addContent(item);
        }
        //formattage du fichier XML
        XMLOutputter xml = new XMLOutputter();
        xml.setFormat(Format.getPrettyFormat());

        try {
            xml.output(writeDoc, new FileOutputStream(new File(fileName)));
            this.notification("L'exportation a réussi.", true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            this.notification("Fichier introuvable!", false);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            this.notification("Une erreur d'écriture s'est produite!", false);
        }
    } 
        
    /**
     * Affiche une vue de notification
     * @param notif texte de notification
     * @param positiveResult le genre de message à afficher (true = confirme | false = avertissement)
     */
    public void notification(String notif, boolean positiveResult){
            Event_notificationJDialog notificationJDialog = new Event_notificationJDialog();
            notificationJDialog.setNotification(notif, positiveResult); 
            notificationJDialog.resizeFrame(notif.length());
    }
    
    /**
     * Imprime les infos pour debugger
     * @param event la nouvelle
     * @param method la méthode employé
     */
    public void printInfo(Model_events event, String method){
        System.out.println("Method: " + method);
        System.out.println("Index: " + eventsList.indexOf(event));
        System.out.println("Titre: " + event.getTitre());
        System.out.println("Date: " + event.getDatePub());
        System.out.println("Lu: " + event.getLu());
        System.out.println("Favori: " + event.getFavori());
        System.out.println("------------------------------\n");
    }

    /**
     * Pour être notifié de tout changement provenant de la source (en l'occurence une nouvelle)
     * @param event la nouvelle
     * @param index la position de la nouvelle dans le fil de nouvelles
     * @param selection sert à déterminer la rangée du tableau à sélectionner: 1 = top | 2 = none | 3 = top | 4 = actual | 5 = précédente | 6 = suivante
     */
    @Override
    public void updateController(Model_events event, int index, int selection) {
        switch(selection){
            case 1:{
                //ajoute une nouvelle
                this.addEvent(event); 
                this.notification("La nouvelle a été ajouté.", true);
                break;
            }
            case 2:{
                //retire une nouvelle
                this.removeEvent(index);  
                this.notification("La nouvelle a été supprimé.", true);
                break;
            }
            case 3:{
                //mise à jour d'une nouvelle
                this.updateEvent(event, index);
                this.notification("La nouvelle a été modifié.", true);
                break;
            }
            case 4:{
                //miseà jour d'une nouvelle dont la date n'est pas modifié
                this.updateEventSameDate(event, index, selection);
                break;
            }
            case 5:{
                //miseà jour d'une nouvelle dont la date n'est pas modifié
                //sélection prédente dans le tableau
               this.updateEventSameDate(event, index, selection);
                break;                
            }
            case 6:{
                //miseà jour d'une nouvelle dont la date n'est pas modifié
                //sélection suivante dans le tableau
                this.updateEventSameDate(event, index, selection);
                break;                
            }            
            default:               
                this.notifyControllerObserver(eventsList, index, 2);

        }
    }
    
    /**
     * Ajoute un objet à la liste d'observateurs du contrôleur
     * @param observer l'observateur
     */
    @Override
    public void addControllerObserver(Controller_Observer observer) {
        observers.add(observer);
    }

    /**
     * Retire un objet de la liste d'observateurs du contrôleur
     * @param observer l'observateur
     */
    @Override
    public void removeControllerObserver(Controller_Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifie les observateurs du contrôleur (en l'occurence, les vues feedsJFrame et eventsJFrame)
     * @param eventsList le fil de nouvelles
     * @param index la position de la nouvelle dans le fil de nouvelles
     * @param selection sert à déterminer la rangée du tableau à sélectionner
     */
    @Override
    public void notifyControllerObserver(ArrayList<Model_events> eventsList, int index, int selection) {
        for(Controller_Observer observer : observers) {
            observer.updateViews(eventsList, index, selection);
        }
    }

}