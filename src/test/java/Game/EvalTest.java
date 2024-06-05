package Game;

import org.junit.Test;

import Main.Main;

public class EvalTest {

    @Test
    public void EvalTest1() {
        Main.main(new String[]{"3k4/8/7p/2p3pP/1pPpPpP1/1P1PpP2/N7/2K5 w - - 0 1", "AI", "AI"});
        GameTest.sleep(6000);
    }

}
