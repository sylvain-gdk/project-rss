/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Classe pour manipuler la visibilité et l'accessibilité des vues de type Leaf
 * @author Sylvain
 */
public class Event_Leaf extends WindowAdapter implements Event_Component{

     Component component;
     int state;
     
    /**
     * Construit un objet leaf
     * @param component la vue
     * @param state le statut de la vue (1 = visibilité | 2 = accessibilité)
     */
    public Event_Leaf(Component component, int state) {
        this.component = component;
        this.state = state;
    }
    
    /**
     * Processus d'activation (en rendant une fenêtre visible ou active)
     */
    @Override
    public void activate() {
        activeState(state);
    }

    /**
     * Processus de désactivation (en rendant une fenêtre invisible ou non-active)
     */
    @Override
    public void deactivate() {
        notActiveState(state);
    }

    /**
     * Désactive le processus à l'ouverture d'une fenêtre de type Leaf
     * @param e l'action produite
     */    
    @Override
    public void windowOpened(WindowEvent e) {
        deactivate();
    }
    
    /**
     * Active le processus à la fermeture d'une fenêtre de type Leaf
     * @param e l'action produite
     */
    @Override
    public void windowClosed(WindowEvent e) {
        activate();
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
