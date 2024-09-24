package Discard;

import Entity.IMAGE;
import Service.ICalculateService;
import Service.IPictureService;
import Service.Impl.ICalculateServiceImpl;
import Service.Impl.IPictureServiceImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImgProcessingController {
    private final ICalculateService calcServer = new ICalculateServiceImpl();
    private final IPictureService picServer = new IPictureServiceImpl();
    // 计算预设
    private boolean multiThreads = false;
    private boolean accurateCalculate = true;
    private boolean gaussianBlur = true;
    private boolean pureEdge = true;
    // 格栅宽度预设
    public final Double GRILLE_REGULAR = 0.0625;
    public final Double GRILLE_MEDIUM = 0.125;
    public final Double GRILLE_BOLD = 0.25;
    // 缩放类型预设
    public final Integer RESIZE_ENTIRETY = 0;
    public final Integer RESIZE_LANDSCAPE = 1;
    public final Integer RESIZE_VERTICAL = 2;

    /**
     * 保存图像，允许保存透明图像，格式固定为png
     * @param img   图像源
     * @param imgName   保存名称
     */
    public void save(IMAGE img,
                     String imgName) throws IOException {
        // 确保输出目录存在
        File outputDir = new File("./output" + imgName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        // 写入图像
        File outputFile = new File(outputDir, imgName + ".png");
        boolean success = ImageIO.write(img.getImg(), "png", outputFile);
        if (!success) {
            throw new IOException("无法保存图像到指定位置: " + outputFile.getAbsolutePath());
        }
    }

    /**
     * 保存图像，格式为jpg，不支持透明图像
     * @param img   图像源
     * @param imgName   图像名称/保存目录
     * @param tips  图像标注
     * @throws IOException
     */
    public void saveByName(IMAGE img,
                           String imgName,
                           String tips) throws IOException {
        // 确保输出目录存在
        File outputDir = new File("./output/" + imgName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        int[][] p = img.getPixelMatrix();
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                result.setRGB(i, j, p[i][j]);
            }
        }
        // 写入图像
        File outputFile = new File(outputDir, imgName + "_" + tips + ".jpg");
        ImageIO.write(result, "jpg", outputFile);
    }

    public void showImg(IMAGE img, String name){
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
        if(rateW > rateH)
            rate = rateH * 0.6;
        else
            rate = rateW * 0.8;
        // 窗口大小
        int frameWidth = (int) (imgWidth * rate);
        int frameHeight = (int) (imgHeight * rate);
        JFrame frame = new JFrame();
        frame.setTitle(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameWidth, frameHeight);

        JLabel label = new JLabel(new ImageIcon(resizeImage(img, rate, RESIZE_ENTIRETY).getImg()));
        JPanel panel = new JPanel();
        panel.add(label);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 开启多线程计算
     */
    public void openMultiThreads() {
        multiThreads = true;
    }

    /**
     * 关闭多线程计算
     */
    public void closeMultiThreads() {
        multiThreads = false;
    }

    /**
     * 开启精确计算
     */
    public void openAccCalc() {
        accurateCalculate = true;
    }

    /**
     * 关闭精确计算
     */
    public void closeAccCalc() {
        accurateCalculate = false;
    }

    /**
     * 开启图片模糊
     */
    public void openGasBlur() {
        gaussianBlur = true;
    }

    /**
     * 关闭图片模糊
     */
    public void closeGasBlur() {
        gaussianBlur = false;
    }


    public void setPureEdge(boolean pureEdge) {
        this.pureEdge = pureEdge;
    }

    /**
     * 高斯滤波
     */
    public IMAGE getGasImage(IMAGE px, int size) {
        double[][] kernel = calcServer.getGasKernel(size);
        return calcServer.convolution(px, kernel, multiThreads, accurateCalculate, true);
    }

    /**
     * Ultra高斯
     */
    public IMAGE getUltraGas(IMAGE px, int baseSize, int maxSize){
        IMAGE raw = picServer.getUltraGas(px, baseSize, maxSize);
        // return getGasImage(raw, 9);
        return raw;
    }

    /**
     * 边缘提取
     */
    public IMAGE getEdgeImage(IMAGE px, boolean erosion) {
        // 高斯滤波降噪
        IMAGE gas = getGasImage(px, 2);
        return picServer.getEdge(gas, multiThreads, accurateCalculate, erosion, pureEdge);
    }

    /**
     * 等值切割
     */
    public List<IMAGE> equalSplit(IMAGE img, int count, boolean horizontal){
        List<IMAGE> result = new ArrayList<>();
        int width = horizontal ? img.getWidth() / count + 1 : img.getWidth() ;
        int height = horizontal ? img.getHeight() : img.getHeight() / count + 1;
        for(int i = 0;i < count;i ++){
            int x = horizontal ? width * i : 0;
            int y = horizontal ? 0 : height * i;
            result.add(picServer.getSubImage(img, width, height, x, y));
        }
        return result;
    }

    /**
     * 异步切割
     */
    public List<IMAGE> asyncSplit(IMAGE img, int count, boolean horizontal){
        List<IMAGE> result = new ArrayList<>();
        int width = horizontal ? img.getWidth() / (count + 1) : img.getWidth() ;
        int height = horizontal ? img.getHeight() : img.getHeight() / (count + 1) + 1;
        for(int i = 0;i < count;i ++){
            int x = horizontal ? width * i : 0;
            int y = horizontal ? 0 : height * i;
            if(horizontal)
                result.add(picServer.getSubImage(img, width * 2, height, x, y));
            else
                result.add(picServer.getSubImage(img, width, height * 2, x, y));
        }
        return result;
    }

    /**
     * 定值切割
     */
    public List<IMAGE> valueSplit(IMAGE img, int value, boolean horizontal){
        List<IMAGE> result = new ArrayList<>();
        int width = horizontal ? value : img.getWidth();
        int height = horizontal ? img.getHeight() : value;
        int length = horizontal ? img.getWidth() : img.getHeight();
        int point = 0;
        int x = horizontal ? point : 0;
        int y = horizontal ? 0 : point;
        while(point < length){
            result.add(picServer.getSubImage(img, width, height, x, y));
            point += value;
            x = horizontal ? point : 0;
            y = horizontal ? 0 : point;
        }
        return result;
    }

    /**
     * 改变图像大小
     */
    public IMAGE resizeImage(IMAGE img, double radio, int type){
        if(type == 0){
            return picServer.getReizedImage(img, (int) (img.getWidth() * radio), (int) (img.getHeight() * radio));
        }else if(type == 1){
            return picServer.getReizedImage(img, (int) (img.getWidth() * radio), img.getHeight());
        }else if(type == 2){
            return picServer.getReizedImage(img, img.getWidth(), (int) (img.getHeight() * radio));
        }
        return img;
    }

    /**
     * 对输入图像进行格栅处理
     */
    public IMAGE getGrilleImage(IMAGE px, double radio, int type){
        IMAGE enhance;
        if(gaussianBlur){
            // 利用缩放降低计算量
            IMAGE min = resizeImage(px, 0.5, RESIZE_ENTIRETY);
            IMAGE gas = getUltraGas(min, 36, 54);
            IMAGE normal = resizeImage(gas, 2.0, RESIZE_ENTIRETY);
            enhance = picServer.getEnhanceImage2(normal);
        }else{
            enhance = picServer.getEnhanceImage2(px);
        }
        List<IMAGE> imgList = asyncSplit(enhance, (int) (1 / radio), false);
        return combineImages(imgList, type, false);
    }

    /**
     * 对输入的图组按比例进行组合
     */
    public IMAGE combineImages(List<IMAGE> imgList, int type, boolean horizontal){
        List<IMAGE> resized = new ArrayList<>();
        if(type == 0){
            for(IMAGE img: imgList){
                resized.add(resizeImage(img, 0.5, horizontal ? RESIZE_LANDSCAPE : RESIZE_VERTICAL));
            }
        }else if(type == 1){
            for(int i = 0;i < imgList.size(); i ++){
                if(i % 2 == 0)
                    resized.add(resizeImage(imgList.get(i), 0.8, horizontal ? RESIZE_LANDSCAPE : RESIZE_VERTICAL));
                else
                    resized.add(resizeImage(imgList.get(i), 0.2, horizontal ? RESIZE_LANDSCAPE : RESIZE_VERTICAL));
            }
        }else if(type == 2){
            for(int i = 0;i < imgList.size(); i ++){
                if(i % 2 == 0)
                    resized.add(resizeImage(imgList.get(i), 0.97, horizontal ? RESIZE_LANDSCAPE : RESIZE_VERTICAL));
                else {
                    resized.add(resizeImage(imgList.get(i), 0.03, horizontal ? RESIZE_LANDSCAPE : RESIZE_VERTICAL));
                }
            }
        }else{
            resized = imgList;
        }
        return picServer.getCombineImage(resized, horizontal);
    }

    public IMAGE getEnhanceImage(IMAGE px, double theta){
        return picServer.getEnhanceImage(px, theta);
    }

}
