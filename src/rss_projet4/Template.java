/*
 * Ce programme permet d'importer un fil de nouvelles pour ensuite consulter ses nouvelles.
 * Il permet égallement d'exporter le fil de nouvelles dans un fichier XML et
 * de créer, supprimer ou modifier ses nouvelles.
 */
package rss_projet4;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *  Classe abstraite pour ajuster la largeur des vues
 * @author sylvain
 */
public abstract class Template extends javax.swing.JFrame {
    
    /**
     * Ajuste le cadre de la vue par rapport aux titres
     * @param titleWidth la largeur du titre de la nouvelle
     */
    final void resizeFrame(int titleWidth){
        if(titleWidth > 80){
            this.setPreferredSize(new Dimension(800 + (titleWidth(titleWidth)), 600));
            this.pack();
        }
        this.setLocationRelativeTo(null); 
        this.setVisible(true);
    }
    
    /**
     * Applique l'icône à afficher à côté d'un bouton dans un label
     * @param label le label du bouton
     * @param panel le panel du bouton
     * @param url le chemin de l'icône à afficher
     */
    final void setLabelIcon(JLabel label, JPanel panel, String url){
        ImageIcon icon = new ImageIcon(url);         
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(panel.getWidth(), panel.getHeight(),  java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        label.setIcon(icon);
    }
    
    /**
     * Applique l'icône à afficher dans un bouton
     * @param button le bouton
     * @param panel le panel du bouton
     * @param url le chemin de l'icône à afficher
     */
    final void setButtonIcon(JButton button, JPanel panel, String url){
        ImageIcon icon = new ImageIcon(url);         
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(panel.getWidth(), panel.getHeight(),  java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        button.setIcon(icon);
    }       
    
    /**
     * Retourne la largeur du titre avec un ajustement selon la vue
     * @param titleWidth la largeur du titre
     * @return la largeur du titre ajusté
     */
    abstract int titleWidth(int titleWidth);
}
