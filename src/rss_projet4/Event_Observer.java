/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.util.ArrayList;

/**
 * Interface d'observateurs de nouvelles
 * @author Sylvain
 */
public interface Event_Observer {
    public void updateController(Model_events event, int index, int selection);
}
