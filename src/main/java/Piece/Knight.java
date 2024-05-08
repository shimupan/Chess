package Piece;

import Util.CONSTANTS;

public class Knight extends Piece {

    public Knight(int color, int row, int col) {
        super(color, row, col);
        this.value = 3.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_knight"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_knight");
    }
}
