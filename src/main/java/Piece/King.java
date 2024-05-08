package Piece;

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
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
