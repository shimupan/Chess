package Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Game.Board;
import Game.Game;
import Piece.Piece;
import Util.CONSTANTS;
import Util.Move;
import Util.Type;

public class AI extends Player {

    private Board AIBoard;
    private Game GameInstance;

    public AI(Board board, Board AIBoard, int color) {
        super(board, color);
        this.AIBoard = AIBoard;
        this.GameInstance = Game.getInstance();
    }

    @Override
    public void makeMove() { 
        Set<Move> moves = GameInstance.mg.moves;
        Random rand = new Random();

        List<Move> movesList = new ArrayList<>(moves);
        Move move = movesList.get(rand.nextInt(moves.size()));

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
