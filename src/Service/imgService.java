package Service;

import Entity.IMAGE;

public interface imgService {
    /**
     * 获取sobel算子计算边缘
     *
     * @param px
     * @return
     */
    int[][] getSobelEdge(IMAGE px) throws Exception;

    /**
     * 获取Prewitt算子计算的图像边缘
     */
    int[][] getPrewittEdge(IMAGE px) throws Exception;

    int[][] getMarrEdge(IMAGE px) throws Exception;

    /**
     * 填充算法
     *
     * @param px 待处理图像
     * @param radius 处理半径
     * @return 处理图像
     */
    int[][] paddingImg(IMAGE px, int radius);

    /**
     * 膨胀算法
     *
     * @param px 待处理图像
     * @param radius 处理半径
     * @return 处理图像
     */
    int[][] dilateImg(IMAGE px, int radius);

    /**
     * 侵蚀算法
     *
     * @param px 待处理图像
     * @param radius 处理半径
     * @return 处理图像
     */
    int[][] erosionImg(IMAGE px, int radius);
}
