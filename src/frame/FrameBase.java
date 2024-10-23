package frame;

import controller.*;
import entity.IMAGE;
import frame.service.IFileService;
import frame.service.impl.IFileServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FrameBase {
    public static int screenWidth;
    public static int screenHeight;
    public static int frameWidth;
    public static int frameHeight;
    public static double rate = 0.6;
    public static double zoom = 1;

    private static JFrame baseFrame = new JFrame("processingWindow");
    private static JLabel centerLabel;
    private static JLabel previewLabel;
    private static JPanel fileChoosePanel = new JPanel();

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
        fileChoosePanel.setBackground(new Color(220, 161, 128));
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

        initCenterLabel();
        baseFrame.add(sidePanel(), BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(centerLabel);
        baseFrame.add(scrollPane, BorderLayout.CENTER);
        baseFrame.add(headPanel(), BorderLayout.NORTH);
        baseFrame.add(bottomPanel(), BorderLayout.SOUTH);
        baseFrame.setVisible(true);
    }

    private void initCenterLabel() {
        centerLabel = new JLabel(new ImageIcon(pcsCtrl.resizeImage(image, rate, pcsCtrl.RESIZE_ENTIRETY).getImg()));
        previewLabel = new JLabel();
        centerLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerLabel.addMouseListener(new MouseAdapter() {
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

    public void updateCenterLabel(IMAGE newImage) {
        centerLabel.setText(null);
        // 获取图像宽高，计算比例
        int imgWidth = newImage.getWidth();
        int imgHeight = newImage.getHeight();
        double imgRate = Math.min((double) frameWidth / imgWidth, (double) frameHeight / imgHeight) * zoom;
        image = newImage;
        centerLabel.setIcon(new ImageIcon(pcsCtrl.resizeImage(newImage, imgRate, pcsCtrl.RESIZE_ENTIRETY).getImg()));
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public void updateCenterLabel(String path) {
        centerLabel.setIcon(null);
        try {
            File f = new File(path);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder content = new StringBuilder("<html>");
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    content.append(line).append("<br>");
                } else {
                    break;
                }
            }
            content.append("</html>");
            centerLabel.setText(content.toString());
        } catch (Exception ignore) {
        }
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    private void initFileList() {
        fileList.clear();
        String path_head = "./";
        fileList = fileService.getFileList(path_head);
        updateSidePanel();
    }

    private void updateFileList() {
        fileList.clear();
        String filePath = "";
        if (path.isEmpty()) {
            filePath = path_head;
        } else {
            filePath = path.get(path.size() - 1);
        }
        fileList = fileService.getFileList(filePath);
        updateSidePanel();
    }

    private void fileChooser(File file) {
        String suffix = fileService.getFileType(file);
        pathNow = file.getAbsolutePath();
        switch (suffix) {
            case "JPG", "PNG" -> {
                try {
                    updateCenterLabel(new IMAGE(file.getAbsolutePath(), 0));
                } catch (IOException e) {
                }
            }
            default -> {
                updateCenterLabel(file.getAbsolutePath());
            }
        }
    }

    public void updateSidePanel() {
        fileChoosePanel.removeAll();
        fileChoosePanel.setLayout(new BoxLayout(fileChoosePanel, BoxLayout.Y_AXIS));
        JButton backBtn = new JButton("...");
        backBtn.addActionListener(action -> {
            if (!path.isEmpty()) {
                path.remove(path.size() - 1);
            } else {
                path_head = path_head.length() <= 2 ? '.' + path_head : path_head;
            }
            updateFileList();
        });
        fileChoosePanel.add(backBtn);
        for (File file : fileList) {
            JButton fileBtn = new JButton((file.isDirectory() ? ">>>D--" : "<<<A--") + file.getName());
            fileBtn.addActionListener(action -> {
                if (file.isDirectory()) {
                    path.add(file.getAbsolutePath());
                    updateFileList();
                } else if (file.isFile()) {
                    fileChooser(file);
                }
            });
            fileChoosePanel.add(fileBtn);
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
            System.out.println(pathNow);
            fileChooser(new File(pathNow));
        });
        headPanel.add(reLoadBtn);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> updateCenterLabel(new IMAGE()));
        headPanel.add(closeButton);

        JButton blurBtn = new JButton("Blur");
        blurBtn.addActionListener(action -> {
            IMAGE gas = blurCtrl.getGasBlur(image, 11, 32);
            updateCenterLabel(gas);
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

    public JScrollPane sidePanel() {
        JScrollPane scrollPane = new JScrollPane(fileChoosePanel);
        scrollPane.setMaximumSize(new Dimension(20, 20));
        return scrollPane;
    }

    public JPanel bottomPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(177, 123, 89));
        southPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        southPanel.add(zoomPanel());

        JButton cancelBtn = new JButton("Cancel");
        southPanel.add(cancelBtn);

        JButton retryBtn = new JButton("Retry");
        southPanel.add(retryBtn);

        return southPanel;
    }

    public JPanel zoomPanel() {
        JPanel panel = new JPanel();

        panel.setBackground(new Color(204, 134, 111));

        JButton plus = new JButton("+");
        JButton down = new JButton("-");
        JLabel zoomX = new JLabel();

        zoomX.setText(String.format("%2.2f", zoom));
        plus.addActionListener(ac -> {
            zoom += 0.1;
            zoomX.setText(String.format("%2.2f", zoom));
            updateCenterLabel(image);
            // fileChooser(new File(pathNow));
        });
        down.addActionListener(ac -> {
            zoom -= 0.1;
            zoomX.setText(String.format("%2.2f", zoom));
            updateCenterLabel(image);
            // fileChooser(new File(pathNow));
        });
        JSlider slider = new JSlider(50, 250, (int) (zoom * 100));
        slider.addChangeListener(change -> {
            int value = slider.getValue();
            zoom = value / 100.0;
            zoomX.setText(String.format("%2.2f", zoom));
        });
        slider.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                updateCenterLabel(image);
            }
        });

        panel.add(slider);
        panel.add(plus);
        panel.add(zoomX);
        panel.add(down);
        return panel;
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
                    updateCenterLabel(grille);
                }
                case "Medium" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_MEDIUM, false);
                    updateCenterLabel(grille);
                }
                case "Bold" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_BOLD, false);
                    updateCenterLabel(grille);
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
                    updateCenterLabel(edge);
                }
                case "Prewitt" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(image, EdgeController.PREWITT);
                    } catch (Exception e) {
                    }
                    updateCenterLabel(edge);
                }
                case "Mar" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(image, EdgeController.MARR);
                    } catch (Exception e) {
                    }
                    updateCenterLabel(edge);
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
                    updateCenterLabel(reStyle);
                }
                case "Oil" -> {
                    IMAGE reStyle = new IMAGE();
                    try {
                        reStyle = styleCtrl.transOilPaintingStyle(image);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    updateCenterLabel(reStyle);
                }
            }
        });
        panel.add(comboBox);
        return panel;
    }
}
