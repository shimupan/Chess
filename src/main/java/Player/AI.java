package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Game.Board;
import Piece.Piece;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Type;

public class AI extends Player {

    private Board AIBoard;

    public AI(Board board, Board AIBoard, int color) {
        super(board, color);
        this.AIBoard = AIBoard;
    }

    @Override
    public void makeMove() { 
        List<Coordinate> moves = null;
        Random rand = new Random();
        do {
            List<Piece> select = (this.color == CONSTANTS.WHITE) ? new ArrayList<>(Piece.WhitePieces) : new ArrayList<>(Piece.BlackPieces);
            Piece p = select.get(rand.nextInt(select.size()));
            // Piece selection
            this.game.handlePieceSelection(p.row, p.col);
            if(this.game.activePC == null) continue;
            // Get Move (randomly for now)
            this.game.activePC.getValidMoves(board, true);
            moves = new ArrayList<>(this.game.activePC.validMoves);
            
        } while(moves == null || moves.isEmpty());

        Coordinate move = moves.get(rand.nextInt(moves.size()));

        this.game.validSquare = true;
        // Update active pc with move
        this.updateMove(move);
        // Promote Piece
        if(this.game.promotionPC != null) {
            // always promote to queen for now
            this.game.handlePiecePlacement();
            this.game.handlePiecePromotion(this.game.promotionPC.row, this.game.promotionPC.col);
        } else {
            // Place it
            this.game.handlePiecePlacement();
        }
        
    }

    private void updateMove(Coordinate move) {
        this.game.activePC.row = move.row;
        this.game.activePC.col = move.col;
        if( Type.isPawn(this.game.activePC) && ((this.color == CONSTANTS.WHITE && move.row == 0) || (this.color == CONSTANTS.BLACK && move.row == 7)) ) {
            this.game.promotionPC = this.game.activePC;
        }
    }

}
