package frame;

import controller.*;
import entity.IMAGE;
import frame.entity.ImageNode;
import frame.entity.Param;
import frame.service.IFileService;
import frame.service.InitializeService;
import frame.service.impl.IFileServiceImpl;
import frame.service.impl.InitializeServiceImpl;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static frame.entity.Param.*;

public class FrameBase {

    private static JFrame baseFrame;
    private static JLabel fileNameLabel = new JLabel();
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
        // 全局字体抗锯齿，在初始化 JFrame 之前调用
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        baseFrame = initServ.initializeMainFrame();
        centerLabel = initServ.initializeCenterLabel();
        previewLabel = new JLabel();

        fileChoosePanel.setBackground(new Color(255, 255, 255));
        initServ.initializeFileList();

        baseFrame.add(sideBar, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(centerLabel);
        baseFrame.add(scrollPane, BorderLayout.CENTER);
        baseFrame.add(headPanel(), BorderLayout.NORTH);
        baseFrame.add(bottomPanel(), BorderLayout.SOUTH);
        baseFrame.setVisible(true);
    }

    public static IMAGE updateNode(IMAGE newImage, int operation) {
        switch (operation) {
            case Param.ADD_NODE -> {
                ImageNode next = new ImageNode(newImage);
                Param.node.next = next;
                next.prev = Param.node;
                Param.node = next;
                Param.node.nodeName = new File(Param.pathNow).getName();
            }
            case Param.CANCEL_NODE -> {
                if (Param.node.prev != null) {
                    Param.node = Param.node.prev;
                }
            }
            case Param.RETRY_NODE -> {
                if (Param.node.next != null) {
                    Param.node = Param.node.next;
                }
            }
        }
        fileNameLabel.setText(Param.node.nodeName);
        return Param.node.image;
    }

