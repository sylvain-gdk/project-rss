/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.util.ArrayList;

/**
 * Interface de la source des observateurs de nouvelles
 * @author Sylvain
 */
public interface Event_Subject {
    public void addEventObserver(Event_Observer observer);
    public void removeEventObserver(Event_Observer observer);
    public void notifyEventObserver(Model_events event, int index, int selection);
}
