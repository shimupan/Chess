package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Game.Board;
import Util.CONSTANTS;
import Util.Move;
import Util.Type;

public class AI extends Player {

    private Board AIBoard;

    public AI(Board board, Board AIBoard, int color) {
        super(board, color);
        this.AIBoard = AIBoard;
    }

    @Override
    public void makeMove() { 
        this.game.mg.generateMoves();
        Set<Move> moves = this.game.mg.moves;
        Random rand = new Random();

        List<Move> movesList = new ArrayList<>(moves);
        Move move = null;
        int index = -1;
        try {
            index = rand.nextInt(moves.size());
            move = movesList.get(index);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        this.game.validSquare = true;
        this.game.handlePieceSelection(move.p.row, move.p.col);
        // Update active pc with move
        try {
            this.updateMove(move);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Promote Piece
        if(this.game.promotionPC != null) {
            // always promote to queen for now
            this.game.handlePiecePlacement(move);
            this.game.handlePiecePromotion(this.game.promotionPC.row, this.game.promotionPC.col, move);
        } else {
            // Place it
            this.game.handlePiecePlacement(move);
        }
        
    }

    private void updateMove(Move move) {

        this.game.activePC.row = move.destCoords.row;
        this.game.activePC.col = move.destCoords.col;
        if( Type.isPawn(this.game.activePC) && 
            ((this.color == CONSTANTS.WHITE && move.destCoords.row == 0) || 
            (this.color == CONSTANTS.BLACK && move.destCoords.row == 7)) ) {
            this.game.promotionPC = this.game.activePC;
        }
    }

}
