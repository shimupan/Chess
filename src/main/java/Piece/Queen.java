package Piece;

import Util.CONSTANTS;

public class Queen extends Piece {
    
    public Queen(int color, int row, int col) {
        super(color, row, col);
        this.value = 9.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wq"): 
                   this.loadImage(CONSTANTS.IMG_URL + "bq");
    }
    
    @Override
    public boolean canMove(int targetRow, int targetCol) {

        if(sameSquare(targetRow, targetCol)) return false;

        if(targetCol == this.prevCol || targetRow == this.prevRow) 
        return (validSquare(targetRow, targetCol) && !pieceOnStraightLine(targetRow, targetCol));

        if(Math.abs(targetCol - this.prevCol) == Math.abs(targetRow - prevRow)) 
        return (validSquare(targetRow, targetCol) && !pieceOnDiagonalLine(targetRow, targetCol));
        
        return false;
    }
}
