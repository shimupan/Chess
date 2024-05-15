package Game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import Controls.*;
import Piece.*;
import Util.*;

public class Game extends JPanel implements Runnable {

    private Thread gameThread;
    private Board board;
    private Mouse mouse;

    private Square activeSQ = null;
    private Piece activePC = null;
    private Piece promotionPC = null;
    private ArrayList<Piece> promoPCLst = new ArrayList<>(4);

    private Square hoveredSquare = null;
    private Square previousMoveLocation = null;
    private Square currentMoveLocation = null;
    
    private int currColor = CONSTANTS.WHITE;
    private int halfmoveClock = 0;
    private int moves = 1;
    private boolean canMove = false;
    private boolean validSquare = false;
    private boolean clearEnPassantNextTurn = false;

    public Game() {
        setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
        setBackground(Color.BLACK);

        board = new Board();
        this.init("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private void init(String fen) {
        String[] gameState = board.loadFEN(fen);
        this.currColor = gameState[1].equals("w") ? CONSTANTS.WHITE : CONSTANTS.BLACK;
        
        this.halfmoveClock = Integer.parseInt(gameState[4]);
        this.moves = Integer.parseInt(gameState[5]);
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
                update();
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
        if(this.promotionPC != null) {
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

    private void update() {
        if(this.promotionPC != null) {
            if(mouse.pressed && mouse.x <= 800) {
                int clicked_row = mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = mouse.x/CONSTANTS.SQSIZE;

                for (Piece pc : promoPCLst) {
                    if (pc.row == clicked_row && pc.col == clicked_col) {
                        // Promotion piece was clicked, handle accordingly
                        board.handlePromotion(this.promotionPC.row, this.promotionPC.col, pc);
                        this.promotionPC = null;
                        promoPCLst.clear();
                        swapTurn();
                        repaint();
                        break;
                    }
                }

            }
            return;
        }
        // Mouse Press
        if(mouse.pressed && mouse.x <= 800) {
            if(this.activeSQ == null || this.activePC == null) { // No square selected
                int clicked_row = mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = mouse.x/CONSTANTS.SQSIZE;
                this.activeSQ = board.getSquare(clicked_row, clicked_col);
                // Square has a piece on it and the piece is the curr turn
                if(this.activeSQ.containsPiece() && 
                this.activeSQ.getPiece().color == currColor) {
                    this.activePC = activeSQ.getPiece();
                    this.activePC.prevCol = this.activePC.col;
                    this.activePC.prevRow = this.activePC.row;
                    this.hoveredSquare = this.activeSQ;
                }
            } else { // Square selected
                simulate(); 
            }
        }

        // Mouse Release
        if(!mouse.pressed) {
            if(this.activePC != null) {
                if (this.validSquare) {

                    boolean castle = false, check = false;

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
                        castle = true;
                        Sound.play("castle");
                        Piece.castlePC.updatePos(this.board);
                        Piece.castlePC = null;
                    }

                    // Check the pseudo legal moves
                    if(Piece.putKingInCheck(this.activePC, this.board)) {
                        check = true;
                        Sound.play("move-check");
                    }

                    // en-passant
                    if (clearEnPassantNextTurn) {
                        Piece.enpassantPieces.clear();
                    }
                    clearEnPassantNextTurn = !Piece.enpassantPieces.isEmpty();
                    
                    // sound for normal move if no castle
                    if(!castle && !check) Sound.play("move-self");

                    // Check if the move moved the pawn 
                    if(Type.isPawn(this.activePC)) {
                        int promotionRank = this.activePC.color == CONSTANTS.WHITE ? 0 : 7;
                        if(this.activePC.row == promotionRank) {
                            this.promotionPC = this.activePC;
                        }
                    }

                    // Promotion
                    if (this.promotionPC == null) { swapTurn(); }
                } else {
                    Sound.play("illegal");
                    this.activePC.resetPos();
                }
                this.activePC = null;
                this.activeSQ = null;
                repaint();
            }
        }
    }

    private void simulate() {

        this.canMove = false;
        this.validSquare = false;

        if(Piece.castlePC != null) {
            Piece.castlePC.col = Piece.castlePC.prevCol;
            Piece.castlePC.x = Piece.castlePC.getX(Piece.castlePC.col);
            Piece.castlePC = null;
        }

        // Dragging
        this.activePC.x = mouse.x - (CONSTANTS.SQSIZE/2);
        this.activePC.y = mouse.y - (CONSTANTS.SQSIZE/2);

        this.activePC.col = activePC.getCol(activePC.x);
        this.activePC.row = activePC.getCol(activePC.y);
        repaint();
        // Checking if valid move
        this.activePC.getValidMoves(board, true);
        if (this.activePC.validMoves.contains(new Coordinate(this.activePC.row, this.activePC.col))) {
            this.canMove = true;
            this.validSquare = true;
            handleCastling();
        } else {
            this.canMove = false;
            this.validSquare = false;
        }
    }

    private void swapTurn() {
        this.currColor = (this.currColor == CONSTANTS.WHITE) ? CONSTANTS.BLACK : CONSTANTS.WHITE;
    }

    private void handleCastling() {
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
}
