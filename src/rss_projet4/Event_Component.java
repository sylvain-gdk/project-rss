/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

/**
 * Interface composantes de visibilité et d'accessibilité des vues
 * @author Sylvain
 */
public interface Event_Component {
    public void activate();
    public void deactivate();
    public void activeState(int state);
    public void notActiveState(int state);    
}
