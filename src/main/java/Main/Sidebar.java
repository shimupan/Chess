package Main;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLayeredPane;

import Util.CONSTANTS;

public class Sidebar {
    public static JButton resetButton;
    public static JButton undoButton;
    public static JButton player1TypeButton;
    public static JButton player2TypeButton;
    public static JLayeredPane sideBarPane;

    public static void init() {
        sideBarPane = new JLayeredPane();
        sideBarPane.setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));

        sideBarPane.add(Main.g, JLayeredPane.DEFAULT_LAYER);
        sideBarPane.add(resetButton, JLayeredPane.PALETTE_LAYER);
        sideBarPane.add(undoButton, JLayeredPane.PALETTE_LAYER);
    }
}
