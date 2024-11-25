package controller;

import entity.Image;
import service.IPictureService;
import service.impl.IPictureServiceImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ImgController {
    private static final ProcessingController pcsCtrl = new ProcessingController();
    private static final IPictureService picServer = new IPictureServiceImpl();
    private static final int[][] imgData = null;
    /**
     * 保存图像，格式为jpg，不支持透明图像
     * jpg格式存储占用小，但清晰度不如png格式
     * @param img     图像源
     * @param imgName 图像名称/保存目录
     * @param tips    图像标注
     * @throws IOException
     */
    public void saveByName(Image img,
                           String imgName,
                           String tips) throws IOException {
        // 确保输出目录存在
        File outputDir = new File("./output/" + imgName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        int[][] p = img.getArgbMatrix();
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result.setRGB(i, j, p[i][j]);
            }
        }
        // 写入图像
        File outputFile = new File(outputDir, imgName + "_" + tips + ".jpg");
        ImageIO.write(result, "jpg", outputFile);
    }

    /**
     * 保存图像，格式为png，支持透明图像
     * 相较于jpg格式更加清晰，但对存储空间的要求较大
     * @param img     图像源
     * @param imgName 图像名称/保存目录
     * @param tips    图像标注
     * @throws IOException
     */
    public void saveByName2(Image img,
                            String imgName,
                            String tips) throws IOException {
        // 确保输出目录存在
        File outputDir = new File("./output/" + imgName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        // 写入图像
        File outputFile = new File(outputDir, imgName + "_" + tips + ".png");
        ImageIO.write(img.getRawFile(), "png", outputFile);
    }

    public void showImg(Image img, String name) {
        // 图像宽高
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        // 屏幕宽高
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        // 确定缩放比
        double rateW = (double) screenWidth / imgWidth;
        double rateH = (double) screenHeight / imgHeight;
        double rate;
        if (rateW > rateH) {
            rate = rateH * 0.5;
        } else {
            rate = rateW * 0.4;
        }
        // 窗口大小
        int frameWidth = (int) (imgWidth * rate);
        int frameHeight = (int) (imgHeight * rate) + 80;
        JFrame frame = new JFrame();
        frame.setTitle(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameWidth, frameHeight);

        JLabel label = new JLabel(new ImageIcon(pcsCtrl.resizeImage(img, rate, pcsCtrl.RESIZE_ENTIRETY).getRawFile()));


        final boolean[] png = {false};
        JButton select = new JButton("jpg");
        select.addActionListener(action -> {
            png[0] = !png[0];
            if(png[0]) {
                select.setText("png");
            } else {
                select.setText("jpg");
            }
        });

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(action -> {
            try {
                LocalDateTime date = LocalDateTime.now();
                String time = date.format(DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss")) + "_" + (int) (Math.random() * 1000);
                if(!png[0]) {
                    saveByName(img, "QuickSave", name + time);
                } else {
                    saveByName2(img, "QuickSave", name + time);
                }
                frame.dispose();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(action -> {
            frame.dispose();
        });

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(saveButton);
        panel.add(select);
        panel.add(closeButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    public void showImg(BufferedImage img, String name) {
        // 图像宽高
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        // 屏幕宽高
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        // 确定缩放比
        double rateW = (double) screenWidth / imgWidth;
        double rateH = (double) screenHeight / imgHeight;
        double rate;
        if (rateW > rateH) {
            rate = rateH * 0.3;
        } else {
            rate = rateW * 0.2;
        }
        // 窗口大小
        int frameWidth = (int) (imgWidth * rate);
        int frameHeight = (int) (imgHeight * rate);
        JFrame frame = new JFrame();
        frame.setTitle(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameWidth, frameHeight);

        JLabel label = new JLabel(new ImageIcon(pcsCtrl.resizeImage(new Image(img), rate, pcsCtrl.RESIZE_ENTIRETY).getRawFile()));

        JPanel panel = new JPanel();
        panel.add(label);

        frame.add(panel);
        frame.setVisible(true);
    }

    public List<Image> asyncSplit(Image img, int count, boolean horizontal) {
        long set = System.currentTimeMillis();
        List<Image> result = new ArrayList<>();
        int width = horizontal ? img.getWidth() / (count + 1) : img.getWidth();
        int height = horizontal ? img.getHeight() : img.getHeight() / (count + 1) + 1;
        picServer.imgData(img);
        for (int i = 0; i < count; i++) {
            int x = horizontal ? width * i : 0;
            int y = horizontal ? 0 : height * i;
            if (horizontal) {
                result.add(picServer.getSubImage(width * 2, height, x, y));
            } else {
                result.add(picServer.getSubImage(width, height * 2, x, y));
            }
        }
        System.out.println(System.currentTimeMillis() - set);
        return result;
    }
}
