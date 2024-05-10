package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;

public class Bishop extends Piece {
    
    public Bishop(int color, int row, int col) {
        super(color, row, col);
        this.value = 3.0001;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wb"): 
                   this.loadImage(CONSTANTS.IMG_URL + "bb");
    }

    public Bishop(Bishop other) {
        super(other);
    }

    @Override
    public boolean canMove(int targetRow, int targetCol, Board board) {
        return (inBound(targetRow,targetCol) &&
                canMoveDiagonally(targetRow, targetCol) && 
                !sameSquare(targetRow, targetCol) && 
                validSquare(targetRow, targetCol, board) &&
                !pieceOnDiagonalLine(targetRow, targetCol, board)
                );
    }

    @Override
    public void getValidMoves(Board board, boolean check) {
        this.validMoves.clear();
        List<Coordinate> directions = Arrays.asList(
            new Coordinate(-1, 1), // up-right
            new Coordinate(-1, -1), // up-left
            new Coordinate(1, 1), // down-right
            new Coordinate(1, -1) // down-left
        );

        for (Coordinate direction : directions) {
            int newRow = this.prevRow + direction.row;
            int newCol = this.prevCol + direction.col;

            while (canMove(newRow, newCol, board)) {
                this.validMoves.add(new Coordinate(newRow, newCol));

                if (board.rep[newRow][newCol].containsPiece()) {
                    break;
                }

                // Move to the next square in the current direction
                newRow += direction.row;
                newCol += direction.col;
            }
        }
    }

    private boolean canMoveDiagonally(int targetRow, int targetCol) {
        return (Math.abs(targetCol - this.prevCol) == Math.abs(targetRow - prevRow));
    }
}
