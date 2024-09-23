package Service;

import Entity.IMAGE;

public interface IAdjustService {

    /**
     * 压缩图像动态范围
     * @param px
     * @return
     */
    public int[][] compressDynamicRange(IMAGE px);

    public int[][] AdjustSaturation(IMAGE px, int sat);

    public int[][] AdjustValue(IMAGE px, int val);
}
