package Service;

import Entity.IMAGE;

public interface IAdjustService {

    /**
     * 压缩图像动态范围
     * @param px
     * @return
     */
    public int[][] compressDynamicRange(IMAGE px);

    /**
     * 调整图像饱和度
     * @param px
     * @param sat
     * @return
     */
    public int[][] AdjustSaturation(IMAGE px, int sat);

    /**
     * 调整图像明暗程度
     * @param px
     * @param val
     * @return
     */
    public int[][] AdjustValue(IMAGE px, int val);

    /**
     * 图像尺寸缩放
     * @param px
     * @param wSize
     * @param hSize
     * @return
     */
    public IMAGE getReizedImage(IMAGE px, int wSize, int hSize);


    public int[][] test(IMAGE px, int value);

}
