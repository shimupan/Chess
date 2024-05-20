package Game;

import java.awt.Color;
import java.awt.Graphics2D;

import Piece.*;
import Util.CONSTANTS;
import Util.Coordinate;
import Util.Type;

public class Board {

    public Square[][] rep;

    public Board() {
        rep = new Square[CONSTANTS.ROWS][CONSTANTS.COLS];
        initBoard();
        setPieces(0); setPieces(1);
    }

    public Board(Board other) {
        this.rep = new Square[CONSTANTS.ROWS][CONSTANTS.COLS];
        for(int row = 0; row < CONSTANTS.ROWS; row++) {
            for(int col = 0; col < CONSTANTS.COLS; col++) {
                this.rep[row][col] = new Square(other.rep[row][col]);
            }
        }
    }

    public static Coordinate algebraicToSquare(String algebraic) {
        int row = algebraic.charAt(1) - '1';
        int col = algebraic.charAt(0) - 'a';
        return new Coordinate(row, col);
    }

    public static String squareToAlgebraic(Coordinate c) {
        char col = (char) (c.col + 'a');
        char row = (char) (8 - c.row + '0');
        return "" + col + row;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Print column numbers
        sb.append("\n");
        sb.append("  ");
        for (int col = 0; col < CONSTANTS.COLS; col++) {
            sb.append(col).append(" ");
        }
        sb.append("\n");

        for (int row = 0; row < CONSTANTS.ROWS; row++) {
            // Print row number
            sb.append(row).append(" ");

            for (int col = 0; col < CONSTANTS.COLS; col++) {
                if (rep[row][col].containsPiece()) {
                    Piece piece = rep[row][col].getPiece();
                    if (Type.isPawn(piece)) {
                        sb.append("P ");
                    } else if (Type.isQueen(piece)) {
                        sb.append("Q ");
                    } else if (Type.isKing(piece)) {
                        sb.append("K ");
                    } else if (Type.isRook(piece)) {
                        sb.append("R ");
                    } else if (Type.isBishop(piece)) {
                        sb.append("B ");
                    } else if (Type.isKnight(piece)) {
                        sb.append("N ");
                    }
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public String[] loadFEN(String fen) {
        Piece.WhitePieces.clear();
        Piece.BlackPieces.clear();

        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/");
    
        // Clear the board
        initBoard();
    
        for (int i = 0; i < CONSTANTS.ROWS; i++) {
            int col = 0;
            for (char c : ranks[7 - i].toCharArray()) { // FEN starts from rank 8
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c); // Skip empty squares
                } else {
                    int color = Character.isLowerCase(c) ? CONSTANTS.BLACK : CONSTANTS.WHITE;
                    Piece piece;
                    switch (Character.toLowerCase(c)) {
                        case 'p': piece = new Pawn(color, 7 - i, col); break;
                        case 'n': piece = new Knight(color, 7 - i, col); break;
                        case 'b': piece = new Bishop(color, 7 - i, col); break;
                        case 'r': piece = new Rook(color, 7 - i, col); break;
                        case 'q': piece = new Queen(color, 7 - i, col); break;
                        case 'k': piece = new King(color, 7 - i, col); break;
                        default: throw new IllegalArgumentException("Invalid FEN string");
                    }
                    
                    if(Type.isPawn(piece)) { // If the pawn has moved, it cannot do en passant
                        boolean hasMoved = (piece.color == CONSTANTS.WHITE && piece.row != 6) || (piece.color == CONSTANTS.BLACK && piece.row != 1);
                        piece.moved = hasMoved;
                    } else if(Type.isKing(piece)) { // store king pos
                        if(piece.color == CONSTANTS.WHITE) {
                            Piece.kingPos[0] = piece;
                            boolean hasMoved = !parts[2].contains("K");
                            piece.moved = hasMoved;
                        } else {
                            Piece.kingPos[1] = piece;
                            boolean hasMoved = !parts[2].contains("k");
                            piece.moved = hasMoved;
                        }
                    } else if(Type.isRook(piece)) { // check rooks castling rights
                        boolean hasMoved = ((piece.color == CONSTANTS.WHITE) && ((piece.col == 0 && !parts[2].contains("Q")) || (piece.col == 7 && !parts[2].contains("K"))))
                        || ((piece.color == CONSTANTS.BLACK) && ((piece.col == 0 && !parts[2].contains("q")) || (piece.col == 7 && !parts[2].contains("k"))));
                        piece.moved = hasMoved;
                    }
                    
                    if(piece.color == CONSTANTS.WHITE) {
                        Piece.WhitePieces.add(piece);
                    } else {
                        Piece.BlackPieces.add(piece);
                    }

                    rep[7 - i][col] = new Square(7 - i, col, piece);
                    col++;
                }
            }
        }
        
        Coordinate enPassCoordinate = parts[3].equals("-") ? null : Board.algebraicToSquare(parts[3]);

        
        if(enPassCoordinate != null) {
            // Set en passant target square
            Piece enPassantPC = this.getPiece(enPassCoordinate.row, enPassCoordinate.col);
            Piece.enpassantPiece = enPassantPC;
        }
        
        return parts;
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
                Square currSquare = rep[row][col];
                if(currSquare.containsPiece()) {
                    currSquare.getPiece().draw( g2 );
                }
            }
        }
    }
    

    public Square getSquare(int row, int col) {
        if(!Piece.inBound(row, col)) return null;
        
        return rep[row][col];
    }

    public Piece getPiece(int row, int col) {
        if(!Piece.inBound(row, col)) return null;
        return rep[row][col].getPiece();
    }

    public boolean containsPiece(int row, int col) {
        if(!Piece.inBound(row, col)) return false;
        return rep[row][col].containsPiece();
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

    public void handlePromotion(int row, int col, Piece promoPiece) {
        Piece.copy(this.getPiece(row, col), promoPiece);
        this.getSquare(row, col).updatePiece(promoPiece);
    }

}
