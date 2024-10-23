package frame;

import controller.*;
import entity.IMAGE;
import frame.entity.Param;
import frame.service.IFileService;
import frame.service.InitializeService;
import frame.service.impl.IFileServiceImpl;
import frame.service.impl.InitializeServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FrameBase {

    private static JFrame baseFrame;
    private static JLabel centerLabel;
    private static JLabel previewLabel;
    private static JPanel fileChoosePanel = new JPanel();
    private static JScrollPane sideBar = new JScrollPane();

    private static final IFileService fileService = new IFileServiceImpl();
    private static final InitializeService initServ = new InitializeServiceImpl();

    private static final ProcessingController pcsCtrl = new ProcessingController();
    private static final StylizeController styleCtrl = new StylizeController();
    private static final AdjustController adCtrl = new AdjustController();
    private static final EdgeController edgeCtrl = new EdgeController();
    private static final BlurController blurCtrl = new BlurController();
    private static final ImgController imgCtrl2 = new ImgController();


    public static void main(String[] args) {
        System.out.println("Hello image");
        SwingUtilities.invokeLater(FrameBase::new);

    }

    public FrameBase() {
        baseFrame = initServ.initializeMainFrame();
        centerLabel = initServ.initializeCenterLabel();
        previewLabel = new JLabel();

        fileChoosePanel.setBackground(new Color(220, 161, 128));
        initServ.initializeFileList();

        baseFrame.add(sideBar, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(centerLabel);
        baseFrame.add(scrollPane, BorderLayout.CENTER);
        baseFrame.add(headPanel(), BorderLayout.NORTH);
        baseFrame.add(bottomPanel(), BorderLayout.SOUTH);
        baseFrame.setVisible(true);
    }

    public static void updateCenterLabel(IMAGE newImage) {
        centerLabel.setText(null);
        // 获取图像宽高，计算比例
        int imgWidth = newImage.getWidth();
        int imgHeight = newImage.getHeight();
        double imgRate = Math.min((double) Param.frameWidth / imgWidth, (double) Param.frameHeight / imgHeight) * Param.zoom;
        Param.image = newImage;
        centerLabel.setIcon(new ImageIcon(pcsCtrl.resizeImage(newImage, imgRate, pcsCtrl.RESIZE_ENTIRETY).getImg()));
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public static void updateCenterLabel(String path) {
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

    private static void updateFileList() {
        Param.fileList.clear();
        String filePath = "";
        if (Param.path.isEmpty()) {
            filePath = Param.path_head;
        } else {
            filePath = Param.path.get(Param.path.size() - 1);
        }
        Param.fileList = fileService.getFileList(filePath);
        updateSidePanel();
    }

    public static void updateSidePanel() {
        fileChoosePanel.removeAll();
        fileChoosePanel.setLayout(new BoxLayout(fileChoosePanel, BoxLayout.Y_AXIS));
        JButton backBtn = new JButton("...");
        backBtn.addActionListener(action -> {
            if (!Param.path.isEmpty()) {
                Param.path.remove(Param.path.size() - 1);
            } else {
                Param.path_head = Param.path_head.length() <= 2 ? '.' + Param.path_head : Param.path_head;
            }
            updateFileList();
        });
        fileChoosePanel.add(backBtn);
        for (File file : Param.fileList) {
            JButton fileBtn = new JButton((file.isDirectory() ? ">>>D--" : "<<<A--") + file.getName());
            fileBtn.addActionListener(action -> {
                if (file.isDirectory()) {
                    Param.path.add(file.getAbsolutePath());
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

    public static void updateSideBar(Component component) {
        baseFrame.remove(sideBar);
        sideBar = new JScrollPane(component);
        baseFrame.add(sideBar, BorderLayout.WEST);
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    private static void fileChooser(File file) {
        String suffix = fileService.getFileType(file);
        Param.pathNow = file.getAbsolutePath();
        switch (suffix) {
            case "JPG", "PNG" -> {
                try {
                    updateCenterLabel(new IMAGE(file.getAbsolutePath(), 0));
                } catch (IOException ignored) {
                }
            }
            default -> {
                updateCenterLabel(file.getAbsolutePath());
            }
        }
    }

    public JPanel headPanel() {
        JPanel headPanel = new JPanel();
        headPanel.setBackground(new Color(177, 123, 89));
        headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton fileBtn = new JButton("File");
        fileBtn.addActionListener(e -> {
            updateSideBar(fileChoosePanel);
        });
        headPanel.add(fileBtn);

        JButton hideBtn = new JButton("CloseSideBar");
        hideBtn.addActionListener(e -> {
            updateSideBar(null);
        });
        headPanel.add(hideBtn);

        JButton operation = new JButton("Operation");
        operation.addActionListener(e -> {
            updateSideBar(OperationBar());
        });
        headPanel.add(operation);

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

    public JPanel OperationBar() {
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(new BoxLayout(operationPanel, BoxLayout.Y_AXIS));

        JPanel base = new JPanel();

        JButton reLoadBtn = new JButton("ReLoad");
        reLoadBtn.addActionListener(e -> {
            System.out.println(Param.pathNow);
            fileChooser(new File(Param.pathNow));
        });
        base.add(reLoadBtn);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> updateCenterLabel(new IMAGE()));
        base.add(closeButton);


        operationPanel.add(base);
        operationPanel.add(blurPanel());
        operationPanel.add(edgeBox());
        operationPanel.add(grilleBox());
        operationPanel.add(StyleBox());
        operationPanel.add(adJustPanel());

        return operationPanel;
    }

    public JPanel blurPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(204, 134, 111));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel title = new JLabel("Blur");
        JLabel count = new JLabel(String.valueOf(Param.blurSize));

        JSlider slider = new JSlider(1, 100, Param.blurSize);
        slider.addChangeListener(change -> {
            Param.blurSize = slider.getValue();
            count.setText(String.valueOf(Param.blurSize));
        });
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                IMAGE gas = blurCtrl.getGasBlur(Param.image, Param.blurSize, 32);
                updateCenterLabel(gas);
            }
        });
        slider.setMajorTickSpacing(12);
        slider.setMinorTickSpacing(6);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

        panel.add(title);
        panel.add(slider);
        panel.add(count);
        return panel;
    }

    public JPanel bottomPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(177, 123, 89));
        southPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        JButton save = new JButton("Save");
        save.addActionListener(ac -> {
            LocalDateTime date = LocalDateTime.now();
            String time = date.format(DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss")) + "_" + (int) (Math.random() * 1000);
            File file = new File(Param.pathNow);
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf("."));
            try {
                imgCtrl2.saveByName2(Param.image, "Visible", name + time);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        southPanel.add(save);


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

        zoomX.setText(String.format("%2.2f", Param.zoom));
        plus.addActionListener(ac -> {
            Param.zoom += 0.1;
            zoomX.setText(String.format("%2.2f", Param.zoom));
            updateCenterLabel(Param.image);
            // fileChooser(new File(pathNow));
        });
        down.addActionListener(ac -> {
            Param.zoom -= 0.1;
            zoomX.setText(String.format("%2.2f", Param.zoom));
            updateCenterLabel(Param.image);
            // fileChooser(new File(pathNow));
        });
        JSlider slider = new JSlider(50, 250, (int) (Param.zoom * 100));
        slider.addChangeListener(change -> {
            int value = slider.getValue();
            Param.zoom = value / 100.0;
            zoomX.setText(String.format("%2.2f", Param.zoom));
        });
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                updateCenterLabel(Param.image);
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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

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
                    IMAGE grille = styleCtrl.transGrilleStyle(Param.image, styleCtrl.GRILLE_REGULAR, false);
                    updateCenterLabel(grille);
                }
                case "Medium" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(Param.image, styleCtrl.GRILLE_MEDIUM, false);
                    updateCenterLabel(grille);
                }
                case "Bold" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(Param.image, styleCtrl.GRILLE_BOLD, false);
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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

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
                        edge = edgeCtrl.getImgEdge(Param.image, EdgeController.SOBEL);
                    } catch (Exception e) {
                    }
                    updateCenterLabel(edge);
                }
                case "Prewitt" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(Param.image, EdgeController.PREWITT);
                    } catch (Exception e) {
                    }
                    updateCenterLabel(edge);
                }
                case "Mar" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(Param.image, EdgeController.MARR);
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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

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
                    IMAGE reStyle = styleCtrl.transPaperStyle(Param.image, 24, 118);
                    updateCenterLabel(reStyle);
                }
                case "Oil" -> {
                    IMAGE reStyle = new IMAGE();
                    try {
                        reStyle = styleCtrl.transOilPaintingStyle(Param.image);
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

    public JPanel adJustPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(89, 127, 177));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton cdrBtn = new JButton("CDR");
        cdrBtn.addActionListener(e -> {
            IMAGE cdr = adCtrl.CDR(Param.image);
            updateCenterLabel(cdr);
        });
        panel.add(cdrBtn);

        JLabel saturationLabel = new JLabel();
        saturationLabel.setText(String.format("%3d", Param.saturation));
        saturationLabel.setForeground(new Color(224, 74, 10));
        JSlider saturationSlider = new JSlider(-100, 100, Param.saturation);
        saturationSlider.setBackground(new Color(89, 127, 177));
        saturationSlider.setMajorTickSpacing(20);
        saturationSlider.setMinorTickSpacing(5);
        saturationSlider.setPaintLabels(true);
        saturationSlider.setPaintTicks(true);
        saturationSlider.addChangeListener(c -> {
            Param.saturation = saturationSlider.getValue();
            saturationLabel.setText(String.format("%3d", Param.saturation));
        });
        JPanel sPanel = new JPanel();
        JPanel saturationPanel = new JPanel();
        saturationPanel.setLayout(new BoxLayout(saturationPanel, BoxLayout.Y_AXIS));
        saturationPanel.setBackground(new Color(141, 182, 239));
        sPanel.setBackground(new Color(89, 127, 177));
        JLabel sTitle = new JLabel("Value");
        sTitle.setForeground(Color.WHITE);
        saturationPanel.add(sTitle);
        saturationPanel.add(saturationLabel);
        sPanel.add(saturationPanel);
        sPanel.add(saturationSlider);
        panel.add(sPanel);

        JLabel valueLabel = new JLabel();
        valueLabel.setText(String.format("%3d", Param.value));
        valueLabel.setForeground(new Color(224, 74, 10));
        JSlider valueSlider = new JSlider(-100, 100, Param.value);
        valueSlider.setBackground(new Color(89, 127, 177));
        valueSlider.setMajorTickSpacing(20);
        valueSlider.setMinorTickSpacing(5);
        valueSlider.setPaintLabels(true);
        valueSlider.setPaintTicks(true);
        valueSlider.addChangeListener(c -> {
            Param.value = valueSlider.getValue();
            valueLabel.setText(String.format("%3d", Param.value));
        });
        JPanel vPanel = new JPanel();
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.setBackground(new Color(146, 187, 241));
        vPanel.setBackground(new Color(89, 127, 177));
        JLabel vTitle = new JLabel("Value");
        vTitle.setForeground(Color.WHITE);
        valuePanel.add(vTitle);
        valuePanel.add(valueLabel);
        vPanel.add(valuePanel);
        vPanel.add(valueSlider);
        panel.add(vPanel);

        JButton apply = new JButton("Apply");
        apply.addActionListener(ac -> {
            IMAGE asv = adCtrl.adjustSatAndVal(Param.image, Param.saturation, Param.value);
            updateCenterLabel(asv);
        });
        panel.add(apply);

        return panel;
    }

}
