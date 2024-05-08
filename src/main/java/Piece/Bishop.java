package Piece;

import Util.CONSTANTS;

public class Bishop extends Piece {
    
    public Bishop(int color, int row, int col) {
        super(color, row, col);
        this.value = 3.0001;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "white_bishop"): 
                   this.loadImage(CONSTANTS.IMG_URL + "black_bishop");
    }
}
