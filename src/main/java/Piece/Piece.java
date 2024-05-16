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
import Util.*;

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
    public static Set<Piece> WhitePieces = new HashSet<>();
    public static Set<Piece> BlackPieces = new HashSet<>();

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
        Piece.copy(other, this);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Piece)) return false;
        Piece p = (Piece) o;
        return super.equals(p);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + (color == CONSTANTS.WHITE ? "White" : "Black") +
                ", type=" + getClass().getSimpleName() +
                ", row=" + row +
                ", col=" + col +
                ", moved=" + moved +
                '}';
    }

    public static Coordinate getKingPosByColor(int color) {
        return (color == CONSTANTS.WHITE) ? 
               new Coordinate(kingPos[0].row, kingPos[0].col): 
               new Coordinate(kingPos[1].row, kingPos[1].col);
    }

    public static boolean putKingInCheck(Piece p, Board board) {
        boolean check = false;
        int tempRow = p.prevRow;
        int tempCol = p.prevCol;
        p.prevRow = p.row;
        p.prevCol = p.col;
        p.getValidMoves(board, false);
        int opponentColor = (p.color == CONSTANTS.WHITE) ? CONSTANTS.BLACK : CONSTANTS.WHITE;
        if(p.validMoves.contains(Piece.getKingPosByColor(opponentColor))) {
            check = true;
        }

        p.prevRow = tempRow;
        p.prevCol = tempCol;

        return check;
    }

    public static void copy(Piece src, Piece dest) {
        dest.color = src.color;
        dest.row = src.row;
        dest.col = src.col;
        dest.prevCol = src.col;
        dest.prevRow = src.row;
        dest.x = src.x;
        dest.y = src.y;
        dest.moved = src.moved;
    }

    public static void swapPositionValues(Piece p) {
        if(p == null) return;
        
        int tmpRow = p.row;
        int tmpCol = p.col;
        p.row = p.prevRow;
        p.col = p.prevCol;
        p.prevCol = tmpCol;
        p.prevRow = tmpRow;
    }

    public void draw(Graphics2D g2) {
        int drawX = this.x;
        int drawY = this.y;
    
        // Ensure the piece is not drawn outside the intended area
        // if (drawX + CONSTANTS.SQSIZE > 800) {
        //     drawX = 800 - CONSTANTS.SQSIZE;
        // }
    
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

        // Sound Playing
        Boolean enpassantSound = false;
        Boolean castleSound = false;
        
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
                        
                    Sound.play("capture");
                    enpassantSound = true;

                    if (capturedSquare.getPiece().color == CONSTANTS.WHITE) {
                        WhitePieces.remove(capturedSquare.getPiece());
                    } else {
                        BlackPieces.remove(capturedSquare.getPiece());
                    }
                    capturedSquare.updatePiece(null);
                }
            }
        }

        // castle
        if(Piece.castlePC != null) {
            Sound.play("castle");
            castleSound = true;
        }

        // Update the Squares Effected
        Square Dest = board.rep[row][col];
        Square Prev = board.rep[prevRow][prevCol];
        
        if(Dest.equals(Prev)) return;

        if(!enpassantSound && !castleSound) {
            if(Dest.containsPiece()) {
                Sound.play("capture");
                if (Dest.getPiece().color == CONSTANTS.WHITE) {
                    WhitePieces.remove(Dest.getPiece());
                } else {
                    BlackPieces.remove(Dest.getPiece());
                }
            } else {
                Sound.play("move-self");
            }
        }
        
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
        boolean print = false;
        // Create a deep copy of the board
        Board boardCopy = new Board(board);
        
        // Simulate the move on the copied board
        boardCopy.getSquare(targetRow, targetCol).updatePiece(p);
        boardCopy.getSquare(this.prevRow, this.prevCol).updatePiece(null);
        if(print) { System.out.println(boardCopy); }
        // Find the king's position
        Coordinate kingCoordinate = Piece.getKingPosByColor(p.color);

        // If the piece being moved is a king, manually update kingCoordinate
        if (p instanceof King && p.color == p.color) {
            kingCoordinate = new Coordinate(targetRow, targetCol);
        }

        // Check if the king is in check
        boolean check = false;
        Set<Piece> opponentPieces = (p.color == CONSTANTS.WHITE) ? BlackPieces : WhitePieces;
        for (Piece pi : opponentPieces) {

            // Make a copy of the curr piece
            Piece tmp = null;
            if (pi instanceof Pawn) {
                tmp = new Pawn((Pawn) pi);
            } else if (pi instanceof Rook) {
                tmp = new Rook((Rook) pi);
            } else if (pi instanceof Knight) {
                tmp = new Knight((Knight) pi);
            } else if (pi instanceof Bishop) {
                tmp = new Bishop((Bishop) pi);
            } else if (pi instanceof Queen) {
                tmp = new Queen((Queen) pi);
            } else if (pi instanceof King) {
                tmp = new King((King) pi);
            }
            
            tmp.getValidMoves(boardCopy, false);
            for (Coordinate c : tmp.validMoves) {
                if (c.equals(kingCoordinate)) {
                    check = true;
                    break;
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
}
