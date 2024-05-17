package Game;

import org.junit.jupiter.api.Test;

import Util.Enums;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeAll;

public class GameTest {
    private static Game g;
    private static JFrame window;
    Enums.PlayerType Human = Enums.PlayerType.Human;
    Enums.PlayerType AI = Enums.PlayerType.AI;

    private void sleep(int ms) {
        try {
            // Keep the test running for 10 minutes or until the user manually closes the GUI
            Thread.sleep(600000); // time in milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    public static void setup() {
        window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        g = Game.getInstance();
        window.add(g);
        window.pack();
        window.setLocationRelativeTo(null);
    }

    // CASTLING BROKEN
    @Test
    public void AIvsAI() {
        g.init("", AI, AI);
        g.start();
        window.setVisible(true);
        sleep(60000);
    }

    // CASTLING BROKEN
    @Test
    public void CastleTest() {
        g.init("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", Human, Human);
        g.start();
        window.setVisible(true);
        sleep(60000);
    }

    // PROMOTION FOR AI BROKEN
    @Test
    public void PromotionTest() {
        g.init("8/3P4/8/8/K6k/8/3p4/8 w - - 0 1", Human, AI);
        g.start();
        window.setVisible(true);
        sleep(60000);
    }

    // ENPASSANT LOADING DOESNT WORK
    @Test
    public void EnPassantTest() {
        g.init("3k4/8/8/8/2pP4/8/8/3K4 b - d3 0 1", Human, Human);
        g.start();
        window.setVisible(true);
        sleep(60000);
    }

    @Test
    public void CheckMateTest() {
        g.init("k1K5/7Q/8/8/8/8/8/6B1 w - - 0 1", Human, Human);
        g.start();
        window.setVisible(true);
        sleep(60000);
    }

    @Test
    public void StaleMateTest() {
        g.init("4Q3/3N4/8/k3K3/5Q2/8/8/5Q2 w - - 0 1", Human, Human);
        g.start();
        window.setVisible(true);
        sleep(60000);
    }
}
