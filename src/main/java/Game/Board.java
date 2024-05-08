package Game;

import java.awt.Color;
import java.awt.Graphics2D;

import Piece.Bishop;
import Piece.King;
import Piece.Knight;
import Piece.Pawn;
import Piece.Queen;
import Piece.Rook;
import Util.CONSTANTS;

public class Board {

    public static Square[][] rep;

    public Board() {
        rep = new Square[CONSTANTS.ROWS][CONSTANTS.COLS];
        initBoard();
        setPieces(0); setPieces(1);
        
    }

    // Renders the Board
    public void draw(Graphics2D g2) {
        for(int row = 0; row < CONSTANTS.ROWS; row++) {
            for(int col = 0; col < CONSTANTS.COLS; col++) {

                Color c = (row + col) % 2 == 0 ? 
                          new Color(234,235,200) : 
                          new Color(119,154,88);

                g2.setColor(c);

                g2.fillRect(col*CONSTANTS.SQSIZE, 
                            row*CONSTANTS.SQSIZE, 
                            CONSTANTS.SQSIZE, 
                            CONSTANTS.SQSIZE);
            }
        }
    }

    private void initBoard() {
        for(int row = 0; row < CONSTANTS.ROWS; row++) {
            for(int col = 0; col < CONSTANTS.COLS; col++) {
                rep[row][col] = new Square(row, col);
            }
        }
    }

    private void setPieces(int color) {
        int row_pawn = (color == CONSTANTS.WHITE) ? 6 : 1;
        int row_other = (color == CONSTANTS.WHITE) ? 7 : 0;

        // Setting Pawns
        for(int col = 0; col < CONSTANTS.COLS; col++) {
            rep[row_pawn][col] = new Square(row_pawn, col, new Pawn(color, row_pawn, col));
        }

        // knights
        rep[row_other][1] = new Square(row_other, 1, new Knight(color, row_other, 1));
        rep[row_other][6] = new Square(row_other, 6, new Knight(color, row_other, 6));

        // bishops
        rep[row_other][2] = new Square(row_other, 2, new Bishop(color, row_other, 2));
        rep[row_other][5] = new Square(row_other, 5, new Bishop(color, row_other, 5));

        // rooks
        rep[row_other][0] = new Square(row_other, 0, new Rook(color, row_other, 0));
        rep[row_other][7] = new Square(row_other, 7, new Rook(color, row_other, 7));

        // queen
        rep[row_other][3] = new Square(row_other, 3, new Queen(color, row_other, 3));

        // king
        rep[row_other][4] = new Square(row_other, 4, new King(color, row_other, 4));
    }
}