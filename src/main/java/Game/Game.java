package Game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JPanel;

import Controls.*;
import Piece.*;
import Player.*;
import Util.*;

public class Game extends JPanel implements Runnable {

    private static Game instance = null;

    private Thread gameThread;
    private Board board;
    private Board AIBoard;

    public Mouse mouse;

    public Piece promotionPC = null;
    public ArrayList<Piece> promoPCLst = new ArrayList<>(4);

    public int currColor = CONSTANTS.WHITE;

    public Piece activePC = null;
    public Square hoveredSquare = null;
    public Square previousMoveLocation = null;
    public Square currentMoveLocation = null;
    public Square activeSQ = null;
    
    public boolean clearEnPassantNextTurn = false;
    
    public boolean canMove = false;
    public boolean validSquare = false;

    private int halfmoveClock = 0;
    private int moves = 1;

    private Player white;
    private Player black;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    private Game() {
        setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
        setBackground(Color.BLACK);
    }

    public void init(String fen, Enums.PlayerType PlayerTypeWhite, Enums.PlayerType PlayerTypeBlack) {
        this.board = new Board();
        this.AIBoard = new Board(this.board);
        this.mouse = new Mouse();
        addMouseListener(this.mouse);
        addMouseMotionListener(this.mouse);

        // default fen
        if(fen == "") fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        String[] gameState = board.loadFEN(fen);
        this.currColor = gameState[1].equals("w") ? CONSTANTS.WHITE : CONSTANTS.BLACK;
        
        this.halfmoveClock = Integer.parseInt(gameState[4]);
        this.moves = Integer.parseInt(gameState[5]);

        if(PlayerTypeWhite == Enums.PlayerType.Human) {
            this.white = new Human(this.board);
        } else if (PlayerTypeWhite == Enums.PlayerType.AI) {
            this.white = new AI(this.board, this.AIBoard);
        }
        
        if(PlayerTypeBlack == Enums.PlayerType.Human) {
            this.black = new Human(this.board);
        } else if (PlayerTypeBlack == Enums.PlayerType.AI) {
            this.black = new AI(this.board, this.AIBoard);
        }
    }

