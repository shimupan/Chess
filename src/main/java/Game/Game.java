package Game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Controls.Mouse;
import Piece.Piece;
import Util.CONSTANTS;

public class Game extends JPanel implements Runnable {

    private Thread gameThread;
    private Board board;
    private Mouse mouse;

    private Square activeSQ = null;
    private Piece activePC = null;
    private int currColor = CONSTANTS.WHITE;

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
                update();
                repaint();
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(getGraphics());

        Graphics2D g2 = (Graphics2D) g;

        board.draw( g2 );

        for(int row = 0; row < CONSTANTS.ROWS; row++) {
            for(int col = 0; col < CONSTANTS.COLS; col++) {
                Square currSquare = Board.rep[row][col];
                if(currSquare.containsPiece()) {
                    currSquare.getPiece().draw( g2 );;
                }
            }
        }

        if(this.activePC != null) {
            g2.setColor(Color.WHITE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(this.activePC.col * CONSTANTS.SQSIZE, this.activePC.row*CONSTANTS.SQSIZE, CONSTANTS.SQSIZE, CONSTANTS.SQSIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));

            this.activePC.draw(g2);
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
                // Square has a piece on it
                if(this.activeSQ.containsPiece()) {
                    this.activePC = activeSQ.getPiece();
                    this.activePC.prevCol = this.activePC.col;
                    this.activePC.prevRow = this.activePC.row;
                }
            } else { // Square selected
                simulate(); 
            }

        }

        // Mouse Release
        if(!mouse.pressed) {
            if(this.activePC != null) {
                this.activePC.updatePos();
                this.activePC = null;
                this.activeSQ = null;
            }
        }
    }

    private void simulate() {
        // Dragging
        this.activePC.x = mouse.x - (CONSTANTS.SQSIZE/2);
        this.activePC.y = mouse.y - (CONSTANTS.SQSIZE/2);

        this.activePC.col = activePC.getCol(activePC.x);
        this.activePC.row = activePC.getCol(activePC.y);
    }
}
