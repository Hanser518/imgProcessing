package Controller;

import Entity.IMAGE;
import Service.ICalculateService;
import Service.IPictureService;
import Service.Impl.ICalculateServiceImpl;
import Service.Impl.IPictureServiceImpl;

import javax.imageio.ImageIO;
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
     * 保存图像
     * @param img
     * @param imgName
     */
    public void saveByName(IMAGE img,
                           String imgName) throws IOException {
        // 确保输出目录存在
        File outputDir = new File("./output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 写入图像
        File outputFile = new File(outputDir, imgName + ".png");
        boolean success = ImageIO.write(img.getImg(), "png", outputFile);
        if (!success) {
            throw new IOException("无法保存图像到指定位置: " + outputFile.getAbsolutePath());
        }
        // ImageIO.write(img.getImg(), "jpg", new File("./output/" + imgName + ".jpg"));
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
        int width = horizontal ? img.getWidth() / (count + 1) + 1 : img.getWidth() ;
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
        List<IMAGE> imgList = asyncSplit(enhance, (int) (1 / radio), true);
        return combineImages(imgList, type);
    }

    /**
     * 对输入的图组按比例进行组合
     */
    public IMAGE combineImages(List<IMAGE> imgList, int type){
        List<IMAGE> resized = new ArrayList<>();
        if(type == 0){
            for(IMAGE img: imgList){
                resized.add(resizeImage(img, 0.5, RESIZE_LANDSCAPE));
            }
        }else if(type == 1){
            for(int i = 0;i < imgList.size(); i ++){
                if(i % 2 == 0)
                    resized.add(resizeImage(imgList.get(i), 0.7, RESIZE_LANDSCAPE));
                else
                    resized.add(resizeImage(imgList.get(i), 0.3, RESIZE_LANDSCAPE));
            }
        }else if(type == 2){
            for(int i = 0;i < imgList.size(); i ++){
                if(i % 2 == 0)
                    resized.add(resizeImage(imgList.get(i), 0.96, RESIZE_LANDSCAPE));
                else {
                    resized.add(resizeImage(imgList.get(i), 0.04, RESIZE_LANDSCAPE));
                }
            }
        }else{
            resized = imgList;
        }
        return picServer.getCombineImage(resized, true);
    }

    public IMAGE getEnhanceImage(IMAGE px, double theta){
        return picServer.getEnhanceImage(px, theta);
    }

}
