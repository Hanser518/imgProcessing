import javax.swing.*;

public class SwingMenuBarExample {
    public static void main(String[] args) {
        // 创建 JFrame 窗口
        JFrame frame = new JFrame("Swing 菜单栏示例");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建菜单
        JMenu fileMenu = new JMenu("文件(F)");
        JMenu editMenu = new JMenu("编辑(E)");
        JMenu viewMenu = new JMenu("视图(V)");
        JMenu navigateMenu = new JMenu("导航(N)");
        JMenu codeMenu = new JMenu("代码(C)");
        JMenu buildMenu = new JMenu("构建(B)");
        JMenu runMenu = new JMenu("运行(R)");

        // 创建菜单项（文件菜单的示例）
        JMenuItem newFile = new JMenuItem("新建");
        JMenuItem openFile = new JMenuItem("打开");
        JMenuItem saveFile = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("退出");


        // 将菜单项添加到文件菜单
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator(); // 添加分隔符
        fileMenu.add(exitItem);

        // 将菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(navigateMenu);
        menuBar.add(codeMenu);
        menuBar.add(buildMenu);
        menuBar.add(runMenu);

        // 设置菜单栏到 JFrame
        frame.setJMenuBar(menuBar);

        // 显示窗口
        frame.setVisible(true);
    }
}
