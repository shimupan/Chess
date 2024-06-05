package Main;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import Util.CONSTANTS;

public class Sidebar {
    // Buttons
    public static JButton resetButton;
    public static JButton undoButton;
    public static JButton player1TypeButton;
    public static JButton player2TypeButton;
    public static JLayeredPane sideBarPane;
    
    // Text
    public static JLabel turnLabel;
    public static JLabel player1TypeLabel;
    public static JLabel player2TypeLabel;

    public static void init() {
        sideBarPane = new JLayeredPane();
        Sidebar.sideBarPane.setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));

        // Button
        Sidebar.sideBarPane.add(Main.g, JLayeredPane.DEFAULT_LAYER);
        Sidebar.sideBarPane.add(resetButton, JLayeredPane.PALETTE_LAYER);
        Sidebar.sideBarPane.add(undoButton, JLayeredPane.PALETTE_LAYER);
        Sidebar.sideBarPane.add(player1TypeButton, JLayeredPane.PALETTE_LAYER);

        // Text
        turnLabel = new JLabel();
        turnLabel.setFont(new Font("default", Font.BOLD, 32));
        turnLabel.setBounds(860, 50, 200, 30);
        Sidebar.sideBarPane.add(turnLabel, JLayeredPane.PALETTE_LAYER);
        updateTurn();

        /* 
        player1TypeLabel = new JLabel();
        player1TypeLabel.setFont(new Font("default", Font.PLAIN, 16));
        player1TypeLabel.setBounds(860, 100, 200, 30);
        player1TypeLabel.setText("Player 1: " + Main.p1.toString());
        Sidebar.sideBarPane.add(player1TypeLabel, JLayeredPane.PALETTE_LAYER);

        player2TypeLabel = new JLabel();
        player2TypeLabel.setFont(new Font("default", Font.PLAIN, 16));
        player2TypeLabel.setBounds(860, 125, 200, 30);
        player2TypeLabel.setText("Player 2: " + Main.p2.toString());
        Sidebar.sideBarPane.add(player2TypeLabel, JLayeredPane.PALETTE_LAYER);
        */
    }

    public static void updateTurn() {
        String turn = (Main.g.currColor == CONSTANTS.WHITE) ? "White's turn" : "Black's turn";
        turnLabel.setText(turn);
    }
}
