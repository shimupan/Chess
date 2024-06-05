package Player;

import java.util.ArrayList;
import java.util.List;
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

        List<Move> movesList = new ArrayList<>(moves);
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int depth = 2;

        for (Move move : movesList) {
            this.game.validSquare = true;
            this.game.handlePieceSelection(move.p.row, move.p.col);
            this.updateMove(move);
            this.game.handlePiecePlacement(move, true);
            int score = -this.game.AlphaBetaPrune(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            this.undoMove(move);
            this.game.swapTurn();
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        // Now make the best move
        if (bestMove != null) {
            this.game.validSquare = true;
            this.game.handlePieceSelection(bestMove.p.row, bestMove.p.col);
            this.updateMove(bestMove);
            this.game.handlePiecePlacement(bestMove, false);
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
