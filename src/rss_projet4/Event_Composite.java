/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Classe pour manipuler la visibilité et l'accessibilité des vues de type Composite
 * @author Sylvain
 */
public class Event_Composite extends WindowAdapter implements Event_Component{
    
    //Liste de composantes
    private ArrayList<Event_Component> components = new ArrayList();
    
    Component component; //la composante (la vue)
    int state; //l'état

    /**
     * Construit un objet Composite
     * @param component la vue
     * @param state le statut de la vue (1 = visibilité | 2 = accessibilité)
     */
    public Event_Composite(Component component, int state) {
        this.component = component;
        this.state = state;
    }
    
    /**
     * Ajoute une composante de vue à la liste
     * @param component la vue
     */
    public void addComponent(Event_Component component) {
        components.add(component);
    }
    
    /**
     * Retire une composante de vue de la liste
     * @param component la vue
     */    public void removeComponent(Event_Component component) {
        components.remove(component);
    }    

    /**
     * Processus d'activation (en rendant les fenêtres visibles ou active)
     */
    @Override
    public void activate() {
        for(Event_Component item : components) {
            item.activeState(state);
        }
        //System.out.println("ouvre une fenêtre.");           
    }

    /**
     * Processus de désactivation (en rendant les fenêtres invisibles ou non-active)
     */
    @Override
    public void deactivate() {
        for(Event_Component item : components) {
            item.notActiveState(state);
        } 
        //System.out.println("Ferme une fenêtre.");           
    }

    /**
     * Désactive le processus à l'ouverture d'une fenêtre de type Composite
     * @param e l'action produite
     */ 
    @Override
    public void windowOpened(WindowEvent e) {
        deactivate();
        component.setVisible(true);       
    }
    
    /**
     * Active le processus à la fermeture d'une fenêtre de type Composite
     * @param e l'action produite
     */ 
    @Override
    public void windowClosed(WindowEvent e) {
        activate();
        component.setVisible(false);
    }   

    /**
     * États du processus d'activation actif
     * @param state rend la fenêtre soit 1 = visible ou 2 = active
     */
    @Override
    public void activeState(int state) {
        if(state == 1)
            component.setVisible(true);
        else if(state == 2)
            component.setEnabled(true);
    }

    /**
     * États du processus de désactivation non-actif
     * @param state rend la fenêtre soit 1 = invisible ou 2 = non-active
     */
    @Override
    public void notActiveState(int state) {
        if(state == 1)
            component.setVisible(false);
        else if(state == 2)
            component.setEnabled(false);
    }
}
