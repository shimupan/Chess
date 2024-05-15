package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Game.Board;
import Piece.Piece;
import Util.Coordinate;

public class AI extends Player {

    private Board AIBoard;

    public AI(Board board, Board AIBoard) {
        super(board);
        this.AIBoard = AIBoard;
    }

    @Override
    public void makeMove() { 
        List<Coordinate> moves;
        Random rand = new Random();
        do {
            List<Piece> select = new ArrayList<>(Piece.BlackPieces);
            Piece p = select.get(rand.nextInt(select.size()));
            // Piece selection
            this.game.handlePieceSelection(p.row, p.col);

            // Get Move (randomly for now)
            this.game.activePC.getValidMoves(board, true);
            moves = new ArrayList<>(this.game.activePC.validMoves);
            
        } while(moves.isEmpty());

        Coordinate move = moves.get(rand.nextInt(moves.size()));

        // Update active pc with move
        this.updateMove(move);

        // Promote Piece
        if(this.game.promotionPC != null) {
            // always promote to queen for now
            this.game.handlePiecePromotion(this.game.promotionPC.row, this.game.promotionPC.col);
        } else {
            // Place it
            this.game.handlePiecePlacement();
        }
        
    }

    private void updateMove(Coordinate move) {
        this.game.activePC.row = move.row;
        this.game.activePC.col = move.col;
    }

}
