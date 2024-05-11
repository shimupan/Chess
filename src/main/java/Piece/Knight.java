package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
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

    public Knight(Knight other) {
        super(other);
    }

    @Override
    public boolean canMove(int targetRow, int targetCol, Board board) {
        return ( inBound(targetRow,targetCol) &&
                 (canMoveInL(targetCol, this.prevCol, targetRow, this.prevRow) ) &&
                  validSquare(targetRow, targetCol, board) );
    }

    @Override
    public void getValidMoves(Board board, boolean check) {
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
            if (canMove(move.row, move.col, board)) {
                if(check) {
                    if(!kingInCheck(this, move.row, move.col, board)) {
                        this.validMoves.add(new Coordinate(move.row, move.col));
                    }
                } else {
                    this.validMoves.add(new Coordinate(move.row, move.col));
                }
            }
        }
    }

    private boolean canMoveInL(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) * Math.abs(y1 - y2) ) == 2 );
    }

}
