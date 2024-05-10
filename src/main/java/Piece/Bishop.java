package Piece;

import Util.CONSTANTS;

public class Bishop extends Piece {
    
    public Bishop(int color, int row, int col) {
        super(color, row, col);
        this.value = 3.0001;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wb"): 
                   this.loadImage(CONSTANTS.IMG_URL + "bb");
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        return (canMoveDiagonally(targetRow, targetCol) && 
                !sameSquare(targetRow, targetCol) && 
                validSquare(targetRow, targetCol) &&
                !pieceOnDiagonalLine(targetRow, targetCol)
                );
    }

    private boolean canMoveDiagonally(int targetRow, int targetCol) {
        return (Math.abs(targetCol - this.prevCol) == Math.abs(targetRow - prevRow));
    }
}
