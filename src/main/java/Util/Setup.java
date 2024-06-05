package Util;

import Game.Game;
import Main.Main;
import Util.Enums.PlayerType;

public abstract class Setup {

    public static void initGame(String fen, PlayerType p1, PlayerType p2) {
        Main.g = Game.getInstance();
        Main.g.init(fen, p1, p2);
        Main.g.initGUI();
        Main.g.setBounds(0, 0, 800, 800);
    }

    public static void parseArgs(String[] args) {
        if(args.length > 0) {
            Main.fen = args[0];
            if(args[1].equals("AI")) {
                Main.p1 = PlayerType.AI;
            }
            if(args[2].equals("AI")) {
                Main.p2 = PlayerType.AI;
            }
        }
    }
}
