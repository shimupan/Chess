package Util;

public class Enums {
    public static enum PlayerType { Human, AI };
    public static enum GameState { Playing, Checkmate, Stalemate, WhiteWin, BlackWin };
    public static enum MoveType { Invalid, Normal, LeftCastle, RightCastle, EnPassant, PawnTwoStep, PawnCapture, Promotion };
}
