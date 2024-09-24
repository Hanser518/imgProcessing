package Service;

import Entity.IMAGE;

public interface imgService {
    /**
     * 获取sobel算子计算边缘
     * @param px
     * @return
     */
    public int[][] getSobelEdge(IMAGE px);

    /**
     * 获取Prewitt算子计算的图像边缘
     */
    public int[][] getPrewittEdge(IMAGE px);

    public int[][] getMarrEdge(IMAGE px);

    /**
     * 膨胀算法
     * @param px
     * @return
     */
    public int[][] dilateImg(IMAGE px);
}
