package Piece;

import java.util.Arrays;
import java.util.List;

import Util.CONSTANTS;
import Util.Coordinate;

public class Knight extends Piece {
    
    public Knight(int color, int row, int col) {
        super(color, row, col);
        this.value = 3.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wn"): 
                   this.loadImage(CONSTANTS.IMG_URL + "bn");
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        return ( inBound(targetRow,targetCol) &&
                 (canMoveInL(targetCol, this.prevCol, targetRow, this.prevRow) ) &&
                  validSquare(targetRow, targetCol) );
    }

    @Override
    public void getValidMoves() {
        this.validMoves.clear();
        List<Coordinate> potentialMoves = Arrays.asList(
            new Coordinate(this.prevRow-2, this.prevCol+1), // up 2, right 1
            new Coordinate(this.prevRow-1, this.prevCol+2), // up 1, right 2
            new Coordinate(this.prevRow+1, this.prevCol+2), // down 1, right 2
            new Coordinate(this.prevRow+2, this.prevCol+1), // down 2, right 1
            new Coordinate(this.prevRow+2, this.prevCol-1), // down 2, left 1
            new Coordinate(this.prevRow+1, this.prevCol-2), // down 1, left 2
            new Coordinate(this.prevRow-1, this.prevCol-2), // up 1, left 2
            new Coordinate(this.prevRow-2, this.prevCol-1)  // up 2, left 1
        );

        for (Coordinate move : potentialMoves) {
            if (canMove(move.row, move.col)) {
                this.validMoves.add(move);
            }
        }
    }

    private boolean canMoveInL(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) * Math.abs(y1 - y2) ) == 2 );
    }

}
