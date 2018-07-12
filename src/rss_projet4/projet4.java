/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Classe Main pour exécuter le programme
 * @author Sylvain
 */
public class projet4 {
    /**
     * Débute le programme en construisant la vue du fil de nouvelles
     * @param args the command line arguments
     */
    public static void main(String[] args){
        try {
            //applique "Nimbus" comme visuel du programme 
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        Controller controller = new Controller();  
    }    
}
