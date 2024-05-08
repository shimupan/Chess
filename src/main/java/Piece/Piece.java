package Piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import Util.CONSTANTS;

public abstract class Piece {
    public BufferedImage img;
    public int x, y;
    public int col, row;
    public int color;
    public double value;

    public Piece(int color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.x = getX(col);
        this.y = getY(row);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(img, x, y, CONSTANTS.SQSIZE, CONSTANTS.SQSIZE, null);
    }

    public BufferedImage loadImage(String path) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new FileInputStream(path + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public int getX(int col) {
        return col * CONSTANTS.SQSIZE;
    }

    public int getY(int row) {
        return row * CONSTANTS.SQSIZE;
    }
}
