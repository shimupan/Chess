package Util;

import java.util.HashSet;
import java.util.Set;

import Game.Board;
import Piece.Piece;
import Util.Enums.GameState;

public class MoveGen {

    public Board board;
    public Piece checkingPC;
    public int currColor;

    public MoveGen(Board board, int currColor, Piece checkingPC) {
        this.board = board;
        this.currColor = currColor;
        this.checkingPC = checkingPC;
    }

    public GameState checkGameState() {
        Set<Move> moves = this.getKingMoves();
        if(moves.size() != 0) { // KING ISNT IN CHECK
            return GameState.Playing;
        }
        
        moves.addAll(this.generateMoves());
        // King isnt in check and no other pc can move
        if(checkingPC == null && moves.size() == 0) {
            return GameState.Stalemate;
        } else if (checkingPC != null && moves.size() == 0) {
            return GameState.Checkmate;
        }
        return GameState.Playing;
    }

    public Set<Move> generateMoves() {
        Set<Move> moves = new HashSet<>();
        Set<Piece> currPieceSet = (this.currColor == CONSTANTS.WHITE) ? Piece.WhitePieces : Piece.BlackPieces;
        
        for(Piece pc: currPieceSet) {
            Piece.swapPositionValues(pc);
            pc.getValidMoves(board, true);
            Piece.swapPositionValues(pc);
            for (Coordinate coord : pc.validMoves) {
                moves.add(new Move(pc, coord));
            }
        }

        return moves;
    }

    public Set<Move> getKingMoves() {
        // Check if the king can move
        Coordinate kPos = Piece.getKingPosByColor(currColor);
        Piece k = this.board.getPiece(kPos.row, kPos.col);
        Piece.swapPositionValues(k);
        k.getValidMoves(board, true);
        Piece.swapPositionValues(k);

        Set<Move> moves = new HashSet<>();
        for (Coordinate coord : k.validMoves) {
            moves.add(new Move(k, coord));
        }

        return moves;
    }
    
}
