package Piece;

import java.util.Arrays;
import java.util.List;

import Game.Board;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Enums.MoveType;
import Util.Move;

public class Pawn extends Piece {

    private int direction;

    public Pawn(int color, int row, int col) {
        super(color, row, col);
        this.value = 1.0;
        this.img = (color == CONSTANTS.WHITE) ? 
                   this.loadImage(CONSTANTS.IMG_URL + "wp"): 
                   this.loadImage(CONSTANTS.IMG_URL + "bp");
        this.direction = (color == CONSTANTS.WHITE) ? -1 : 1;
    }

    public Pawn(Pawn other) {
        super(other);
        this.direction = other.direction;
    }

    @Override
    public MoveType canMove(int targetRow, int targetCol, Board board) {
        if(inBound(targetRow,targetCol)) {
            Piece p = board.getPiece(targetRow, targetCol);
            
            boolean isOneSquareAheadEmpty = targetCol == this.prevCol && 
                                            board.getPiece(this.prevRow + direction, this.prevCol) == null;

            if(isOneSquareAheadEmpty && targetRow == this.prevRow + direction) { // 1 Square Movement
                return MoveType.Normal;
            } 
            
            if(isOneSquareAheadEmpty && targetCol == this.prevCol && 
            targetRow == this.prevRow + (direction*2) && 
            p == null && 
            !this.moved) { // 2 Square Movement
                return MoveType.PawnTwoStep;
            }
            // Capture Diagonal
            if(Math.abs(targetCol - this.prevCol) == 1 && 
            targetRow == this.prevRow + direction && 
            p != null && 
            p.color != this.color) {
                return MoveType.PawnCapture;
            }
            // En Passant
            if(Math.abs(targetCol - this.prevCol) == 1 && 
            targetRow == this.prevRow + direction) {
                for(Piece ep: enpassantPieces) {
                    if(ep != null && ep.col == targetCol && ep.color != this.color) {
                        return MoveType.EnPassant;
                    }
                }
            }
        }

        return MoveType.Invalid;
    }

    @Override
    public void getValidMoves(Board board, boolean check) {
        this.validMoves.clear();
        int direction = this.color == CONSTANTS.WHITE ? -1 : 1;

        List<Coordinate> potentialMoves = Arrays.asList(
            new Coordinate(this.prevRow + direction, this.prevCol), // Move forward by one square
            new Coordinate(this.prevRow + 2 * direction, this.prevCol), // Move forward by two squares
            new Coordinate(this.prevRow + direction, this.prevCol - 1), // Capture diagonally left
            new Coordinate(this.prevRow + direction, this.prevCol + 1) // Capture diagonally right
        );

        for (Coordinate move : potentialMoves) {
            MoveType moveType = canMove(move.row, move.col, board);
            if (moveType != MoveType.Invalid) {
                if(check) {
                    if(!kingInCheck(this, move.row, move.col, board)) {
                        this.validMoves.add(new Move(this, new Coordinate(move.row, move.col), moveType));
                    }
                } else {
                    this.validMoves.add(new Move(this, new Coordinate(move.row, move.col), moveType));
                }
            }
        }
    }
}
