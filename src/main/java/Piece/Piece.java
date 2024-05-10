package Piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import Game.Board;
import Game.Square;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Sound;

public abstract class Piece {

    public BufferedImage img;
    public int x, y;
    public int col, row, prevCol, prevRow;
    public int color;
    public double value;
    public boolean moved;
    public List<Coordinate> validMoves = new ArrayList<>();

    public static Piece castlePC;
    public static List<Piece> enpassantPieces = new ArrayList<>();

    public abstract boolean canMove(int targetRow, int targetCol);
    public abstract void getValidMoves();

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

    public void updatePos() {
        this.x = getX(col);
        this.y = getY(row);
        this.col = getCol(x);
        this.row = getRow(y);
        
        // If its a pawn 
        if(this.value == 1.0) {
            // check if it moved 2 squares
            if(Math.abs(this.row - this.prevRow) == 2) {
                Piece.enpassantPieces.add(this);
            }
            // check if it did enpassant
            if (Math.abs(this.col - this.prevCol) == 1) {
                int capturedRow = (this.color == CONSTANTS.WHITE) ? this.row + 1 : this.row - 1;
                Square capturedSquare = Board.rep[capturedRow][this.col];
                Square movedSquare = Board.rep[this.row][this.col];

                // If there is a pawn behind the current move and the moved square is empty
                if (movedSquare.getPiece() == null && 
                    capturedSquare.getPiece() != null && 
                    capturedSquare.getPiece().value == 1.0) {
                        
                    capturedSquare.updatePiece(null);
                    Sound.play("capture");
                    System.out.println("ENPASSANT");
                    return;
                }
            }
        }

        // Update the Squares Effected
        Square Dest = Board.rep[row][col];
        Square Prev = Board.rep[prevRow][prevCol];
        
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

    public Piece getCollidingPiece(int targetRow, int targetCol) {
        if(Board.getPiece(targetRow, targetCol) == this) {
            return null;
        }
        return Board.getPiece(targetRow, targetCol);
    }

    public boolean validSquare(int targetRow, int targetCol) {
        Piece p = getCollidingPiece(targetRow, targetCol);
        
        if(p == null) {
            return true;
        } else {
            return p.color != this.color ? true : false;
        }
    }

    public boolean sameSquare(int targetRow, int targetCol) {
        return (targetCol == this.prevCol && targetRow == this.prevRow);
    }

    public boolean pieceOnStraightLine(int targetRow, int targetCol) {
        
        // Left
        for(int col = this.prevCol-1; col > targetCol; col--) {
            if(Board.rep[targetRow][col].containsPiece()) {
                return true;
            }
        }

        // Right
        for(int col = this.prevCol+1; col < targetCol; col++) {
            if(Board.rep[targetRow][col].containsPiece()) {
                return true;
            }
        }

        // Up
        for(int row = this.prevRow-1; row > targetRow; row--) {
            if(Board.rep[row][targetCol].containsPiece()) {
                return true;
            }
        }

        // Down
        for(int row = this.prevRow+1; row < targetRow; row++) {
            if(Board.rep[row][targetCol].containsPiece()) {
                return true;
            }
        }

        return false;
    }

    public boolean pieceOnDiagonalLine(int targetRow, int targetCol) {
        
        if(targetRow < this.prevRow) { // upper left and right
            
            // upper left
            for(int col = this.prevCol-1; col > targetCol; col--) {
                int rowDiff = this.prevRow - Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && Board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }

            // upper right
            for(int col = this.prevCol+1; col < targetCol; col++) {
                int rowDiff = this.prevRow - Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && Board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }

        } else { // lower left and right

            // lower left
            for(int col = this.prevCol-1; col > targetCol; col--) {
                int rowDiff = this.prevRow + Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && Board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }

            // lower right
            for(int col = this.prevCol+1; col < targetCol; col++) {
                int rowDiff = this.prevRow - Math.abs(col - this.prevCol);
                if(inBound(rowDiff, col) && Board.rep[rowDiff][col].containsPiece()) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public boolean inBound(int targetRow, int targetCol) {
        return (targetRow < CONSTANTS.ROWS && 
                targetRow >= 0 && 
                targetCol < CONSTANTS.COLS && 
                targetCol >= 0);
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

    
}
