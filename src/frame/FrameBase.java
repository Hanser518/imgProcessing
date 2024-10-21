package frame;

import controller.*;
import entity.IMAGE;
import frame.entity.SideItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FrameBase {
    public static int screenWidth;
    public static int screenHeight;
    public static int frameWidth;
    public static int frameHeight;
    public static double rate = 0.6;

    private static JFrame baseFrame = new JFrame("processingWindow");
    private static JLabel imgLabel;
    private static JPanel sidePanel = new JPanel();

    private ArrayList<SideItem> fileList = new ArrayList<>();

    private static IMAGE image = new IMAGE();

    private static final ProcessingController pcsCtrl = new ProcessingController();
    private static AdjustController adCtrl = new AdjustController();
    private static BlurController blurCtrl = new BlurController();
    private static ImgController imgCtrl2 = new ImgController();
    private static EdgeController edgeCtrl = new EdgeController();
    private static StylizeController styleCtrl = new StylizeController();

    public static void main(String[] args) {
        System.out.println("Hello image");
        SwingUtilities.invokeLater(() -> new FrameBase());

    }

    public FrameBase() {
        updateSizePanel();
        // 屏幕宽高
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        frameWidth = (int) (screenWidth * rate);
        frameHeight = (int) (screenHeight * rate);
        // 窗口宽高
        baseFrame.setBounds(
                (int) (screenWidth * (1 - rate) / 2),
                (int) (screenHeight * (1 - rate) / 2),
                frameWidth,
                frameHeight + 100);
        baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        baseFrame.setLayout(new BorderLayout());
        imgLabel = new JLabel(new ImageIcon(pcsCtrl.resizeImage(image, rate, pcsCtrl.RESIZE_ENTIRETY).getImg()));

        baseFrame.add(imgLabel, BorderLayout.CENTER);
        baseFrame.add(headPanel(), BorderLayout.NORTH);
        baseFrame.add(sidePanel, BorderLayout.WEST);
        baseFrame.add(bottomPanel(), BorderLayout.SOUTH);
        baseFrame.setVisible(true);
    }

    private void initList(){
        fileList.clear();
        for(int i = 0;i < 10;i ++){
            fileList.add(new SideItem(("test" + Math.random()), null));
        }
    }

    public void updateLabelImage(IMAGE newImage) {
        // 获取图像宽高，计算比例
        int imgWidth = newImage.getWidth();
        int imgHeight = newImage.getHeight();
        double imgRate = Math.min((double) frameWidth / imgWidth, (double) frameHeight / imgHeight);
        image = newImage;
        imgLabel.setIcon(new ImageIcon(pcsCtrl.resizeImage(newImage, imgRate, pcsCtrl.RESIZE_ENTIRETY).getImg()));
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public void updateSizePanel(){
        sidePanel.removeAll();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        initList();
        for(SideItem file : fileList){
            JButton fileButton = new JButton(file.getItemName());
            fileButton.addActionListener(action -> {
                Method method = file.getMethod();
                try {
                    method.invoke(image);
                } catch (Exception e) {
                }
            });
            sidePanel.add(fileButton);
        }
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public JPanel headPanel() {
        JPanel headPanel = new JPanel();
        headPanel.setBackground(new Color(177, 123, 89));
        headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton readButton = new JButton("Load");
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = "bus";
                try {
                    updateLabelImage(new IMAGE(fileName + ".jpg"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        headPanel.add(readButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLabelImage(new IMAGE());
            }
        });
        headPanel.add(closeButton);

        JButton blurBtn = new JButton("Blur");
        blurBtn.addActionListener(action -> {
            IMAGE gas = blurCtrl.getGasBlur(image, 11, 32);
            updateLabelImage(gas);
        });
        headPanel.add(blurBtn);


        headPanel.add(edgeBox());
        headPanel.add(grilleBox());

        JButton PaperBtn = new JButton("Paper");
        PaperBtn.addActionListener(action -> {
            IMAGE grille;
            grille = styleCtrl.transPaperStyle(image, 24, 118);
            updateLabelImage(grille);
        });
        headPanel.add(PaperBtn);

        JButton sideButton = new JButton("SIDE");
        sideButton.addActionListener(action -> {
            updateSizePanel();
        });
        headPanel.add(sideButton);


        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(action -> {
            baseFrame.dispose();
        });
        headPanel.add(exitButton);
        
        
        return headPanel;
    }

    public JPanel bottomPanel(){
        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(177, 123, 89));
        southPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        JButton cancelBtn = new JButton("Cancel");
        southPanel.add(cancelBtn);

        JButton retryBtn = new JButton("Retry");
        southPanel.add(retryBtn);

        return southPanel;
    }

    public JPanel grilleBox(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(177, 123, 89));
        JLabel messageLabel = new JLabel("Grille:");
        panel.add(messageLabel);

        String[] constellations = {
                "Regular", "Medium", "Bold"
        };
        JComboBox comboBox = new JComboBox(constellations);
        comboBox.addActionListener(action -> {
            String select = (String) comboBox.getSelectedItem();
            switch(select) {
                case "Regular" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_REGULAR, false);
                    updateLabelImage(grille);
                }
                case "Medium" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_MEDIUM, false);
                    updateLabelImage(grille);
                }
                case "Bold" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_BOLD, false);
                    updateLabelImage(grille);
                }
            }
        });
        panel.add(comboBox);
        return panel;
    }

    public JPanel edgeBox(){
        JPanel panel = new JPanel();
        panel.setBackground(new Color(177, 123, 89));
        JLabel messageLabel = new JLabel("Edge:");
        panel.add(messageLabel);

        String[] constellations = {
                "Sobel", "Prewitt", "Mar"
        };
        JComboBox comboBox = new JComboBox(constellations);
        comboBox.addActionListener(action -> {
            String select = (String) comboBox.getSelectedItem();
            switch(select) {
                case "Sobel" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(image, EdgeController.SOBEL);
                    } catch (Exception e) {
                    }
                    updateLabelImage(edge);
                }
                case "Prewitt" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(image, EdgeController.PREWITT);
                    } catch (Exception e) {
                    }
                    updateLabelImage(edge);
                }
                case "Mar" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(image, EdgeController.MARR);
                    } catch (Exception e) {
                    }
                    updateLabelImage(edge);
                }
            }
        });
        panel.add(comboBox);
        return panel;
    }
}
