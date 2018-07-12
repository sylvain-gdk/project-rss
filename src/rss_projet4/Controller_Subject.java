/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et 
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.util.ArrayList;

/**
 * Interface de la source des observateurs du contrôleur
 * @author Sylvain
 */
public interface Controller_Subject {
    public void addControllerObserver(Controller_Observer observer);
    public void removeControllerObserver(Controller_Observer observer);
    public void notifyControllerObserver(ArrayList<Model_events> eventsList, int index, int selection);
}
