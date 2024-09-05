package Service;

import Entity.IMAGE;

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
    public IMAGE getSubImage(IMAGE img,
                             int width, int height,
                             int startX, int startY);

    /**
     * 获取拼接后的图像
     * @param images 原图组
     * @param horizontal 是否水平方向处理
     * @return
     */
    public IMAGE getCombineImage(List<IMAGE> images, boolean horizontal);

    /**
     * 图像尺寸缩放
     * @param img
     * @param w
     * @param h
     * @return
     */
    public IMAGE getReizedImage(IMAGE img, int w, int h);

    public IMAGE getUltraGas(IMAGE img, int baseSize, int maxSize);

    /**
     * 获取灰图
     * @param img
     * @return
     */
    public IMAGE getGrayImage(IMAGE img);

    public IMAGE getCalcGray(IMAGE img);

    public IMAGE getEdge(IMAGE img, boolean multiThreads, boolean accurateCalculate, boolean erosion, boolean pureEdge);

    /**
     * 图像亮度增强
     * @param img
     * @param theta
     * @return
     */
    public IMAGE getEnhanceImage(IMAGE img, double theta);

    public IMAGE getEnhanceImage2(IMAGE img);

    public IMAGE getGammaFix(IMAGE img, double param);
}