package Piece;

import Util.CONSTANTS;

public class Rook extends Piece {
    
    public Rook(int color, int row, int col) {
        super(color, row, col);
        this.value = 5.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_rook"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_rook");
    }
}