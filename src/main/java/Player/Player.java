package Player;

import java.util.Set;

import Game.Board;
import Game.Game;
import Piece.Piece;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Enums.MoveType;
import Util.Move;

public abstract class Player {

    public Board board;
    public Game game;
    public int color;

    public Player(Board board, int color) {
        this.board = board;
        this.color = color;
        this.game = Game.getInstance();
    }
    
    public abstract void makeMove();
 
    public void undoMove(Move move) {
        // Normal None Capture Move
        if(move.type == MoveType.Normal || move.type == MoveType.PawnTwoStep) {
            // Move the piece back to its original position
            this.board.getSquare(move.prevCoords.row, move.prevCoords.col).updatePiece(move.p);
            this.board.getSquare(move.destCoords.row, move.destCoords.col).updatePiece(null);
            // Fix the piece's position
            this.updatePiecePosition(move.p, move.prevCoords, move.destCoords, move, true);
            Piece.enpassantPiece = Piece.prevEnpassantPiece;
            Piece.enpassantPiece = null;
        } else if(move.type == MoveType.Capture || move.type == MoveType.PawnCapture) {
            this.board.getSquare(move.prevCoords.row, move.prevCoords.col).updatePiece(move.p);
            this.board.getSquare(move.destCoords.row, move.destCoords.col).updatePiece(move.getCapturedPC());
            // Fix the piece's position
            this.updatePiecePosition(move.p, move.prevCoords, move.destCoords, move, true);
            // Fix the captured piece's position
            this.updatePiecePosition(move.getCapturedPC(), move.moveTypePrevCoords, move.moveTypeDestCoords, move, false);
            Piece.enpassantPiece = Piece.prevEnpassantPiece;
            Piece.enpassantPiece = null;
            Set<Piece> pieces = (move.getCapturedPC().color == CONSTANTS.WHITE) ? Piece.WhitePieces : Piece.BlackPieces;
            pieces.add(move.getCapturedPC());
        } else if(move.type == MoveType.EnPassant) {
            this.board.getSquare(move.prevCoords.row, move.prevCoords.col).updatePiece(move.p);
            this.board.getSquare(move.moveTypeDestCoords.row, move.moveTypeDestCoords.col).updatePiece(move.getCapturedPC());
            this.board.getSquare(move.destCoords.row, move.destCoords.col).updatePiece(null);
            // Fix the piece's position
            this.updatePiecePosition(move.p, move.prevCoords, move.destCoords, move, true);
            // Fix the captured piece's position
            this.updatePiecePosition(move.getCapturedPC(), move.moveTypePrevCoords, move.moveTypeDestCoords, move, false);
            Piece.enpassantPiece = move.getCapturedPC();
            Set<Piece> pieces = (move.getCapturedPC().color == CONSTANTS.WHITE) ? Piece.WhitePieces : Piece.BlackPieces;
            pieces.add(move.getCapturedPC());
        } else if(move.type == MoveType.QueenSideCastle || move.type == MoveType.KingSideCastle) {
            // Move the king back to its original position
            this.board.getSquare(move.prevCoords.row, move.prevCoords.col).updatePiece(move.p);
            this.board.getSquare(move.destCoords.row, move.destCoords.col).updatePiece(null);
            // Move the rook back to its original position
            this.board.getSquare(move.moveTypePrevCoords.row, move.moveTypePrevCoords.col).updatePiece(move.getCastlePC());
            this.board.getSquare(move.moveTypeDestCoords.row, move.moveTypeDestCoords.col).updatePiece(null);

            // Fix the king's position
            this.updatePiecePosition(move.p, move.prevCoords, move.destCoords, move, true);
            // Fix the rook's position
            this.updatePiecePosition(move.getCastlePC(), move.moveTypePrevCoords, move.moveTypeDestCoords, move, true);
            Piece.enpassantPiece = Piece.prevEnpassantPiece;
            Piece.enpassantPiece = null;
        } else if(move.type == MoveType.Promotion) {
            // TODO: Fix this
            this.board.getSquare(move.prevCoords.row, move.prevCoords.col).updatePiece(move.p);
            this.board.getSquare(move.destCoords.row, move.destCoords.col).updatePiece(move.getPCBeforePromotion());
            move.p.row = move.prevCoords.row;
            move.p.col = move.prevCoords.col;
            move.p.moved = move.firstMove;
            move.p.updatePos(board, move, true);
        }
        this.game.activePC = null;
        this.game.activeSQ = null;
        this.game.validSquare = false;
        this.game.canMove = false;
        this.game.currentMoveLocation = null;
        this.game.previousMoveLocation = null;
    }

    public void updatePiecePosition(Piece piece, Coordinate prevCoords, Coordinate destCoords, Move move, boolean update) {
        this.board.getSquare(prevCoords.row, prevCoords.col).updatePiece(piece);
        
        if(update) {
            piece.row = prevCoords.row;
            piece.col = prevCoords.col;
        } else {
            piece.row = destCoords.row;
            piece.col = destCoords.col;
        }
        
        piece.prevRow = prevCoords.row;
        piece.prevCol = prevCoords.col;
        if(update) {
            piece.updatePos(board, move, update);
            piece.moved = move.firstMove;
        }
    }

    public void update() { }
    
}
