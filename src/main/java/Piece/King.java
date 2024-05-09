package Piece;

import Game.Board;
import Util.CONSTANTS;

public class King extends Piece {
    
    public King(int color, int row, int col) {
        super(color, row, col);
        this.value = 1000.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_king"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_king");
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        // normal
        if((canMoveUpAndDown(targetCol, this.prevCol, targetRow, this.prevRow)  || 
            canMoveDiagonally(targetCol, this.prevCol, targetRow, this.prevRow) ) &&
            validSquare(targetRow, targetCol)) {
            return true;
        }

        // castling
        if(this.moved) {return false;}

        // right castling
        if(targetCol == this.prevCol + 2 && !pieceOnStraightLine(targetRow, targetCol)) {
            if(!Board.getPiece(this.prevRow, this.prevCol+3).moved) {
                Piece.castlePC = Board.getPiece(this.prevRow, this.prevCol+3);
                return true;
            }
        }

        // left castle
        if(targetCol == this.prevCol - 2 && !pieceOnStraightLine(targetRow, targetCol)) {
            if(!Board.getPiece(this.prevRow, this.prevCol-4).moved && !Board.containsPiece(this.prevRow, this.prevCol-3)) {
                Piece.castlePC = Board.getPiece(this.prevRow, this.prevCol-4);
                return true;
            }
        }

        return false;
    }

    private boolean canMoveDiagonally(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) * Math.abs(y1 - y2) ) == 1 );
    }

    private boolean canMoveUpAndDown(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) + Math.abs(y1 - y2) ) == 1 );
    }
}
