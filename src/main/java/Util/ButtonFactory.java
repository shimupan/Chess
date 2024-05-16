package Util;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

public class ButtonFactory {
    public static JButton createButton(String text, Rectangle bounds, Font font, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBounds(bounds);
        button.setFont(font);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
}