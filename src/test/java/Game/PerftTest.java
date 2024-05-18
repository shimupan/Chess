package Game;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void DefaultBoardPerftDepth2() {
        g.init("", AI, AI);
        window.setVisible(true);
        int result = g.perft(2);
        assertEquals(result, 400);
    }

    @Test
    public void DefaultBoardPerftDepth3() {
        g.init("", AI, AI);
        window.setVisible(true);
        int result = g.perft(3);
        assertEquals(result, 8902);
    }

    @Test
    public void CustomBoardPerftDepth3() {
        g.init("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", AI, AI);
        window.setVisible(true);
        int result = g.perft(1);
        assertEquals(result, 48);
        result = g.perft(2);
        assertEquals(result, 2039);
    }

}
