package Player;

import Game.*;
import Util.*;
import Piece.Piece;

public class Human extends Player {

    public Human(Board board, int color) {
        super(board, color);
        
    }

    @Override
    public void makeMove() {
        if(this.game.mouse.pressed && this.game.promotionPC != null) {
            if(this.game.mouse.pressed && this.game.mouse.x <= 800) {
                int clicked_row = this.game.mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = this.game.mouse.x/CONSTANTS.SQSIZE;
                this.game.handlePiecePromotion(clicked_row, clicked_col);
            }
            return;
        }
        // Mouse Press
        if(this.game.mouse.pressed && this.game.mouse.x <= 800) {
            if(this.game.activeSQ == null || this.game.activePC == null) { // No square selected
                int clicked_row = this.game.mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = this.game.mouse.x/CONSTANTS.SQSIZE;
                this.game.handlePieceSelection(clicked_row, clicked_col);
            } else { // Square selected
                simulate(); 
            }
        }

        // Mouse Release
        if(!this.game.mouse.pressed) {
            if(this.game.activePC != null) {
                this.game.handlePiecePlacement();
            }
        }
    }

    private void simulate() {
        this.game.canMove = false;
        this.game.validSquare = false;

        if(Piece.castlePC != null) {
            Piece.castlePC.col = Piece.castlePC.prevCol;
            Piece.castlePC.x = Piece.castlePC.getX(Piece.castlePC.col);
            Piece.castlePC = null;
        }

        // Dragging
        this.game.activePC.x = this.game.mouse.x - (CONSTANTS.SQSIZE/2);
        this.game.activePC.y = this.game.mouse.y - (CONSTANTS.SQSIZE/2);

        this.game.activePC.col = this.game.activePC.getCol(this.game.activePC.x);
        this.game.activePC.row = this.game.activePC.getCol(this.game.activePC.y);
        this.game.repaint();
        // Checking if valid move
        this.game.activePC.getValidMoves(board, true);
        if (this.game.activePC.validMoves.contains(new Coordinate(this.game.activePC.row, this.game.activePC.col))) {
            this.game.canMove = true;
            this.game.validSquare = true;
            this.game.handleCastling();
        } else {
            this.game.canMove = false;
            this.game.validSquare = false;
        }
    }
}
