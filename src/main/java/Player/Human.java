package Player;

import Game.*;
import Util.*;
import Util.Enums.MoveType;
import Piece.Piece;

public class Human extends Player {

    private Move move;

    public Human(Board board, int color) {
        super(board, color);
    }

    @Override
    public void makeMove() {
        if(this.game.mouse.pressed && this.game.promotionPC != null) {
            if(this.game.mouse.pressed && this.game.mouse.x <= 800) {
                int clicked_row = this.game.mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = this.game.mouse.x/CONSTANTS.SQSIZE;
                this.game.handlePiecePromotion(clicked_row, clicked_col, this.move);
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
                
                // Exception for castling
                this.move = new Move(this.game.activePC, new Coordinate(this.game.activePC.row, this.game.activePC.col), MoveType.Normal);
                if(Type.isKing(this.game.activePC)) {
                    for (Move m : this.game.activePC.validMoves) {
                        if (m.destCoords.row == this.game.activePC.row && m.destCoords.col == this.game.activePC.col) {
                            this.move = m;
                            break;
                        }
                    }
                }

                this.game.handlePiecePlacement(this.move);
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
        Coordinate target = new Coordinate(this.game.activePC.row, this.game.activePC.col);
        for (Move move : this.game.activePC.validMoves) {
            if (move.destCoords.equals(target)) {
                this.game.canMove = true;
                this.game.validSquare = true;
                break;
            } else {
                this.game.canMove = false;
                this.game.validSquare = false;
            }
        }
    }
}
