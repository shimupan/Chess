package Piece;

import Util.CONSTANTS;

public class Pawn extends Piece {

    public Pawn(int color, int row, int col) {
        super(color, row, col);
        this.value = 1.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_pawn"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_pawn");
    }
}
