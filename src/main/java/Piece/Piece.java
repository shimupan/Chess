package Piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import Game.Board;
import Game.Square;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Sound;
import Util.Type;

public abstract class Piece {

    public BufferedImage img;
    public int x, y;
    public int col, row, prevCol, prevRow;
    public int color;
    public double value;
    public boolean moved;
    public Set<Coordinate> validMoves = new HashSet<>();

    public static Piece castlePC;
    public static Piece[] kingPos = new Piece[2];
    public static Set<Piece> enpassantPieces = new HashSet<>();

    public abstract boolean canMove(int targetRow, int targetCol, Board board);
    public abstract void getValidMoves(Board board, boolean check);

    public Piece(int color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.prevCol = col;
        this.prevRow = row;
        this.x = getX(col);
        this.y = getY(row);
        this.moved = false;
    }

    public Piece(Piece other) {
        this.color = other.color;
        this.row = other.row;
        this.col = other.col;
        this.prevCol = other.col;
        this.prevRow = other.row;
        this.x = other.x;
        this.y = other.y;
        this.moved = other.moved;
    }

    public void draw(Graphics2D g2) {
        int drawX = this.x;
        int drawY = this.y;
    
        // Ensure the piece is not drawn outside the intended area
        if (drawX + CONSTANTS.SQSIZE > 800) {
            drawX = 800 - CONSTANTS.SQSIZE;
        }
    
        g2.drawImage(img, drawX, drawY, CONSTANTS.SQSIZE, CONSTANTS.SQSIZE, null);
    }

    public BufferedImage loadImage(String path) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new FileInputStream(path + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public void updatePos(Board board) {
        this.x = getX(col);
        this.y = getY(row);
        this.col = getCol(x);
        this.row = getRow(y);
        
        // If its a pawn 
        if(Type.isPawn(this)) {
            // check if it moved 2 squares
            if(Math.abs(this.row - this.prevRow) == 2) {
                Piece.enpassantPieces.add(this);
            }
            // check if it did enpassant
            if (Math.abs(this.col - this.prevCol) == 1) {
                int capturedRow = (this.color == CONSTANTS.WHITE) ? this.row + 1 : this.row - 1;
                Square capturedSquare = board.rep[capturedRow][this.col];
                Square movedSquare = board.rep[this.row][this.col];

                // If there is a pawn behind the current move and the moved square is empty
                if (movedSquare.getPiece() == null && 
                    capturedSquare.getPiece() != null && 
                    Type.isPawn(capturedSquare.getPiece())) {
                        
                    capturedSquare.updatePiece(null);
                    Sound.play("capture");
                }
            }
        }

        // Update the Squares Effected
        Square Dest = board.rep[row][col];
        Square Prev = board.rep[prevRow][prevCol];
        
        if(Dest.equals(Prev)) return;

        if(Dest.containsPiece()) Sound.play("capture");

        Dest.updatePiece(Prev.getPiece());
        Prev.updatePiece(null);
        
        this.moved = true;
    }

    public void resetPos() {
        this.col = prevCol;
        this.row = prevRow;
        this.x = getX(this.col);
        this.y = getY(this.row);
    }

    public Piece getCollidingPiece(int targetRow, int targetCol, Board board) {
        if(board.getPiece(targetRow, targetCol) == this) {
            return null;
        }
        return board.getPiece(targetRow, targetCol);
    }

    public boolean validSquare(int targetRow, int targetCol, Board board) {
        Piece p = getCollidingPiece(targetRow, targetCol, board);
        
        if(p == null) {
            return true;
        } else {
            return p.color != this.color ? true : false;
        }
    }

    public boolean sameSquare(int targetRow, int targetCol) {
        return (targetCol == this.prevCol && targetRow == this.prevRow);
    }

    public boolean pieceOnStraightLine(int targetRow, int targetCol, Board board) {
        
        // Left
        for(int col = this.prevCol-1; col > targetCol; col--) {
            if(board.rep[targetRow][col].containsPiece()) {
                return true;
            }
        }

        // Right
        for(int col = this.prevCol+1; col < targetCol; col++) {
            if(board.rep[targetRow][col].containsPiece()) {
                return true;
            }
        }

        // Up
        for(int row = this.prevRow-1; row > targetRow; row--) {
            if(board.rep[row][targetCol].containsPiece()) {
                return true;
            }
        }

        // Down
        for(int row = this.prevRow+1; row < targetRow; row++) {
            if(board.rep[row][targetCol].containsPiece()) {
                return true;
            }
        }

        return false;
    }

    public boolean pieceOnDiagonalLine(int targetRow, int targetCol, Board board) {
        
        if(targetRow < this.prevRow) { // upper left and right
            
            // upper left
            for(int col = this.prevCol-1; col > targetCol; col--) {
                int rowDiff = this.prevRow - Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }

            // upper right
            for(int col = this.prevCol+1; col < targetCol; col++) {
                int rowDiff = this.prevRow - Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }

        } else { // lower left and right

            // lower left
            for(int col = this.prevCol-1; col > targetCol; col--) {
                int rowDiff = this.prevRow + Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }

            // lower right
            for(int col = this.prevCol+1; col < targetCol; col++) {
                int rowDiff = this.prevRow + Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public static boolean inBound(int targetRow, int targetCol) {
        return (targetRow < CONSTANTS.ROWS && 
                targetRow >= 0 && 
                targetCol < CONSTANTS.COLS && 
                targetCol >= 0);
    }

    public boolean kingInCheck(Piece p, int targetRow, int targetCol, Board board) {
        // Create a deep copy of the board
        Board boardCopy = new Board(board);

        // Simulate the move on the copied board
        boardCopy.getSquare(targetRow, targetCol).updatePiece(p);
        boardCopy.getSquare(this.prevRow, this.prevCol).updatePiece(null);

        // Find the king's position
        Coordinate kingCoordinate = Piece.getKingPosByColor(p.color);

        // If the piece being moved is a king, manually update kingCoordinate
        if (p instanceof King && p.color == p.color) {
            kingCoordinate = new Coordinate(targetRow, targetCol);
        }

        // Check if the king is in check
        boolean check = false;
        for (int row = 0; row < CONSTANTS.ROWS; row++) {
            for (int col = 0; col < CONSTANTS.COLS; col++) {
                Piece pi = boardCopy.getPiece(row, col);
                if (pi != null && pi.color != p.color) {
                    pi.getValidMoves(boardCopy, false);
                    for (Coordinate c : pi.validMoves) {
                        if (c.equals(kingCoordinate)) {
                            check = true;
                            break;
                        }
                    }
                }
            }
            if (check) {
                break;
            }
        }

        return check;
    }

    public int getX(int col) {
        return col * CONSTANTS.SQSIZE;
    }

    public int getY(int row) {
        return row * CONSTANTS.SQSIZE;
    }

    public int getRow(int y) {
        return (y + (CONSTANTS.SQSIZE/2) ) / CONSTANTS.SQSIZE;
    }

    public int getCol(int x) {
        return (x + (CONSTANTS.SQSIZE/2) ) / CONSTANTS.SQSIZE;
    }

    public static Coordinate getKingPosByColor(int color) {
        return (color == CONSTANTS.WHITE) ? 
               new Coordinate(kingPos[0].row, kingPos[0].col): 
               new Coordinate(kingPos[1].row, kingPos[1].col);
    }
}
