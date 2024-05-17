package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Enums.MoveType;
import Util.Move;

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
    public MoveType canMove(int targetRow, int targetCol, Board board) {
        if ( inBound(targetRow,targetCol)  &&
             validSquare(targetRow, targetCol, board) ) {
             return MoveType.Normal;
        }
        return MoveType.Invalid;
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
            MoveType moveType = canMove(move.row, move.col, board);
            if (moveType != MoveType.Invalid) {
                if(check) {
                    if(!kingInCheck(this, move.row, move.col, board)) {
                        this.validMoves.add(new Move(this, new Coordinate(move.row, move.col), moveType));
                    }
                } else {
                    this.validMoves.add(new Move(this, new Coordinate(move.row, move.col), moveType));
                }
            }
        }
    }

}
