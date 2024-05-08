package Game;

import Piece.Piece;

public class Square {

    private int row;
    private int col;
    private Piece piece;
    
    public Square(int row, int col, Piece piece) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    public boolean containsPiece() {
        return this.piece != null;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

}
