package service;

import entity.Image;

import java.util.List;

public interface IPictureService {
    /**
     * 图像切割
     * @param img 原图
     * @param width 切割宽度
     * @param height 切割高度
     * @param startX 切割起始点
     * @param startY
     * @return
     */
    public Image getSubImage(Image img,
                             int width, int height,
                             int startX, int startY);

    /**
     * 获取拼接后的图像
     * @param images 原图组
     * @param horizontal 是否水平方向处理
     * @return
     */
    public Image getCombineImage(List<Image> images, boolean horizontal);

    /**
     * 图像尺寸缩放
     * @param img
     * @param w
     * @param h
     * @return
     */
    public Image getReizedImage(Image img, int w, int h);

    public Image getUltraGas(Image img, int baseSize, int maxSize);

    /**
     * 获取灰图
     * @param img
     * @return
     */
    public Image getGrayImage(Image img);

    public Image getCalcGray(Image img);

    public Image getEdge(Image img, boolean multiThreads, boolean accurateCalculate, boolean erosion, boolean pureEdge);

    /**
     * 图像亮度增强
     * @param img
     * @param theta
     * @return
     */
    public Image getEnhanceImage(Image img, double theta);

    public Image getEnhanceImage2(Image img);

    public Image getGammaFix(Image img, double param);

    void imgData(Image img);
    Image getSubImage(int width, int height, int startX, int startY);
}