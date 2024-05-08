package Piece;

import Util.CONSTANTS;

public class Queen extends Piece {
    
    public Queen(int color, int row, int col) {
        super(color, row, col);
        this.value = 9.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_queen"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_queen");
    }
}
