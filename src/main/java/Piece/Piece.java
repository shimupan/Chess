package Piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import Game.Board;
import Game.Square;
import Util.CONSTANTS;

public abstract class Piece {
    public BufferedImage img;
    public int x, y;
    public int col, row, prevCol, prevRow;
    public int color;
    public double value;

    public Piece(int color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.prevCol = col;
        this.prevRow = row;
        this.x = getX(col);
        this.y = getY(row);
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
        
        // Update the Squares Effected
        Square Dest = Board.rep[row][col];
        Square Prev = Board.rep[prevRow][prevCol];
        
        if(Dest.equals(Prev)) return;

        Dest.updatePiece(Prev.getPiece());
        Prev.updatePiece(null);
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
            System.out.println(p.color != this.color);
            return p.color != this.color ? true : false;
        }
    }

    public abstract boolean canMove(int targetRow, int targetCol);

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
