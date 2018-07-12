/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

/**
 * Interface de commandes undo et redo
 * @author Sylvain
 */
public interface Event_Command {
    public void undo(String title, String desc, String url);    
    public void redo(String title, String desc, String url);    
}
