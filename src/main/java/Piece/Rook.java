package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Enums.MoveType;
import Util.Move;

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
    public MoveType canMove(int targetRow, int targetCol, Board board) {
        if(inBound(targetRow,targetCol) &&
          (targetCol == this.prevCol || targetRow == this.prevRow) && 
          !sameSquare(targetRow, targetCol) && 
          validSquare(targetRow, targetCol, board) &&
          !pieceOnStraightLine(targetRow, targetCol, board)  
        ) {
            return MoveType.Normal;
        }
        return MoveType.Invalid;
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
            MoveType moveType = canMove(newRow, newCol, board);
            while (moveType != MoveType.Invalid) {
                if(check) {
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
}
