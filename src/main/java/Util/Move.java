package Util;

import Piece.Piece;

public class Move {
    Piece p;
    Coordinate destCoords;

    // Flags for if the move performed one of the following
    boolean promotion = false;
    boolean check = false;
    boolean enpassant = false;
    boolean castle = false;

    public Move(Piece p, Coordinate destCoords) {
        this.p = p;
        this.destCoords = new Coordinate(destCoords);
    }

    public Move(Piece p, int destRow, int destCol) {
        this.p = p;
        this.destCoords = new Coordinate(destRow, destCol);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Move)) {
            return false;
        }
        Move m = (Move) o;
        return this.p.equals(m.p) && this.destCoords.equals(m.destCoords);
    }

    @Override
    public int hashCode() {
        return ( 31 * this.p.hashCode() + 31 * this.destCoords.hashCode() ); 
    }
}
