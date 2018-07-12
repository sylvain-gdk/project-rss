/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.util.ArrayList;

/**
 * Interface d'observateur du contrôleur
 * @author Sylvain
 */
public interface Controller_Observer {
    public void updateViews(ArrayList<Model_events> eventsList, int index, int selection);
}
