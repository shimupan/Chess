package Main;

import javax.swing.JFrame;

public class Window {
    public static JFrame MainWindow;

    public static void init() {
        Window.MainWindow = new JFrame("Chess");
        Window.MainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Window.MainWindow.setResizable(false);
        Window.MainWindow.add(Sidebar.sideBarPane);
        Window.MainWindow.pack();
    
        Window.MainWindow.setLocationRelativeTo(null);
        Window.MainWindow.setVisible(true);
    }
}
