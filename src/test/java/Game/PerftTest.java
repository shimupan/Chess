package Game;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Main.Main;
import Util.CONSTANTS;
import Util.Enums;

public class PerftTest {
    
    private static Game g;
    private static JFrame window;
    Enums.PlayerType Human = Enums.PlayerType.Human;
    Enums.PlayerType AI = Enums.PlayerType.AI;

    private void sleep(int ms) {
        try {
            // Keep the test running for 10 minutes or until the user manually closes the GUI
            Thread.sleep(ms); // time in milliseconds
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

    /* 
    @Test
    public void DefaultBoardPerft() {
        Main.main(new String[]{"", "Human", "Human"});
        sleep(600000);
    }
    */

    @Test
    public void DefaultBoardPerft() {
        g.init("", AI, AI);
        g.perft(3, CONSTANTS.WHITE);
        window.setVisible(true);
        sleep(60000);
    }
}
