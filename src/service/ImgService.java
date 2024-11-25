package service;

import entity.Image;

public interface ImgService {
    /**
     * 获取sobel算子计算边缘
     *
     * @param px
     * @return
     */
    int[][] getSobelEdge(Image px) throws Exception;

    /**
     * 获取Prewitt算子计算的图像边缘
     */
    int[][] getPrewittEdge(Image px) throws Exception;

    int[][] getMarrEdge(Image px) throws Exception;

    /**
     * 填充算法
     *
     * @param px 待处理图像
     * @param radius 处理半径
     * @return 处理图像
     */
    int[][] paddingImg(Image px, int radius);

    /**
     * 膨胀算法
     *
     * @param px 待处理图像
     * @param radius 处理半径
     * @return 处理图像
     */
    int[][] dilateImg(Image px, int radius);

    /**
     * 侵蚀算法
     *
     * @param px 待处理图像
     * @param radius 处理半径
     * @return 处理图像
     */
    int[][] erosionImg(Image px, int radius);

    int[][] traceImg(Image px) throws Exception;

    int[][] getThumbnail(Image px, int step);
}
