package Util;

import java.util.Set;

import Game.Board;
import Piece.Piece;
import Util.Enums.GameState;

public class MoveGen {

    public Board board;
    public Piece checkingPC;
    public int currColor;
    public int moveCount;

    public MoveGen(Board board, int currColor, Piece checkingPC) {
        this.board = board;
        this.currColor = currColor;
        this.checkingPC = checkingPC;
    }

    public GameState checkGameState() {
        this.getKingMoves();
        if(moveCount != 0) { // KING ISNT IN CHECK
            return GameState.Playing;
        }
        
        this.generateMoves();
        // King isnt in check and no other pc can move
        if(checkingPC == null && moveCount == 0) {
            return GameState.Stalemate;
        } else if (checkingPC != null && moveCount == 0) {
            return GameState.Checkmate;
        }
        return GameState.Playing;
    }

    public void generateMoves() {

        Set<Piece> currPieceSet = (this.currColor == CONSTANTS.WHITE) ? Piece.WhitePieces : Piece.BlackPieces;
        
        for(Piece pc: currPieceSet) {
            pc.getValidMoves(board, true);
            moveCount += pc.validMoves.size();
        }
    }

    public void getKingMoves() {
        // Check if the king can move
        Coordinate kPos = Piece.getKingPosByColor(currColor);
        Piece k = this.board.getPiece(kPos.row, kPos.col);
        k.getValidMoves(board, true);
        moveCount += k.validMoves.size();
    }
    
}
