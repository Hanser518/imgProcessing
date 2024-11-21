package frame;

import algorithm.wpfo.main.WPFO;
import controller.*;
import entity.IMAGE;
import frame.entity.ImageNode;
import frame.entity.Param;
import frame.service.IFileService;
import frame.service.InitializeService;
import frame.service.impl.IFileServiceImpl;
import frame.service.impl.InitializeServiceImpl;
import netscape.javascript.JSObject;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static frame.entity.Param.*;

public class FrameBase {
    private boolean fileBarOpen = true;
    private boolean operateBarOpen = false;
    private boolean layerBarOpen = false;

    private static JFrame baseFrame;
    private static final JLabel fileNameLabel = new JLabel();
    private static final JLabel layerLabel = new JLabel();
    private static JLabel centerLabel;
    private static final JPanel layerPanel = new JPanel();
    private static final JPanel fileChoosePanel = new JPanel();
    private static JScrollPane sideBar = new JScrollPane();
    private static JScrollPane sidePanel = new JScrollPane();
    private static final JPanel thumbPanel = new JPanel();


    private static final IFileService fileService = new IFileServiceImpl();
    private static final InitializeService initServ = new InitializeServiceImpl();

    private static final ProcessingController pcsCtrl = new ProcessingController();
    private static final StylizeController styleCtrl = new StylizeController();
    private static final AdjustController adCtrl = new AdjustController();
    private static final EdgeController edgeCtrl = new EdgeController();
    private static final BlurController blurCtrl = new BlurController();
    private static final ImgController imgCtrl2 = new ImgController();



    private boolean init = false;
    private int sx, sy;

    public static void main(String[] args) {
        System.out.println("Hello image");
        SwingUtilities.invokeLater(FrameBase::new);

    }

    public FrameBase() {
        // 全局字体抗锯齿，在初始化 JFrame 之前调用
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        testFunction(blurCtrl.getClass(), "getQuickGasBlur", null);

        baseFrame = initServ.initializeMainFrame();
        centerLabel = initServ.initializeCenterLabel();

        JScrollPane scrollPane = new JScrollPane(centerLabel);
        processLabel.setFont(funcFont);
        processLabel.setForeground(Color.WHITE);
        updateSideBar(fileChoosePanel);
        fileChoosePanel.setBackground(new Color(255, 255, 255));
        initLayerPanel();
        initServ.initializeFileList();

        baseFrame.add(sideBar, BorderLayout.WEST);
        baseFrame.add(scrollPane, BorderLayout.CENTER);
        baseFrame.add(headPanel(), BorderLayout.NORTH);
        baseFrame.add(bottomPanel(), BorderLayout.SOUTH);
        baseFrame.setVisible(true);
    }

    public static IMAGE updateNode(IMAGE newImage, int operation) {
        ImageNode nodeNow = imageLayer.getNode(imageLayer.getIndex());
        switch (operation) {
            case ADD_NODE -> {
                ImageNode next = new ImageNode(newImage);
                nodeNow.next = next;
                next.prev = nodeNow;
                nodeNow = next;
                nodeNow.nodeName = new File(pathNow).getName();
            }
            case CANCEL_NODE -> {
                if (nodeNow.prev != null) {
                    nodeNow = nodeNow.prev;
                }
            }
            case RETRY_NODE -> {
                if (nodeNow.next != null) {
                    nodeNow = nodeNow.next;
                }
            }
            case CLEAR_NODE -> {
                nodeNow.next = null;
                nodeNow.prev = null;
                nodeNow.image = new IMAGE();
            }
        }
        fileNameLabel.setText(nodeNow.nodeName);
        System.out.println("Operation Layer:" + imageLayer.getIndex());
        imageLayer.updateLayer(nodeNow, imageLayer.getIndex());
        return nodeNow.image;
    }

    public static IMAGE updateNode(){
        ImageNode nodeNow = imageLayer.getNode(imageLayer.getIndex());
        fileNameLabel.setText(nodeNow.nodeName);
        System.out.println("Operation Layer:" + imageLayer.getIndex());
        return nodeNow.image;
    }

