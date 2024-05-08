package Game;

import java.awt.Color;
import java.awt.Graphics2D;

import Util.CONSTANTS;

public class Board {

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
}
