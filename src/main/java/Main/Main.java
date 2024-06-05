package Main;

import Game.Game;
import Util.Enums.PlayerType;;

public class Main {

    public static Game g;
    public static String fen = "";
    public static PlayerType p1 = PlayerType.Human;
    public static PlayerType p2 = PlayerType.Human;
    public static void main(String[] args) { 
        // SETUP GAME
        Util.Setup.parseArgs(args);
        Util.Setup.initGame(fen, p1, p2);
        Sidebar.init();
        Window.init();
        // START
        g.start();
    }
}

