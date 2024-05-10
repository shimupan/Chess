package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;

public class Rook extends Piece {
    
    public Rook(int color, int row, int col) {
        super(color, row, col);
        this.value = 5.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wr"): 
                   this.loadImage(CONSTANTS.IMG_URL + "br");
    }

    public Rook(Rook other) {
        super(other);
    }

    @Override
    public boolean canMove(int targetRow, int targetCol, Board board) {
        return (inBound(targetRow,targetCol) &&
                (targetCol == this.prevCol || targetRow == this.prevRow) && 
                !sameSquare(targetRow, targetCol) && 
                validSquare(targetRow, targetCol, board) &&
                !pieceOnStraightLine(targetRow, targetCol, board)  
                );
    }

    @Override
    public void getValidMoves(Board board, boolean check) {
        this.validMoves.clear();
        List<Coordinate> directions = Arrays.asList(
            new Coordinate(-1, 0), // up
            new Coordinate(0, 1), // right
            new Coordinate(1, 0), // down
            new Coordinate(0, -1) // left
        );

        for (Coordinate direction : directions) {
            int newRow = this.prevRow + direction.row;
            int newCol = this.prevCol + direction.col;

            while (inBound(newRow, newCol) && canMove(newRow, newCol, board)) {
                this.validMoves.add(new Coordinate(newRow, newCol));

                // Move to the next square in the current direction
                newRow += direction.row;
                newCol += direction.col;
            }
        }
    }
}
