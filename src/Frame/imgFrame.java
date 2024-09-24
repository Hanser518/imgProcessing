package Frame;

import javax.swing.*;
import java.awt.*;

public class imgFrame {
    public static int screenWidth;
    public static int screenHeight;
    public static double rate = 0.6;

    public static void main(String[] args) {
        System.out.println("Hello image");
        init();
    }

    public static void init() {
        JFrame baseFrame = new JFrame("processingWindow");
        // 屏幕宽高
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        // 窗口宽高
        baseFrame.setBounds(
                (int) (screenWidth * (1 - rate) / 2),
                (int) (screenHeight * (1 - rate) / 2),
                (int) (screenWidth * rate),
                (int) (screenHeight * rate));
        baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        baseFrame.setVisible(true);
    }
}
