package Util;

import Piece.Piece;
import Util.Enums.MoveType;

public class Move {
    public Piece p;
    public Coordinate destCoords;
    public MoveType type;

    public Move(Piece p, Coordinate destCoords, MoveType type) {
        this.p = p;
        this.destCoords = new Coordinate(destCoords);
        this.type = type;
    }

    public Move(Piece p, int destRow, int destCol, MoveType type) {
        this.p = p;
        this.destCoords = new Coordinate(destRow, destCol);
        this.type = type;
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
