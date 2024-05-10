package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;

public class King extends Piece {
    
    public King(int color, int row, int col) {
        super(color, row, col);
        this.value = 1000.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wk"): 
                   this.loadImage(CONSTANTS.IMG_URL + "bk");
    }

    public King(King other) {
        super(other);
    }

    @Override
    public boolean canMove(int targetRow, int targetCol, Board board) {
        // normal
        if(inBound(targetRow,targetCol) && 
           (canMoveUpAndDown(targetCol, this.prevCol, targetRow, this.prevRow)  || 
           canMoveDiagonally(targetCol, this.prevCol, targetRow, this.prevRow) ) &&
           validSquare(targetRow, targetCol, board)) {
            return true;
        }

        // castling
        if(this.moved) {return false;}

        // right castling
        if(targetCol == this.prevCol + 2 && !pieceOnStraightLine(targetRow, targetCol, board)) {
            if(board.getPiece(this.prevRow, this.prevCol+3) != null && 
               !board.getPiece(this.prevRow, this.prevCol+3).moved) {
                Piece.castlePC = board.getPiece(this.prevRow, this.prevCol+3);

                return true;
            }
        }

        // left castle
        if(targetCol == this.prevCol - 2 && !pieceOnStraightLine(targetRow, targetCol, board)) {
            if(board.getPiece(this.prevRow, this.prevCol+3) != null && 
               !board.getPiece(this.prevRow, this.prevCol-4).moved && 
               !board.containsPiece(this.prevRow, this.prevCol-3)) {
                
                Piece.castlePC = board.getPiece(this.prevRow, this.prevCol-4);
                return true;
            }
        }

        return false;
    }

    @Override
    public void getValidMoves(Board board, boolean check) {
        this.validMoves.clear();
        List<Coordinate> directions = Arrays.asList(
            new Coordinate(this.prevRow-1, this.prevCol+0), // up
            new Coordinate(this.prevRow-1, this.prevCol+1), // up-right
            new Coordinate(this.prevRow+0, this.prevCol+1), // right
            new Coordinate(this.prevRow+1, this.prevCol+1), // down-right
            new Coordinate(this.prevRow+1, this.prevCol+0), // down
            new Coordinate(this.prevRow+1, this.prevCol-1), // down-left
            new Coordinate(this.prevRow+0, this.prevCol-1), // left
            new Coordinate(this.prevRow-1, this.prevCol-1)  // up-left
        );
        for(Coordinate c: directions) {
            if(canMove(c.row, c.col, board)) {
                if(check) {
                    // Check if current move will put king in check
                    // SLOW
                    if(!kingInCheck(this, c.row, c.col, board)) {
                        this.validMoves.add(c);
                    }
                } else {
                    this.validMoves.add(c);
                }
            }
        }
    }

    private boolean canMoveDiagonally(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) * Math.abs(y1 - y2) ) == 1 );
    }

    private boolean canMoveUpAndDown(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) + Math.abs(y1 - y2) ) == 1 );
    }
}
