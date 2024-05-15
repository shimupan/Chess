package Player;

import Game.*;
import Piece.Piece;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Sound;
import Util.Type;

public class Human extends Player {

    private Square activeSQ = null;
    private boolean validSquare = false;

    public Human(Board board) {
        super(board);
        
    }

    @Override
    public Coordinate makeMove() { return new Coordinate(0, 0); }

    public void update() {
        if(this.game.promotionPC != null) {
            if(this.game.mouse.pressed && this.game.mouse.x <= 800) {
                int clicked_row = this.game.mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = this.game.mouse.x/CONSTANTS.SQSIZE;

                for (Piece pc : this.game.promoPCLst) {
                    if (pc.row == clicked_row && pc.col == clicked_col) {
                        // Promotion piece was clicked, handle accordingly
                        this.board.handlePromotion(this.game.promotionPC.row, this.game.promotionPC.col, pc);
                        this.game.promotionPC = null;
                        this.game.promoPCLst.clear();
                        this.game.swapTurn();
                        this.game.repaint();
                        break;
                    }
                }
            }
            return;
        }
        // Mouse Press
        if(this.game.mouse.pressed && this.game.mouse.x <= 800) {
            if(this.activeSQ == null || this.game.activePC == null) { // No square selected
                int clicked_row = this.game.mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = this.game.mouse.x/CONSTANTS.SQSIZE;
                this.activeSQ = board.getSquare(clicked_row, clicked_col);
                // Square has a piece on it and the piece is the curr turn
                if(this.activeSQ.containsPiece() && 
                this.activeSQ.getPiece().color == this.game.currColor) {
                    this.game.activePC = activeSQ.getPiece();
                    this.game.activePC.prevCol = this.game.activePC.col;
                    this.game.activePC.prevRow = this.game.activePC.row;
                    this.game.hoveredSquare = this.activeSQ;
                }
            } else { // Square selected
                simulate(); 
            }
        }

        // Mouse Release
        if(!this.game.mouse.pressed) {
            if(this.game.activePC != null) {
                if (this.validSquare) {

                    boolean castle = false, check = false;

                    // Update moved piece
                    this.game.activePC.updatePos(this.board);
                    
                    // square highlighting
                    int currRow = this.game.activePC.getRow(this.game.activePC.y);
                    int currCol = this.game.activePC.getCol(this.game.activePC.x);
                    this.game.currentMoveLocation = board.rep[currRow][currCol];
                    
                    int prevRow = this.game.activePC.prevRow;
                    int prevCol = this.game.activePC.prevCol;
                    this.game.previousMoveLocation = board.rep[prevRow][prevCol];
                    this.game.hoveredSquare = null;

                    // castle
                    if(Piece.castlePC != null) {
                        castle = true;
                        Sound.play("castle");
                        Piece.castlePC.updatePos(this.board);
                        Piece.castlePC = null;
                    }

                    // Check the pseudo legal moves
                    if(Piece.putKingInCheck(this.game.activePC, this.board)) {
                        check = true;
                        Sound.play("move-check");
                    }

                    // en-passant
                    if (this.game.clearEnPassantNextTurn) {
                        Piece.enpassantPieces.clear();
                    }
                    this.game.clearEnPassantNextTurn = !Piece.enpassantPieces.isEmpty();
                    
                    // sound for normal move if no castle
                    if(!castle && !check) Sound.play("move-self");

                    // Check if the move moved the pawn 
                    if(Type.isPawn(this.game.activePC)) {
                        int promotionRank = this.game.activePC.color == CONSTANTS.WHITE ? 0 : 7;
                        if(this.game.activePC.row == promotionRank) {
                            this.game.promotionPC = this.game.activePC;
                        }
                    }

                    // Promotion
                    if (this.game.promotionPC == null) { this.game.swapTurn(); }
                } else {
                    Sound.play("illegal");
                    this.game.activePC.resetPos();
                }
                this.game.activePC = null;
                this.activeSQ = null;
                this.game.repaint();
            }
        }
    }

    private void simulate() {

        this.game.canMove = false;
        this.validSquare = false;

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
            this.validSquare = true;
            this.game.handleCastling();
        } else {
            this.game.canMove = false;
            this.validSquare = false;
        }
    }
}
