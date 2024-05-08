package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Util.CONSTANTS;

public class Game extends JPanel implements Runnable {

    private Thread gameThread;
    private Board board;
    
    public Game() {
        setPreferredSize(new Dimension(CONSTANTS.WIDTH, CONSTANTS.HEIGHT));
        setBackground(Color.BLACK);

        board = new Board();
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
        board.draw( (Graphics2D) g );

        for(int row = 0; row < CONSTANTS.ROWS; row++) {
            for(int col = 0; col < CONSTANTS.COLS; col++) {
                Square currSquare = Board.rep[row][col];
                if(currSquare.containsPiece()) {
                    currSquare.getPiece().draw( (Graphics2D) g );;
                }
            }
        }
    }

    public void start() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update() {

    }
}