    public static void updateCenterLabel(boolean finish) {
        centerLabel.setIcon(null);
        // 获取图像宽高，计算比例
        centerLabel.setText(finish ? "ok" : "wait");
        centerLabel.setFont(titalFont);
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public static void updateCenterLabel(IMAGE newImage) {
        updateLayerThumb(imageLayer.getIndex());
        centerLabel.setText(null);
        // 获取图像宽高，计算比例
        int imgWidth = newImage.getWidth();
        int imgHeight = newImage.getHeight();
        double imgRate = Math.min((double) frameWidth / imgWidth, (double) frameHeight / imgHeight) * zoom;
        image = newImage;
        centerLabel.setIcon(new ImageIcon(pcsCtrl.resizeImage(newImage, imgRate, pcsCtrl.RESIZE_ENTIRETY).getImg()));
        centerLabel.setBackground(Color.darkGray);
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public static void updateCenterLabel(String path) {
        updateLayerThumb(imageLayer.getIndex());
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

    public static void updateSidePanel() {
        fileChoosePanel.removeAll();
        fileChoosePanel.setLayout(new BoxLayout(fileChoosePanel, BoxLayout.Y_AXIS));
        JButton backBtn = buildBackButton();
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

    public static void updateSideBar2(JPanel component) {
        if(sidePanel != null) {
            baseFrame.remove(sidePanel);
        }
        sidePanel = new JScrollPane(component);
        baseFrame.add(sidePanel, BorderLayout.EAST);
        baseFrame.revalidate(); // 重新验证布局
        baseFrame.repaint(); // 重新绘制组件
    }

    public static void fileChooser(File file) {
        String suffix = fileService.getFileType(file);
        pathNow = file.getAbsolutePath();
        switch (suffix) {
            case "JPG", "PNG" -> {
                try {
                    updateCenterLabel(updateNode(new IMAGE(file.getAbsolutePath(), 0), ADD_NODE));
                } catch (IOException ignored) {
                }
            }
            default -> {
                updateCenterLabel(file.getAbsolutePath());
            }
        }
    }

    public static void initLayerPanel() {
        layerPanel.setBackground(new Color(235, 235, 235));
        layerPanel.setLayout(new BorderLayout());
        int[] layerCount = imageLayer.getRange();
        layerLabel.setText(String.format("%d/%d", imageLayer.getIndex() + 1, layerCount[1]));
        layerLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton layerUp = Param.functionButton2("+");
        JButton layerDown = Param.functionButton2("-");
        layerUp.addActionListener(ac -> {
            if(imageLayer.getIndex() >= imageLayer.getRange()[1] - 1){
                imageLayer.addLayer(new IMAGE());
            }
            imageLayer.setIndex(imageLayer.getIndex() != null ? imageLayer.getIndex() + 1 : 0);
            layerLabel.setText(String.format("%d/%d", imageLayer.getIndex() + 1, imageLayer.getRange()[1]));
            updateCenterLabel(updateNode());
        });
        layerDown.addActionListener(ac -> {
            imageLayer.setIndex(imageLayer.getIndex() > 0 ? imageLayer.getIndex() - 1 : 0);
            layerLabel.setText(String.format("%d/%d", imageLayer.getIndex() + 1, imageLayer.getRange()[1]));
            updateCenterLabel(updateNode());
        });

        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new GridLayout(2, 1));
        paramPanel.add(layerLabel);

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(1, 2));
        optionPanel.add(layerUp);
        optionPanel.add(layerDown);

        paramPanel.add(optionPanel);

        thumbPanel.setLayout(new BoxLayout(thumbPanel, BoxLayout.PAGE_AXIS));
        layerPanel.add(paramPanel, BorderLayout.NORTH);
        layerPanel.add(thumbPanel, BorderLayout.CENTER);
    }

    public static void updateLayerThumb(Integer index){
        int[] range = imageLayer.getRange();
        if(range[1] > thumbList.size()){
            System.out.println("THUMB IN");
            for(int i = thumbList.size();i < range[1];i ++){
                ImageNode node = imageLayer.getNode(i);
                IMAGE nodeImg = node.image;
                double imgRate = Math.min((double) 100 / nodeImg.getWidth(), (double) 100 / nodeImg.getHeight());
                nodeImg = pcsCtrl.resizeImage(nodeImg, imgRate, pcsCtrl.RESIZE_ENTIRETY);
                JLabel thumbLabel = new JLabel();
                thumbLabel.setIcon(new ImageIcon(nodeImg.getImg()));
                thumbLabel.setBorder(new TitledBorder(new EtchedBorder(), node.nodeName));
                int finalI = i;
                thumbLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        imageLayer.setIndex(finalI);
                        layerLabel.setText(String.format("%d/%d", imageLayer.getIndex() + 1, imageLayer.getRange()[1]));
                        updateCenterLabel(updateNode());
                        System.out.println("CLICK:" + finalI);
                    }
                });
                thumbList.add(thumbLabel);
                thumbPanel.add(thumbLabel);
            }
        }else{
            ImageNode node = imageLayer.getNode(index);
            IMAGE nodeImg = node.image;
            double imgRate = Math.min((double) 100 / nodeImg.getWidth(), (double) 100 / nodeImg.getHeight());
            nodeImg = pcsCtrl.resizeImage(nodeImg, imgRate, pcsCtrl.RESIZE_ENTIRETY);
            JLabel thumbLabel = thumbList.get(index);
            thumbLabel.setIcon(new ImageIcon(nodeImg.getImg()));
            thumbLabel.setBorder(new TitledBorder(new EtchedBorder(), node.nodeName));
            thumbList.set(index, thumbLabel);
        }

    }

    public JPanel headPanel() {
        JPanel headPanel = new JPanel();
        headPanel.setBackground(new Color(177, 123, 89));
        headPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton fileBtn = new JButton("File");
        fileBtn.addActionListener(e -> {
            fileBarOpen = !fileBarOpen;
            operateBarOpen = false;
            layerBarOpen = false;
            updateSideBar(fileBarOpen ? fileChoosePanel : null);
        });
        headPanel.add(fileBtn);

        JButton operation = new JButton("Operation");
        operation.addActionListener(e -> {
            operateBarOpen = !operateBarOpen;
            fileBarOpen = false;
            layerBarOpen = false;
            updateSideBar(operateBarOpen ? OperationBar() : null);
        });
        headPanel.add(operation);

        JButton layer = new JButton("Layer");
        layer.addActionListener(e -> {
            layerBarOpen = !layerBarOpen;
            updateSideBar2(layerBarOpen ? layerPanel : null);
        });
        headPanel.add(layer);

        fileNameLabel.setFont(titalFont);
        fileNameLabel.setForeground(Color.white);

        headPanel.add(processLabel);
        headPanel.add(fileNameLabel);

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
        base.setLayout(new GridLayout(2, 2));

        JButton reLoadBtn = functionButton("ReLoad");
        reLoadBtn.addActionListener(e -> {
            System.out.println(pathNow);
            fileChooser(new File(pathNow));
        });
        base.add(reLoadBtn);

        JButton closeButton = functionButton("Close");
        closeButton.addActionListener(e -> updateCenterLabel(updateNode(new IMAGE(), CLEAR_NODE)));
        base.add(closeButton);

        JButton cancelBtn = functionButton("Cancel");
        cancelBtn.addActionListener(ac -> {
            updateCenterLabel(updateNode(null, CANCEL_NODE));
        });
        base.add(cancelBtn);

        JButton retryBtn = functionButton("Retry");
        retryBtn.addActionListener(ac -> {
            updateCenterLabel(updateNode(null, RETRY_NODE));
        });
        base.add(retryBtn);


        operationPanel.add(base);
        operationPanel.add(blurPanel());
        operationPanel.add(edgePanel());
        operationPanel.add(grilleBox());
        operationPanel.add(grillePanel());
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
        JLabel gasCount = new JLabel("BlurRadio: " + blurRadio);
        gasCount.setFont(countFont);
        JPanel gasBlurPanel = new JPanel();
        gasBlurPanel.setLayout(new GridLayout(2, 1));
        gasBlurPanel.setBorder(new TitledBorder(new EtchedBorder(), "GasBlur"));
        JSlider gasSlider = new JSlider(1, MAX_BLUR, blurRadio);
        JButton model = functionButton("Quick");


        gasSlider.addChangeListener(change -> {
            blurRadio = gasSlider.getValue();
            gasCount.setText("BlurRadio: " + blurRadio);
            model.setText((BLUR_QUICK ? "Quick：" : "Normal：") + blurRadio);
        });
        JButton gasBtn = buildApplyButton();
        gasBtn.addActionListener(ac -> {
            IMAGE gas;
            if (BLUR_QUICK) {
                gas = blurCtrl.getQuickGasBlur(image, blurRadio, 32);
            } else {
                gas = blurCtrl.getGasBlur(image, blurRadio, 32);
            }
            updateCenterLabel(updateNode(gas, ADD_NODE));
        });
        gasSlider.setMajorTickSpacing(MAX_BLUR / 6 - 1);
        gasSlider.setMinorTickSpacing(MAX_BLUR / 36 - 1);
        gasSlider.setPaintLabels(true);
        gasSlider.setPaintTicks(true);
        gasBlurPanel.add(gasSlider);

        JPanel gasPanel = new JPanel();
        gasPanel.setLayout(new GridLayout(1, 2));
        // gasPanel.add(gasCount);

        JPanel modelPanel = new JPanel();
        modelPanel.setBorder(new TitledBorder(new EtchedBorder(), "GasModel"));
        modelPanel.setLayout(new GridLayout(1, 2));
        model.addActionListener(ac -> {
            BLUR_QUICK = !BLUR_QUICK;
            MAX_BLUR = BLUR_QUICK ? 200 : 100;
            gasSlider.setMaximum(MAX_BLUR);
            gasSlider.setMajorTickSpacing(MAX_BLUR / 6 - 1);
            gasSlider.setMinorTickSpacing(MAX_BLUR / 36 - 1);
            model.setText((BLUR_QUICK ? "Quick：" : "Normal：") + blurRadio);
        });
        // gasPanel.add(new JLabel());
        gasPanel.add(model);
        gasPanel.add(gasBtn);
        gasBlurPanel.add(gasPanel);

        return gasBlurPanel;
    }

    public JPanel strangePanel() {
        JLabel strangeCount = new JLabel("BlurRadio: " + strangeBlurRadio);
        strangeCount.setFont(countFont);
        JPanel strangeBlurPanel = new JPanel();
        strangeBlurPanel.setLayout(new GridLayout(2, 1));
        strangeBlurPanel.setBorder(new TitledBorder(new EtchedBorder(), "StrangeBlur"));
        JSlider strangeSlider = new JSlider(1, 100, strangeBlurRadio);
        strangeSlider.addChangeListener(change -> {
            strangeBlurRadio = strangeSlider.getValue();
            strangeCount.setText("BlurRadio: " + strangeBlurRadio);
        });
        JButton stgBtn = buildApplyButton();
        stgBtn.addActionListener(ac -> {
            IMAGE strange = blurCtrl.getStrangeBlur(image, strangeBlurRadio);
            updateCenterLabel(updateNode(strange, ADD_NODE));
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

        southPanel.add(processLabel);
        southPanel.add(saveButton());
        southPanel.add(zoomPanel());

        return southPanel;
    }

    private static JButton saveButton() {
        JButton save = new JButton("SAVE");
        save.addActionListener(ac -> {
            LocalDateTime date = LocalDateTime.now();
            String time = date.format(DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss")) + "_" + (int) (Math.random() * 1000);
            File file = new File(pathNow);
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf("."));
            try {
                imgCtrl2.saveByName2(image, "Visible", name + time);
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

        zoomX.setText(String.format("%2.2f", zoom));
        zoomX.setForeground(Color.white);
        JSlider slider = new JSlider(50, 500, (int) (zoom * 100));
        slider.addChangeListener(change -> {
            int value = slider.getValue();
            zoom = value / 100.0;
            zoomX.setText(String.format("%2.2f", zoom));
        });
        slider.setOpaque(false);
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                updateCenterLabel(image);
            }
        });
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        plus.addActionListener(ac -> {
            zoom += 0.1;
            zoomX.setText(String.format("%2.2f", zoom));
            slider.setValue((int) (zoom * 100));
            updateCenterLabel(image);
        });
        down.addActionListener(ac -> {
            zoom -= 0.1;
            zoomX.setText(String.format("%2.2f", zoom));
            slider.setValue((int) (zoom * 100));
            updateCenterLabel(image);
        });

        panel.add(slider);
        panel.add(plus);
        panel.add(zoomX);
        panel.add(down);
        return panel;
    }

    public JPanel grilleBox() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Grille"));
        panel.setLayout(new GridLayout(2, 2, 1, 1));

        JLabel textLabel = new JLabel("");
        textLabel.setFont(applyBtnFont);
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        JButton btn1 = functionButton("Regular");
        btn1.addActionListener(ac -> {
            textLabel.setText("Regular");
            IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_REGULAR, false);
            updateCenterLabel(updateNode(grille, ADD_NODE));
        });

        JButton btn2 = functionButton("Medium");
        btn2.addActionListener(ac -> {
            textLabel.setText("Medium");
            IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_MEDIUM, false);
            updateCenterLabel(updateNode(grille, ADD_NODE));
        });

        JButton btn3 = functionButton("Bold");
        btn3.addActionListener(ac -> {
            textLabel.setText("Bold");
            IMAGE grille = styleCtrl.transGrilleStyle(image, styleCtrl.GRILLE_BOLD, false);
            updateCenterLabel(updateNode(grille, ADD_NODE));
        });
        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);
        panel.add(textLabel);
        return panel;
    }

    public JPanel grillePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Grille"));
        panel.setLayout(new GridLayout(2, 1, 1, 1));

        JLabel textField = new JLabel(String.format("%.3f", grilleParam / 1000.0));
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setFont(countFont);
        JSlider slider = new JSlider(0, 1000, grilleParam);
        slider.addChangeListener(c -> {
            grilleParam = slider.getValue();
            textField.setText(String.format("%.3f", grilleParam / 1000.0));
        });
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        panel.add(slider);

        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new GridLayout(1, 3));
        JButton type = functionButton("Vertical");
        type.addActionListener(ac -> {
            if (grilleType < 2) grilleType++;
            else grilleType = 0;
            switch (grilleType) {
                case 0 -> type.setText("Horizon");
                case 1 -> type.setText("Vertical");
                case 2 -> type.setText("Multiple");
                default -> type.setText("Wrong");
            }
        });

        JButton apply = buildApplyButton();
        apply.addActionListener(ac -> {
            IMAGE grille = styleCtrl.buildGrille(image, grilleParam / 1000.0, grilleType);
            updateCenterLabel(updateNode(grille, ADD_NODE));
        });

        paramPanel.add(type);
        paramPanel.add(textField);
        paramPanel.add(apply);
        panel.add(paramPanel);
        return panel;
    }

    public JPanel edgePanel() {
        JPanel panel = new JPanel();
        // panel.setBackground(new Color(177, 123, 89));
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Edge"));
        panel.setLayout(new GridLayout(2, 2, 1, 1));

        JLabel textLabel = new JLabel("");
        textLabel.setFont(applyBtnFont);
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        JButton sobel = functionButton("SOBEL");
        sobel.addActionListener(ac -> {
            IMAGE edge = new IMAGE();
            try {
                textLabel.setText("SOBEL");
                edge = edgeCtrl.getImgEdge(image, EdgeController.SOBEL);
            } catch (Exception e) {
            }
            updateCenterLabel(updateNode(edge, ADD_NODE));
        });
        sobel.setToolTipText("泛用性强，对噪声的抗性强");

        JButton prewitt = functionButton("PREWITT");
        prewitt.addActionListener(ac -> {
            IMAGE edge = new IMAGE();
            try {
                textLabel.setText("PREWITT");
                edge = edgeCtrl.getImgEdge(image, EdgeController.PREWITT);
            } catch (Exception e) {
            }
            updateCenterLabel(updateNode(edge, ADD_NODE));
        });
        prewitt.setToolTipText("边缘清晰，对噪声的抗性一般");

        JButton mar = functionButton("MAR");
        mar.addActionListener(ac -> {
            IMAGE edge = new IMAGE();
            try {
                textLabel.setText("MAR");
                edge = edgeCtrl.getImgEdge(image, EdgeController.MARR);
            } catch (Exception e) {
            }
            updateCenterLabel(updateNode(edge, ADD_NODE));
        });
        mar.setToolTipText("仅适用于提取明暗对比明显的场景");
        panel.add(sobel);
        panel.add(prewitt);
        panel.add(mar);
        panel.add(textLabel);
        return panel;
    }

    public JPanel styleBox() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(177, 123, 89));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel messageLabel = new JLabel("Stylize:");
        panel.add(messageLabel);

        String[] constellations = {
                "Paper", "Oil", "Erosion"
        };
        JComboBox comboBox = new JComboBox(constellations);
        comboBox.addActionListener(action -> {
            String select = (String) comboBox.getSelectedItem();
            switch (select) {
                case "Paper" -> {
                    IMAGE reStyle = styleCtrl.transPaperStyle(image, 24, 118);
                    updateCenterLabel(updateNode(reStyle, ADD_NODE));
                }
                case "Oil" -> {
                    try {
                        IMAGE reStyle = styleCtrl.transOilPaintingStyle(image);
                        updateCenterLabel(updateNode(reStyle, ADD_NODE));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "Erosion" -> {
                    IMAGE reStyle = pcsCtrl.erosionImage(image);
                    updateCenterLabel(updateNode(reStyle, ADD_NODE));
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
            IMAGE cdr = adCtrl.CDR(image);
            updateCenterLabel(updateNode(cdr, ADD_NODE));
        });

        JButton apply = buildApplyButton();
        apply.addActionListener(ac -> {
            IMAGE asv = adCtrl.adjustSatAndVal(image, saturation, value);
            updateCenterLabel(updateNode(asv, ADD_NODE));
        });

        btnPanel.add(cdrBtn);
        btnPanel.add(apply);
        panel.add(btnPanel);

        JLabel saturationLabel = new JLabel();
        saturationLabel.setText(String.format("%3d", saturation));
        saturationLabel.setForeground(fontColor);
        JSlider saturationSlider = new JSlider(-99, 99, saturation);
        saturationSlider.setBackground(subBackColor);
        saturationSlider.setMajorTickSpacing(20);
        saturationSlider.setMinorTickSpacing(5);
        saturationSlider.setPaintLabels(true);
        saturationSlider.setPaintTicks(true);
        saturationSlider.addChangeListener(c -> {
            saturation = saturationSlider.getValue();
            saturationLabel.setText(String.format("%3d", saturation));
        });
        JPanel sPanel = new JPanel();
        sPanel.setBackground(subBackColor);
        sPanel.setBorder(new TitledBorder(new EtchedBorder(), "Saturation"));
        sPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sPanel.add(saturationSlider);
        sPanel.add(saturationLabel);
        panel.add(sPanel);

        JLabel valueLabel = new JLabel();
        valueLabel.setText(String.format("%3d", value));
        valueLabel.setForeground(fontColor);
        JSlider valueSlider = new JSlider(-99, 99, value);
        valueSlider.setBackground(subBackColor);
        valueSlider.setMajorTickSpacing(20);
        valueSlider.setMinorTickSpacing(5);
        valueSlider.setPaintLabels(true);
        valueSlider.setPaintTicks(true);
        valueSlider.addChangeListener(c -> {
            value = valueSlider.getValue();
            valueLabel.setText(String.format("%3d", value));
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

    public void testFunction(Class<?> classOfService,
                             String methodName,
                             Map<String, Object> params) {
        try {
            Method[] methodList = classOfService.getDeclaredMethods();
            Method method = null;
            for (Method m : methodList) {
                if (m.getName().equals(methodName)) {
                    method = m;
                    System.out.println("Catch method:" + methodName);
                }
            }
            if (method == null) {
                System.out.println("No such method:" + methodName);
                return;
            }
            Parameter[] paramList = method.getParameters();
            for (Parameter p : paramList) {
                System.out.println(p.isNamePresent() + "|" + p.getParameterizedType());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
