package Util;

import Piece.*;

public class Type {
    
    public static boolean isPawn(Piece piece) {
        return piece instanceof Pawn;
    }

    public static boolean isRook(Piece piece) {
        return piece instanceof Rook;
    }

    public static boolean isKnight(Piece piece) {
        return piece instanceof Knight;
    }

    public static boolean isBishop(Piece piece) {
        return piece instanceof Bishop;
    }

    public static boolean isQueen(Piece piece) {
        return piece instanceof Queen;
    }

    public static boolean isKing(Piece piece) {
        return piece instanceof King;
    }

}