    public void start() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // Game Loop
    @Override
    public void run() {
        double drawInterval = 1000000000 / CONSTANTS.FPS;
        double delta = 0;
        long prevTime = System.nanoTime();
        long currTime;
        Sound.play("game-start");
        while(gameThread != null) {
            currTime = System.nanoTime();
            delta += (currTime - prevTime)/drawInterval;
            prevTime = currTime;

            if(delta >= 1) {
                sleep(15);

                Player p = getCurrPlayer();
                p.makeMove();
                
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent( g );

        Graphics2D g2 = (Graphics2D) g;

        board.draw( g2 );

        // Highlight the current move set
        if(this.previousMoveLocation != null && this.currentMoveLocation != null) {
            g2.setColor(Color.YELLOW.brighter());
            g2.fillRect(this.previousMoveLocation.getCol() * CONSTANTS.SQSIZE, 
                        this.previousMoveLocation.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            g2.fillRect(this.currentMoveLocation.getCol() * CONSTANTS.SQSIZE, 
                        this.currentMoveLocation.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            
            this.currentMoveLocation.getPiece().draw(g2);
        }

        // Color hovered square
        if(this.hoveredSquare != null) {
            g2.setColor(Color.YELLOW.brighter());
            g2.fillRect(this.hoveredSquare.getCol() * CONSTANTS.SQSIZE, 
                        this.hoveredSquare.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            this.hoveredSquare.getPiece().draw(g2);

            if(this.activePC != null) {
                this.activePC.getValidMoves(this.board, true);
                for(Coordinate c: this.activePC.validMoves) {
                    // Set color to semi-transparent gray
                    g2.setColor(new Color(128, 128, 128, 128)); // RGBA values

                    // Calculate the center of the square
                    int centerX = c.col * CONSTANTS.SQSIZE + CONSTANTS.SQSIZE / 2;
                    int centerY = c.row * CONSTANTS.SQSIZE + CONSTANTS.SQSIZE / 2;

                    // Draw a circle in the center of the square
                    g2.fillOval(centerX - CONSTANTS.SQSIZE / 8, 
                                centerY - CONSTANTS.SQSIZE / 8, 
                                CONSTANTS.SQSIZE / 4, 
                                CONSTANTS.SQSIZE / 4);

                    if(board.getPiece(c.row, c.col) != null) {
                        g2.setColor(Color.RED.brighter());
                        g2.fillRect(c.col * CONSTANTS.SQSIZE, 
                                    c.row * CONSTANTS.SQSIZE, 
                                    CONSTANTS.SQSIZE, 
                                    CONSTANTS.SQSIZE);
                        board.getPiece(c.row, c.col).draw(g2);
                    }
                }
            }
        }

        // Draw the piece moving
        if(this.activePC != null) {
            if(this.canMove) {
                // Draw a border around the square
                g2.setColor(Color.WHITE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                int borderThickness = 5;
                g2.setStroke(new BasicStroke(borderThickness)); // Set the thickness of the border
                g2.drawRect(this.activePC.col * CONSTANTS.SQSIZE + borderThickness / 2, 
                            this.activePC.row * CONSTANTS.SQSIZE + borderThickness / 2, 
                            CONSTANTS.SQSIZE - borderThickness, 
                            CONSTANTS.SQSIZE - borderThickness);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
            }
            this.activePC.draw(g2);
        }

        // handle promotion
        if(this.promotionPC != null && currPlayerIsHuman() ) {
            for (int i = 0; i <= 3; i++) {
                int offset = (this.promotionPC.color == CONSTANTS.WHITE) ? i : -i;
                Piece tempPiece;
                switch (i) {
                    case 0:
                        tempPiece = new Queen(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    case 1:
                        tempPiece = new Rook(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    case 2:
                        tempPiece = new Bishop(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    case 3:
                        tempPiece = new Knight(this.currColor, this.promotionPC.row + offset, this.promotionPC.col);
                        break;
                    default:
                        continue;
                }
                g2.setColor(Color.WHITE);
                g2.fillRect(tempPiece.getX(tempPiece.col), tempPiece.getY(tempPiece.row), CONSTANTS.SQSIZE, CONSTANTS.SQSIZE);
                g2.drawImage(tempPiece.img, tempPiece.getX(tempPiece.col), tempPiece.getY(tempPiece.row), CONSTANTS.SQSIZE, CONSTANTS.SQSIZE, null);
                promoPCLst.add(i, tempPiece);
            }
        }
    }

    public void swapTurn() {
        this.currColor = (this.currColor == CONSTANTS.WHITE) ? CONSTANTS.BLACK : CONSTANTS.WHITE;
    }

    public void handlePieceSelection(int pieceRow, int pieceCol) {
        this.activeSQ = board.getSquare(pieceRow, pieceCol);
        // Square has a piece on it and the piece is the curr turn
        if(this.activeSQ.containsPiece() && 
            this.activeSQ.getPiece().color == this.currColor) {
            this.activePC = activeSQ.getPiece();
            this.activePC.prevCol = this.activePC.col;
            this.activePC.prevRow = this.activePC.row;
            this.hoveredSquare = this.activeSQ;
        }
    }

    public void handlePiecePlacement() {
        if(this.validSquare) {
            // Update moved piece
            this.activePC.updatePos(this.board);
            
            // square highlighting
            int currRow = this.activePC.getRow(this.activePC.y);
            int currCol = this.activePC.getCol(this.activePC.x);
            this.currentMoveLocation = board.rep[currRow][currCol];
            
            int prevRow = this.activePC.prevRow;
            int prevCol = this.activePC.prevCol;
            this.previousMoveLocation = board.rep[prevRow][prevCol];
            this.hoveredSquare = null;

            // castle
            if(Piece.castlePC != null) {
                Piece.castlePC.updatePos(this.board);
                Piece.castlePC = null;
            }

            // Check the pseudo legal moves
            if(Piece.putKingInCheck(this.activePC, this.board)) {
                Sound.play("move-check");
            }

            // en-passant
            if (this.clearEnPassantNextTurn) {
                Piece.enpassantPieces.clear();
            }
            this.clearEnPassantNextTurn = !Piece.enpassantPieces.isEmpty();

            // Check if the move moved the pawn 
            if(Type.isPawn(this.activePC)) {
                int promotionRank = this.activePC.color == CONSTANTS.WHITE ? 0 : 7;
                if(this.activePC.row == promotionRank) {
                    this.promotionPC = this.activePC;
                }
            }
            // Promotion
            if (this.promotionPC == null) { this.swapTurn(); }

        } else {
            Sound.play("illegal");
            this.activePC.resetPos();
        }
        this.activePC = null;
        this.activeSQ = null;
        this.repaint();
    }

    public void handlePiecePromotion(int clickedRow, int clickedCol) {

        Set<Piece> pcs = (this.promotionPC.color == CONSTANTS.WHITE) ? Piece.WhitePieces : Piece.BlackPieces;
        for (Piece pc : this.promoPCLst) {
            if (pc.row == clickedRow && pc.col == clickedCol) {
                // Promotion piece was clicked, handle accordingly
                this.board.handlePromotion(this.promotionPC.row, this.promotionPC.col, pc);
                pcs.add(pc);
                break;
            }
        }
        pcs.remove(this.promotionPC);
        this.promotionPC = null;
        this.promoPCLst.clear();
        this.swapTurn();
        this.repaint();
        return;
    }
    
    public void handleCastling() {
        if(Piece.castlePC != null) {
            // Check which side rook is being castled
            if(Piece.castlePC.col == 0) { // left rook
                Piece.castlePC.col += 3;
            } else {
                Piece.castlePC.col -= 2;
            }
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean currPlayerIsHuman() {
        return ( (this.currColor == CONSTANTS.WHITE && this.white instanceof Human) || 
                 (this.currColor == CONSTANTS.BLACK && this.black instanceof Human) );
    }

    private Player getCurrPlayer() {
        if(this.currColor == CONSTANTS.WHITE) {
            return this.white;
        } else {
            return this.black;
        }
    }

}
