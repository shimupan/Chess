package Main;

import javax.swing.JFrame;

import Game.Game;
import Util.Enums;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        Game g = Game.getInstance();
        g.init("8/3P4/8/8/8/8/3p4/8 w - - 0 1", Enums.PlayerType.Human, Enums.PlayerType.Human);

        window.add(g);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        g.start();
    }
}

