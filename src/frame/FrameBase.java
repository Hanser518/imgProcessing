package frame;

import controller.*;
import entity.IMAGE;
import frame.service.IFileService;
import frame.service.impl.IFileServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Collectors;

public class FrameBase {
    public static int screenWidth;
    public static int screenHeight;
    public static int frameWidth;
    public static int frameHeight;
    public static double rate = 0.6;

    private static JFrame baseFrame = new JFrame("processingWindow");
    private static JLabel imgLabel;
    private static JPanel sidePanel = new JPanel();

    private ArrayList<File> fileList = new ArrayList<>();
    private ArrayList<String> path = new ArrayList<>();
    private String path_head = "./";
    private String pathNow = null;

    private static IMAGE image = new IMAGE();

    private static IFileService fileService = new IFileServiceImpl();

    private static ProcessingController pcsCtrl = new ProcessingController();
    private static StylizeController styleCtrl = new StylizeController();
    private static AdjustController adCtrl = new AdjustController();
    private static EdgeController edgeCtrl = new EdgeController();
    private static BlurController blurCtrl = new BlurController();
    private static ImgController imgCtrl2 = new ImgController();


    public static void main(String[] args) {
        System.out.println("Hello image");
        SwingUtilities.invokeLater(() -> new FrameBase());

    }

    public FrameBase() {
        sidePanel.setBackground(new Color(220, 161, 128));
        initFileList();
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

        initImageLabel();
        baseFrame.add(imgLabel, BorderLayout.CENTER);
        baseFrame.add(headPanel(), BorderLayout.NORTH);
        baseFrame.add(sidePanel, BorderLayout.WEST);
        baseFrame.add(bottomPanel(), BorderLayout.SOUTH);
        baseFrame.setVisible(true);
    }

    private void initImageLabel() {
        imgLabel = new JLabel(new ImageIcon(pcsCtrl.resizeImage(image, rate, pcsCtrl.RESIZE_ENTIRETY).getImg()));
        imgLabel.addMouseListener(new MouseAdapter() {
            boolean press = false;

            @Override
            public void mousePressed(MouseEvent e) {
                press = true;
                System.out.println(press);
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + " " + y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                System.out.println(x + " " + y);
            }
        });

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

    /**
     * 初始化文件列表
     */
    private void initFileList() {
        fileList.clear();
        String path_head = "./";
        fileList = fileService.getFileList(path_head);
        updateSidePanel();
    }

    private void updateFileList() {
        fileList.clear();
        String filePath = "";
        if(path.isEmpty()){
            filePath = path_head;
        }else{
            filePath = path.get(path.size()-1);
        }
        fileList = fileService.getFileList(filePath);
        updateSidePanel();
    }

    public void updateSidePanel() {
        sidePanel.removeAll();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        JButton backBtn = new JButton("...");
        backBtn.addActionListener(action -> {
            if(!path.isEmpty()){
                path.remove(path.size()-1);
            }else{
                path_head = path_head.length() <= 2 ? '.' + path_head : path_head;
            }
            updateFileList();
        });
        sidePanel.add(backBtn);
        for (File file : fileList) {
            JButton fileBtn = new JButton((file.isDirectory() ? "D----" : "-D---") + file.getName());
            fileBtn.addActionListener(action -> {
                if (file.isDirectory()) {
                    path.add(file.getAbsolutePath());
                    updateFileList();
                } else if (file.isFile()) {
                    try {
                        pathNow = file.getAbsolutePath();
                        updateLabelImage(new IMAGE(file.getAbsolutePath(), 0));
                    } catch (IOException e) {
                    }
                }
            });
            sidePanel.add(fileBtn);
        }
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public JPanel headPanel() {
        JPanel headPanel = new JPanel();
        headPanel.setBackground(new Color(177, 123, 89));
        headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton reLoadBtn = new JButton("ReLoad");
        reLoadBtn.addActionListener(e -> {
            String fileName = "building";
            try {
                System.out.println(pathNow);
                updateLabelImage(new IMAGE(pathNow, 0));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        headPanel.add(reLoadBtn);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> updateLabelImage(new IMAGE()));
        headPanel.add(closeButton);

        JButton blurBtn = new JButton("Blur");
        blurBtn.addActionListener(action -> {
            IMAGE gas = blurCtrl.getGasBlur(image, 11, 32);
            updateLabelImage(gas);
        });
        headPanel.add(blurBtn);


        headPanel.add(edgeBox());
        headPanel.add(grilleBox());
        headPanel.add(StyleBox());

        JButton sideButton = new JButton("SIDE");
        sideButton.addActionListener(action -> {
            updateSidePanel();
        });
        headPanel.add(sideButton);


        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(action -> {
            baseFrame.dispose();
        });
        headPanel.add(exitButton);


        return headPanel;
    }

    public JPanel bottomPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(177, 123, 89));
        southPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        JButton cancelBtn = new JButton("Cancel");
        southPanel.add(cancelBtn);

        JButton retryBtn = new JButton("Retry");
        southPanel.add(retryBtn);

        return southPanel;
    }

    public JPanel grilleBox() {
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
            switch (select) {
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

    public JPanel edgeBox() {
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
            switch (select) {
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

    public JPanel StyleBox() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(177, 123, 89));
        JLabel messageLabel = new JLabel("Stylize:");
        panel.add(messageLabel);

        String[] constellations = {
                "Paper", "Oil"
        };
        JComboBox comboBox = new JComboBox(constellations);
        comboBox.addActionListener(action -> {
            String select = (String) comboBox.getSelectedItem();
            switch (select) {
                case "Paper" -> {
                    IMAGE reStyle = styleCtrl.transPaperStyle(image, 24, 118);
                    updateLabelImage(reStyle);
                }
                case "Oil" -> {
                    IMAGE reStyle = new IMAGE();
                    try {
                        reStyle = styleCtrl.transOilPaintingStyle(image);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    updateLabelImage(reStyle);
                }
            }
        });
        panel.add(comboBox);
        return panel;
    }
}
