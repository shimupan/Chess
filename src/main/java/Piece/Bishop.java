package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Enums.MoveType;
import Util.Move;

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
    public MoveType canMove(int targetRow, int targetCol, Board board) {
        if (inBound(targetRow,targetCol) &&
            canMoveDiagonally(targetRow, targetCol) && 
            !sameSquare(targetRow, targetCol) && 
            validSquare(targetRow, targetCol, board) &&
            !pieceOnDiagonalLine(targetRow, targetCol, board)
            ) {
                return MoveType.Normal;
            }
        return MoveType.Invalid;
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
            MoveType moveType = canMove(newRow, newCol, board);
            while (moveType != MoveType.Invalid) {
                if(check) {
                    // Check if king is in check
                    if(!kingInCheck(this, newRow, newCol, board)) {
                        this.validMoves.add(new Move(this, new Coordinate(newRow, newCol), moveType));
                    }
                } else {
                    this.validMoves.add(new Move(this, new Coordinate(newRow, newCol), moveType));
                }
                // Move to the next square in the current direction
                newRow += direction.row;
                newCol += direction.col;
                moveType = canMove(newRow, newCol, board);
            }
        }
    }

    private boolean canMoveDiagonally(int targetRow, int targetCol) {
        return (Math.abs(targetCol - this.prevCol) == Math.abs(targetRow - prevRow));
    }
}
