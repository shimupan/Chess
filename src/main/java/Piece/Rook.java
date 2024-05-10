package Piece;

import Util.CONSTANTS;

public class Rook extends Piece {
    
    public Rook(int color, int row, int col) {
        super(color, row, col);
        this.value = 5.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wr"): 
                   this.loadImage(CONSTANTS.IMG_URL + "br");
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        return ((targetCol == this.prevCol || targetRow == this.prevRow) && 
                !sameSquare(targetRow, targetCol) && 
                validSquare(targetRow, targetCol) &&
                !pieceOnStraightLine(targetRow, targetCol)  
                );
    }
}
