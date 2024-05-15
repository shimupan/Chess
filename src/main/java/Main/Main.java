package Main;

import javax.swing.JFrame;

import Game.Game;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        Game g = Game.getInstance();
        g.init("");
        
        window.add(g);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        g.start();
    }
}

