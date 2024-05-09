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

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        return ( ( canMoveInL(targetCol, this.prevCol, targetRow, this.prevRow) ) &&
                   validSquare(targetRow, targetCol)
                );
    }

    private boolean canMoveInL(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) * Math.abs(y1 - y2) ) == 2 );
    }

}
