package Piece;

import Game.Board;
import Util.CONSTANTS;

public class Pawn extends Piece {

    private int direction;

    public Pawn(int color, int row, int col) {
        super(color, row, col);
        this.value = 1.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_pawn"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_pawn");
        this.direction = (color == CONSTANTS.WHITE) ? -1 : 1;
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        
        Piece p = Board.getPiece(targetRow, targetCol);

        // 1 Square Movement
        if(targetCol == this.prevCol && 
           targetRow == this.prevRow + direction && 
           p == null) {
            return true;
        }
        // 2 Square Movement
        if(targetCol == this.prevCol && targetRow == this.prevRow + (direction*2) && 
           p == null && 
           !this.moved) {
            return true;
        }
        // Capture Diagonal
        if(Math.abs(targetCol - this.prevCol) == 1 && 
           targetRow == this.prevRow + direction && 
           p != null && 
           p.color != this.color) {
            return true;
        }

        return false;
    }
}
