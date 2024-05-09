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
    public boolean canMove(int targetRow, int targetCol) {
        return ( ( canMoveUpAndDown(targetCol, this.prevCol, targetRow, this.prevRow)  || 
                   canMoveDiagonally(targetCol, this.prevCol, targetRow, this.prevRow) ) &&
                   validSquare(targetRow, targetCol)
                );
    }

    private boolean canMoveDiagonally(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) * Math.abs(y1 - y2) ) == 1 );
    }

    private boolean canMoveUpAndDown(int x1, int x2, int y1, int y2) {
        return ( (Math.abs(x1 - x2) + Math.abs(y1 - y2) ) == 1 );
    }
}
