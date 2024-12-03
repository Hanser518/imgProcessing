import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DraggableLabelWithMenuExample {
    private static int labelCount = 0; // 记录 JLabel 数量
    private static JPanel panel; // 用于容纳 JLabel 的面板
    private static JPanel headerPanel; // 用于容纳 JLabel 的面板
    private static JLabel infoLabel;
    private static JLabel paramLabel;
    private static Point initialClick;
    private static ArrayList<JLabel> labelList = new ArrayList<>();

    private static double a1 = 0, b1 = 0, c1 = 0;
    private static double a2 = 0, b2 = 0, c2 = 0;

    private static class CustomPanel extends JPanel {
        public CustomPanel(Object o) {
            super(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (labelList.size() > 2) {
                int x1 = 0, x2 = 1000;
                int y11 = (int) (-(a1 * x1 + c1) / b1);
                int y12 = (int) (-(a1 * x2 + c1) / b1);
                g.setColor(new Color(28, 54, 124, 255));
                g.drawLine(x1, y11, x2, y12);

                int y21 = (int) (-(a2 * x1 + c2) / b2);
                int y22 = (int) (-(a2 * x2 + c2) / b2);
                g.setColor(new Color(0xFFF83B0A, true));
                g.drawLine(x1, y21, x2, y22);
            }
        }
    }

    public static void main(String[] args) {
        // 创建 JFrame 窗口
        JFrame frame = new JFrame("Draggable JLabel 示例（右键删除）");
        frame.setSize(1000, 1120);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 创建按钮
        JButton button = new JButton("创建 JLabel");
//        button.setBounds(20, 20, 150, 30);

        // 数据展示
        infoLabel = new JLabel("LabelCount" + labelList.size());
        infoLabel.setBorder(new TitledBorder(new EtchedBorder(), "COUNT"));
        paramLabel = new JLabel();
        paramLabel.setBorder(new TitledBorder(new EtchedBorder(), "PARAM INFO"));

        // 创建一个容器面板
        panel = new CustomPanel(null);
        panel.setBounds(0, 60, 1000, 1000);
        panel.setBackground(Color.LIGHT_GRAY);
        frame.add(panel);

        // 按钮点击事件：创建 JLabel
        button.addActionListener(e -> createDraggableLabel());

        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBounds(0, 0, 1000, 60);

        headerPanel.add(button);
        headerPanel.add(infoLabel);
        headerPanel.add(paramLabel);
        frame.add(headerPanel);

        // 显示窗口
        frame.setVisible(true);
    }

    private static void createDraggableLabel() {
        // 增加 JLabel 数量计数
        labelCount++;

        // 创建新的 JLabel
        JLabel label = new JLabel("JLabel " + labelList.size());
        label.setBounds((int) (Math.random() * 900), (int) (Math.random() * 900), 100, 100);
        label.setOpaque(true);
        label.setBackground(new Color((int) (Math.random() * 200 + 25), (int) (Math.random() * 200 + 25), (int) (Math.random() * 200 + 25), 127));
        label.setBorder(new TitledBorder(new EtchedBorder(), "JLabel " + labelList.size()));

        // 添加鼠标拖动事件
        addDraggableFunctionality(label);

        // 添加右键菜单功能
        addRightClickMenu(label);

        // 将 JLabel 添加到面板
        panel.add(label);
        panel.repaint();

        // 将 JLabel 添加到存储队列中
        labelList.add(label);
        updateLabelsDistance();
        infoLabel.setText("LabelCount: " + labelList.size());

    }

    private static void addDraggableFunctionality(JLabel label) {
        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    initialClick = e.getPoint(); // 记录初始点击位置
                }
            }

//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (SwingUtilities.isLeftMouseButton(e)) {
//                    updateLabelsDistance(); // 刷新组件之间的距离
//                }
//            }
        });

        label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // 获取当前鼠标在面板中的位置
                    int mouseX = e.getXOnScreen();
                    int mouseY = e.getYOnScreen();

                    // 计算 JLabel 的新位置
                    Point panelLocation = panel.getLocationOnScreen();
                    int newX = mouseX - panelLocation.x - initialClick.x;
                    int newY = mouseY - panelLocation.y - initialClick.y;

                    // 更新 JLabel 的位置
                    label.setLocation(newX, newY);
                    updateLabelsDistance(); // 刷新组件之间的距离
                }
            }
        });
    }

    private static void addRightClickMenu(JLabel label) {
        // 创建右键菜单
        JPopupMenu popupMenu = new JPopupMenu();

        // 创建删除菜单项
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> {
            // 从面板中移除 JLabel
            panel.remove(label);
            labelList.remove(label);
            infoLabel.setText("LabelCount: " + labelList.size());
            for (int index = 0; index < labelList.size(); index++) {
                labelList.get(index).setBorder(new TitledBorder(new EtchedBorder(), "JLabel " + index));
            }
            updateLabelsDistance();
            panel.repaint(); // 重新绘制面板
        });

        // 将菜单项添加到右键菜单
        popupMenu.add(deleteItem);

        // 为 JLabel 添加右键点击事件
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(label, e.getX(), e.getY()); // 显示右键菜单
                }
            }
        });
    }

    private static void updateLabelsDistance() {
        if (labelList.size() > 2) {
            JLabel label1 = labelList.get(0);
            JLabel label2 = labelList.get(1);
            Point p1 = label1.getLocationOnScreen();
            Point p2 = label2.getLocationOnScreen();
            label1.setText("<html>Label 0: <br>" + p1.getX() + "," + p1.getY());
            label2.setText("<html>Label 1: <br>" + p2.getX() + "," + p2.getY());
            // 计算基准线
            a1 = p1.getY() - p2.getY();
            b1 = p2.getX() - p1.getX();
            c1 = p1.getX() * p2.getY() - p2.getX() * p1.getY();
            // 计算垂直线
            a2 = b1;
            b2 = -a1;
            c2 = -b1 * p1.getX() + a1 * p1.getY();
            // 更新函数参数
            String param = String.format("<html>a1_%f，b1_%f，c1_%f<br>a2_%f，b2_%f，c1_%f", a1, b1, c1, a2, b2, c2);
            paramLabel.setText(param);
            // 在面板上绘制连线
            panel.repaint();
            // 预存储部分参数
            double param1 = Math.sqrt(a1 * a1 + b1 * b1);
            double param2 = Math.sqrt(a2 * a2 + b2 * b2);
            // 更新各不同点位到两直线的距离
            for (int i = 2; i < labelList.size(); i++) {
                JLabel label = labelList.get(i);
                Point labelPoint = label.getLocationOnScreen(); // 获取label坐标
                StringBuilder labelSB = new StringBuilder("<html>Label: " + i + "<br>");
                double dis1 = Math.abs(a1 * labelPoint.getX() + b1 * labelPoint.getY() + c1) / param1;
                double dis2 = Math.abs(a2 * labelPoint.getX() + b2 * labelPoint.getY() + c2) / param2;
                labelSB.append("DIS_1: ").append(String.format("%.2f", dis1)).append("<br>");
                labelSB.append("DIS_2: ").append(String.format("%.2f", dis2)).append("<br>");
                labelSB.append("</html>");
                label.setText(labelSB.toString());
            }
        } else {
            for (int i = 0; i < labelList.size(); i++) {
                JLabel label = labelList.get(i);
                Point labelPoint = label.getLocationOnScreen(); // 获取label坐标
                StringBuilder labelSB = new StringBuilder("<html>Label: " + i + "<br>");
                for (int index = 0; index < labelList.size(); index++) {
                    if (index == i) continue;
                    Point address = labelList.get(index).getLocationOnScreen();
                    double distance = Math.sqrt(Math.pow(address.x - labelPoint.x, 2) + Math.pow(address.y - labelPoint.y, 2));
                    distance = 1000 / distance;
                    labelSB.append("Label: ").append(index).append(" ").append(String.format("%.2f", distance)).append("<br>");
                }
                labelSB.append("</html>");
                label.setText(labelSB.toString());
            }
        }
    }
}
