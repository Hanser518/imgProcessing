package frame2.constant;


import frame.entity.Param;
import frame2.annotation.InitMethod;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

public class JFrameModuleAndParam {

    public static double rate = 0.5;

    public static int screenWidth = 640;

    public static int screenHeight = 640;

    public static int frameWidth = 480;

    public static int frameHeight = 480;

    public static void initModules() {
        // 获取当前类的所有方法
        Method[] methods = JFrameModuleAndParam.class.getDeclaredMethods();
        // 遍历所有方法，找到标记了 @InitMethod 注解的方法并调用
        for (Method method : methods) {
            if (method.isAnnotationPresent(InitMethod.class)) {
                try {
                    method.invoke(new JFrameModuleAndParam());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void updateModules() {
        window.revalidate();
        window.repaint();
    }


    /**
     * window
     */
    public static JFrame window = new JFrame();

    @InitMethod
    private static void initWindow() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        frameWidth = (int) (screenWidth * rate);
        frameHeight = (int) (screenHeight * rate);
        // 窗口宽高
        window.setBounds(
                (int) (screenWidth * (1 - rate) / 2),
                (int) (screenHeight * (1 - rate) / 2),
                frameWidth,
                frameHeight + 100);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * operationPanel
     */
    public static JPanel operationPanel = new JPanel();

    @InitMethod
    private static void initOperationPanel() {
        operationPanel.setLayout(new BoxLayout(operationPanel, BoxLayout.Y_AXIS));
        operationPanel.setBackground(new Color(146, 187, 241));
    }

    /**
     * mainImageLabel
     */
    public static JLabel mainImageLabel = new JLabel();

    @InitMethod
    private static void initMainImageLabel() {
        mainImageLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
    }

    /**
     * headPanel
     */
    public static JPanel headPanel = new JPanel();

    @InitMethod
    private static void initHeaderPanel() {
        headPanel.setBackground(new Color(177, 123, 89));
        headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * buildFunctionButton
     */
    public static JButton buildFunctionButton(String btnName) {
        JButton button = new JButton(btnName);
        button.setFont(Param.funcFont);
        button.setContentAreaFilled(false);
        return button;
    }

}
