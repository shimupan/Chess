package Game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Controls.Mouse;
import Piece.Piece;
import Util.CONSTANTS;

public class Game extends JPanel implements Runnable {

    private Thread gameThread;
    private Board board;
    private Mouse mouse;

    private Square activeSQ = null;
    private Piece activePC = null;

    private Square hoveredSquare = null;
    private Square previousMoveLocation = null;
    private Square currentMoveLocation = null;
    
    private int currColor = CONSTANTS.WHITE;
    private boolean canMove;
    private boolean validSquare;

    public Game() {
        setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
        setBackground(Color.BLACK);

        board = new Board();

        mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }
    
    // Game Loop
    @Override
    public void run() {
        double drawInterval = 1000000000 / CONSTANTS.FPS;
        double delta = 0;
        long prevTime = System.nanoTime();
        long currTime;

        while(gameThread != null) {
            currTime = System.nanoTime();
            delta += (currTime - prevTime)/drawInterval;
            prevTime = currTime;

            if(delta >= 1) {
                SwingUtilities.invokeLater(() -> {
                    update();
                    repaint();
                });
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(getGraphics());

        Graphics2D g2 = (Graphics2D) g;

        board.draw( g2 );

        // Draw the pieces
        for(int row = 0; row < CONSTANTS.ROWS; row++) {
            for(int col = 0; col < CONSTANTS.COLS; col++) {
                Square currSquare = Board.rep[row][col];
                if(currSquare.containsPiece()) {
                    currSquare.getPiece().draw( g2 );;
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

        if(this.hoveredSquare != null) {
            g2.setColor(Color.YELLOW.brighter());
            g2.fillRect(this.hoveredSquare.getCol() * CONSTANTS.SQSIZE, 
                        this.hoveredSquare.getRow() * CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE, 
                        CONSTANTS.SQSIZE);
            this.hoveredSquare.getPiece().draw(g2);
        }
    }

    public void start() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {

        // Mouse Press
        if(mouse.pressed && mouse.x <= 800) {
            if(this.activeSQ == null || this.activePC == null) { // No square selected
                int clicked_row = mouse.y/CONSTANTS.SQSIZE;
                int clicked_col = mouse.x/CONSTANTS.SQSIZE;
                this.activeSQ = Board.rep[clicked_row][clicked_col];
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
                    this.activePC.updatePos();

                    int currRow = this.activePC.getRow(this.activePC.y);
                    int currCol = this.activePC.getCol(this.activePC.x);
                    this.currentMoveLocation = Board.rep[currRow][currCol];
                    
                    int prevRow = this.activePC.prevRow;
                    int prevCol = this.activePC.prevCol;
                    this.previousMoveLocation = Board.rep[prevRow][prevCol];
                    swapTurn();
                } else {
                    this.activePC.resetPos();
                }
                this.activePC = null;
                this.activeSQ = null;
            }
        }
    }

    private void simulate() {

        this.canMove = false;
        this.validSquare = false;

        // Dragging
        this.activePC.x = mouse.x - (CONSTANTS.SQSIZE/2);
        this.activePC.y = mouse.y - (CONSTANTS.SQSIZE/2);

        this.activePC.col = activePC.getCol(activePC.x);
        this.activePC.row = activePC.getCol(activePC.y);

        // Checking if valid move
        if (this.activePC.canMove(this.activePC.row, this.activePC.col)) {
            this.canMove = true;
            this.validSquare = true;
        } else {
            this.canMove = false;
            this.validSquare = false;
        }
    }

    private void swapTurn() {
        this.currColor = (this.currColor == CONSTANTS.WHITE) ? CONSTANTS.BLACK : CONSTANTS.WHITE;
    }
}
