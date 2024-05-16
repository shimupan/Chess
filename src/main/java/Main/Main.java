package Main;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import Game.Game;
import Util.CONSTANTS;
import Util.Enums.PlayerType;;


public class Main {

    public static JButton resetButton;
    public static void main(String[] args) {
        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
    
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
    
        Game g = Game.getInstance();
        g.init("", PlayerType.AI, PlayerType.AI);
        g.initGUI();
        g.setBounds(0, 0, 800, 800);
    
        resetButton.setBounds(850, 700, 200, 50);
    
        layeredPane.add(g, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(resetButton, JLayeredPane.PALETTE_LAYER);
    
        window.add(layeredPane);
        window.pack();
    
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    
        g.start();
    }
}

