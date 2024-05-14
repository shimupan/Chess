package Game;

import Piece.*;

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

    public Square(Square other) {
        this.row = other.row;
        this.col = other.col;
        if (other.piece != null) {
            if (other.piece instanceof Pawn) {
                this.piece = new Pawn((Pawn) other.piece);
            } else if (other.piece instanceof Rook) {
                this.piece = new Rook((Rook) other.piece);
            } else if (other.piece instanceof Knight) {
                this.piece = new Knight((Knight) other.piece);
            } else if (other.piece instanceof Bishop) {
                this.piece = new Bishop((Bishop) other.piece);
            } else if (other.piece instanceof Queen) {
                this.piece = new Queen((Queen) other.piece);
            } else if (other.piece instanceof King) {
                this.piece = new King((King) other.piece);
            }
        } else {
            this.piece = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Square)) {
            return false;
        }
        Square s = (Square) o;
        return this.row == s.row && this.col == s.col;
    }

    public boolean containsPiece() {
        return this.piece != null;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public void updatePiece(Piece p) {
        this.piece = p;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

}
