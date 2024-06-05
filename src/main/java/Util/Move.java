package Util;

import java.util.List;

import Game.Board;
import Piece.Piece;
import Util.Enums.MoveType;

public class Move {
    public Piece p;
    public Coordinate destCoords, prevCoords, moveTypeDestCoords, moveTypePrevCoords;
    public MoveType type;
    public boolean firstMove;

    public String boardNotation;
    
    private Piece capturedPC;
    private Piece castlePC;
    private Piece PCBeforePromotion;

    public Move(Piece p, Coordinate destCoords, MoveType type) {
        this.p = p;
        this.capturedPC = null;
        this.destCoords = new Coordinate(destCoords);
        this.prevCoords = new Coordinate(p.prevRow, p.prevCol);
        this.type = type;
        this.firstMove = p.moved;
        this.boardNotation = Board.squareToAlgebraic(prevCoords) + Board.squareToAlgebraic(destCoords);
    }
    
    public Move(Piece p, int destRow, int destCol, MoveType type) {
        this(p, new Coordinate(destRow, destCol), type);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Move)) {
            return false;
        }
        Move m = (Move) o;
        return this.boardNotation.equals(m.boardNotation) && this.p.equals(m.p);
    }

    @Override
    public int hashCode() {
        return ( 31 * this.p.hashCode() + 31 * this.boardNotation.hashCode() ); 
    }

    @Override
    public String toString() {
        return this.boardNotation;
    }

    public static void sortMoves(List<Move> moves) {
        moves.sort((m1, m2) -> {
            if(m1.type == MoveType.Capture && m2.type != MoveType.Capture) {
                return -1;
            } else if(m1.type != MoveType.Capture && m2.type == MoveType.Capture) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public void setCapturedPC(Piece capturedPC) {
        this.capturedPC = capturedPC;
        this.setMoveTypeCoords(capturedPC);
    }

    public void setCastlePC(Piece CastlePC, MoveType castleSide) {
        this.castlePC = CastlePC;
        int tempCol = this.castlePC.col;
        if(castleSide == MoveType.KingSideCastle) {
            this.castlePC.col = this.castlePC.prevCol - 2;
        } else {
            this.castlePC.col = this.castlePC.prevCol + 3;
        }
        this.setMoveTypeCoords(CastlePC);
        this.castlePC.col = tempCol;
    }

    public void setPCBeforePromotion(Piece PCBeforePromotion) {
        this.PCBeforePromotion = PCBeforePromotion;
        this.setPCBeforePromotion(PCBeforePromotion);
    }

    public Coordinate getMoveTypeDestCoords() {
        return this.moveTypeDestCoords;
    }

    public Coordinate getMoveTypePrevCoords() {
        return this.moveTypePrevCoords;
    }

    public Piece getCapturedPC() {
        return this.capturedPC;
    }

    public Piece getCastlePC() {
        return this.castlePC;
    }

    public Piece getPCBeforePromotion() {
        return this.PCBeforePromotion;
    }

    private void setMoveTypeCoords(Piece p) {
        this.moveTypeDestCoords = new Coordinate(p.row, p.col);
        this.moveTypePrevCoords = new Coordinate(p.prevRow, p.prevCol);
    }
}
