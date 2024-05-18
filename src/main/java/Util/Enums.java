package Util;

public class Enums {
    public static enum PlayerType { Human, AI };
    public static enum GameState { Playing, Checkmate, Stalemate, WhiteWin, BlackWin };
    public static enum MoveType { Invalid, Normal, Capture, KingSideCastle, QueenSideCastle, EnPassant, PawnTwoStep, PawnCapture, Promotion };
}
