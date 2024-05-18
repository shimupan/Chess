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
    public static JButton undoButton;
    public static void main(String[] args) {
        // ARGS
        String fen = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1";
        PlayerType p1 = PlayerType.Human;
        PlayerType p2 = PlayerType.AI;
        if(args.length > 0) {
            fen = args[0];
            if(args[1].equals("AI")) {
                p1 = PlayerType.AI;
            }
            if(args[2].equals("AI")) {
                p2 = PlayerType.AI;
            }
        }


        // SETUP WINDO
        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
    
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
        
        // SETUP GAME
        Game g = Game.getInstance();
        g.init(fen, p1, p2);
        g.initGUI();
        g.setBounds(0, 0, 800, 800);
    
        // MORE WINDOW SETUP
        layeredPane.add(g, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(resetButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(undoButton, JLayeredPane.PALETTE_LAYER);
    
        window.add(layeredPane);
        window.pack();
    
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        // START
        g.start();
    }
}

