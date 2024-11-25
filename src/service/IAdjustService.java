package service;

import entity.Image;

public interface IAdjustService {

    /**
     * 压缩图像动态范围
     * @param px
     * @return
     */
    public int[][] compressDynamicRange(Image px);

    /**
     * 调整图像饱和度
     * @param px
     * @param sat
     * @return
     */
    public int[][] AdjustSaturation(Image px, int sat);

    /**
     * 调整图像明暗程度
     * @param px
     * @param val
     * @return
     */
    public int[][] AdjustValue(Image px, int val);

    public int[][] AdjustSaturationAndValue(Image px, int sat, int val);

    /**
     * 图像尺寸缩放
     * @param px
     * @param wSize
     * @param hSize
     * @return
     */
    public Image getReizedImage(Image px, int wSize, int hSize);


    public int[][] test(Image px, int value);


}