    public static void updateCenterLabel(boolean finish) {
        centerLabel.setIcon(null);
        // 获取图像宽高，计算比例
        centerLabel.setText(finish ? "ok" : "wait");
        centerLabel.setFont(Param.titalFont);
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
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

    public static void updateFileList() {
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
        JButton backBtn = buildBackButton();
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
            JButton fileBtn = fileButton(file);
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

    public static void fileChooser(File file) {
        String suffix = fileService.getFileType(file);
        Param.pathNow = file.getAbsolutePath();
        switch (suffix) {
            case "JPG", "PNG" -> {
                try {
                    updateCenterLabel(updateNode(new IMAGE(file.getAbsolutePath(), 0), Param.ADD_NODE));
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
        closeButton.addActionListener(e -> updateCenterLabel(updateNode(new IMAGE(), Param.ADD_NODE)));
        base.add(closeButton);


        operationPanel.add(base);
        operationPanel.add(blurPanel());
        operationPanel.add(edgeBox());
        operationPanel.add(grilleBox());
        operationPanel.add(styleBox());
        operationPanel.add(adjustPanel());

        return operationPanel;
    }

    public JPanel blurPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(248, 118, 80, 124));
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Blur"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(gasPanel());
        panel.add(strangePanel());
        return panel;
    }

    public JPanel gasPanel() {
        JLabel gasCount = new JLabel("BlurSize: " + Param.blurSize);
        gasCount.setFont(Param.countFont);
        JPanel gasBlurPanel = new JPanel();
        gasBlurPanel.setLayout(new GridLayout(2, 1));
        gasBlurPanel.setBorder(new TitledBorder(new EtchedBorder(), "GasBlur"));
        JSlider gasSlider = new JSlider(1, 100, Param.blurSize);
        gasSlider.addChangeListener(change -> {
            Param.blurSize = gasSlider.getValue();
            gasCount.setText("BlurSize: " + Param.blurSize);
        });
        JButton gasBtn = Param.buildApplyButton();
        gasBtn.addActionListener(ac -> {
            IMAGE gas = blurCtrl.getQuickGasBlur(Param.image, Param.blurSize, 32);
            updateCenterLabel(updateNode(gas, Param.ADD_NODE));
        });
        gasSlider.setMajorTickSpacing(12);
        gasSlider.setMinorTickSpacing(6);
        gasSlider.setPaintLabels(true);
        gasSlider.setPaintTicks(true);
        gasBlurPanel.add(gasSlider);

        JPanel gasPanel = new JPanel();
        gasPanel.setLayout(new GridLayout(1, 2));
        gasPanel.add(gasCount);
        gasPanel.add(gasBtn);
        gasBlurPanel.add(gasPanel);

        return gasBlurPanel;
    }

    public JPanel strangePanel() {
        JLabel strangeCount = new JLabel("BlurSize: " + Param.strangeBlurSize);
        strangeCount.setFont(Param.countFont);
        JPanel strangeBlurPanel = new JPanel();
        strangeBlurPanel.setLayout(new GridLayout(2, 1));
        strangeBlurPanel.setBorder(new TitledBorder(new EtchedBorder(), "StrangeBlur"));
        JSlider strangeSlider = new JSlider(1, 100, Param.strangeBlurSize);
        strangeSlider.addChangeListener(change -> {
            Param.strangeBlurSize = strangeSlider.getValue();
            strangeCount.setText("BlurSize: " + Param.strangeBlurSize);
        });
        JButton stgBtn = Param.buildApplyButton();
        stgBtn.addActionListener(ac -> {
            IMAGE strange = blurCtrl.getStrangeBlur(Param.image, Param.strangeBlurSize);
            updateCenterLabel(updateNode(strange, Param.ADD_NODE));
        });
        strangeSlider.setMajorTickSpacing(12);
        strangeSlider.setMinorTickSpacing(6);
        strangeSlider.setPaintLabels(true);
        strangeSlider.setPaintTicks(true);
        strangeBlurPanel.add(strangeSlider);

        JPanel stgPanel = new JPanel();
        stgPanel.setLayout(new GridLayout(1, 2));
        stgPanel.add(strangeCount);
        stgPanel.add(stgBtn);
        strangeBlurPanel.add(stgPanel);

        return strangeBlurPanel;
    }

    public JPanel bottomPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setBackground(bkC1);
        southPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        fileNameLabel.setFont(titalFont);
        fileNameLabel.setForeground(Color.white);

        southPanel.add(Param.processLabel);
        southPanel.add(fileNameLabel);
        southPanel.add(saveButton());
        southPanel.add(zoomPanel());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(ac -> {
            updateCenterLabel(updateNode(null, Param.CANCEL_NODE));
        });
        southPanel.add(cancelBtn);

        JButton retryBtn = new JButton("Retry");
        retryBtn.addActionListener(ac -> {
            updateCenterLabel(updateNode(null, Param.RETRY_NODE));
        });
        southPanel.add(retryBtn);

        return southPanel;
    }

    private static JButton saveButton() {
        JButton save = new JButton("SAVE");
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
        return save;
    }

    public JPanel zoomPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton plus = new JButton("+");
        JButton down = new JButton("-");
        JLabel zoomX = new JLabel();

        zoomX.setText(String.format("%2.2f", Param.zoom));
        zoomX.setForeground(Color.white);
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
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        plus.addActionListener(ac -> {
            Param.zoom += 0.1;
            zoomX.setText(String.format("%2.2f", Param.zoom));
            slider.setValue((int) (zoom * 100));
            updateCenterLabel(Param.image);
        });
        down.addActionListener(ac -> {
            Param.zoom -= 0.1;
            zoomX.setText(String.format("%2.2f", Param.zoom));
            slider.setValue((int) (zoom * 100));
            updateCenterLabel(Param.image);
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
                    updateCenterLabel(updateNode(grille, Param.ADD_NODE));
                }
                case "Medium" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(Param.image, styleCtrl.GRILLE_MEDIUM, false);
                    updateCenterLabel(updateNode(grille, Param.ADD_NODE));
                }
                case "Bold" -> {
                    IMAGE grille = styleCtrl.transGrilleStyle(Param.image, styleCtrl.GRILLE_BOLD, false);
                    updateCenterLabel(updateNode(grille, Param.ADD_NODE));
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
                    updateCenterLabel(updateNode(edge, Param.ADD_NODE));
                }
                case "Prewitt" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(Param.image, EdgeController.PREWITT);
                    } catch (Exception e) {
                    }
                    updateCenterLabel(updateNode(edge, Param.ADD_NODE));
                }
                case "Mar" -> {
                    IMAGE edge = new IMAGE();
                    try {
                        edge = edgeCtrl.getImgEdge(Param.image, EdgeController.MARR);
                    } catch (Exception e) {
                    }
                    updateCenterLabel(updateNode(edge, Param.ADD_NODE));
                }
            }
        });
        panel.add(comboBox);
        return panel;
    }

    public JPanel styleBox() {
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
                    updateCenterLabel(updateNode(reStyle, Param.ADD_NODE));
                }
                case "Oil" -> {
                    IMAGE reStyle = new IMAGE();
                    try {
                        reStyle = styleCtrl.transOilPaintingStyle(Param.image);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    updateCenterLabel(updateNode(reStyle, Param.ADD_NODE));
                }
            }
        });
        panel.add(comboBox);
        return panel;
    }

    public JPanel adjustPanel() {
        Color subBackColor = new Color(197, 203, 222);
        Color fontColor = new Color(199, 91, 28);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(153, 156, 164));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(new EtchedBorder(), "饱和度/亮度"));

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(subBackColor);
        JButton cdrBtn = new JButton("CDR");
        cdrBtn.addActionListener(e -> {
            IMAGE cdr = adCtrl.CDR(Param.image);
            updateCenterLabel(updateNode(cdr, Param.ADD_NODE));
        });

        JButton apply = Param.buildApplyButton();
        apply.addActionListener(ac -> {
            IMAGE asv = adCtrl.adjustSatAndVal(Param.image, Param.saturation, Param.value);
            updateCenterLabel(updateNode(asv, Param.ADD_NODE));
        });

        btnPanel.add(cdrBtn);
        btnPanel.add(apply);
        panel.add(btnPanel);

        JLabel saturationLabel = new JLabel();
        saturationLabel.setText(String.format("%3d", Param.saturation));
        saturationLabel.setForeground(fontColor);
        JSlider saturationSlider = new JSlider(-99, 99, Param.saturation);
        saturationSlider.setBackground(subBackColor);
        saturationSlider.setMajorTickSpacing(20);
        saturationSlider.setMinorTickSpacing(5);
        saturationSlider.setPaintLabels(true);
        saturationSlider.setPaintTicks(true);
        saturationSlider.addChangeListener(c -> {
            Param.saturation = saturationSlider.getValue();
            saturationLabel.setText(String.format("%3d", Param.saturation));
        });
        JPanel sPanel = new JPanel();
        sPanel.setBackground(subBackColor);
        sPanel.setBorder(new TitledBorder(new EtchedBorder(), "Saturation"));
        sPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sPanel.add(saturationSlider);
        sPanel.add(saturationLabel);
        panel.add(sPanel);

        JLabel valueLabel = new JLabel();
        valueLabel.setText(String.format("%3d", Param.value));
        valueLabel.setForeground(fontColor);
        JSlider valueSlider = new JSlider(-99, 99, Param.value);
        valueSlider.setBackground(subBackColor);
        valueSlider.setMajorTickSpacing(20);
        valueSlider.setMinorTickSpacing(5);
        valueSlider.setPaintLabels(true);
        valueSlider.setPaintTicks(true);
        valueSlider.addChangeListener(c -> {
            Param.value = valueSlider.getValue();
            valueLabel.setText(String.format("%3d", Param.value));
        });
        JPanel vPanel = new JPanel();
        vPanel.setBackground(subBackColor);
        vPanel.setBorder(new TitledBorder(new EtchedBorder(), "Value"));
        vPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        vPanel.add(valueSlider);
        vPanel.add(valueLabel);
        panel.add(vPanel);

        return panel;
    }


}
