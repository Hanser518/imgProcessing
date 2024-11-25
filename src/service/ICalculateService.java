package service;

import entity.Image;

import java.util.Map;

public interface ICalculateService {
    /**
     * 计算高斯核
     * @param size
     * @param theta
     * @return
     */
    public double[][] getGasKernel(int size,
                                   double theta);
    public double[][] getGasKernel(int size);

    public int[][] pixFill(int[][] img, double[][] kernel);

    public int picMarCalc(int[][] matrix, double[][] kernel, int x, int y, boolean negativeFix);

    /**
     * 卷积计算
     * @param img
     * @param kernel
     * @param multiThreads
     * @param accurateCalculate
     * @return
     */
    public Image convolution(Image img,
                             double[][] kernel,
                             boolean multiThreads, boolean accurateCalculate, boolean negativeFix);

    /**
     * 侵蚀算法
     * @param img
     * @return
     */
    public Image erosion(Image img);

    /**
     * 获取灰度值分布
     * @param img
     * @return
     */
    public Map<Integer, Integer> getGList(Image img);

    /**
     * 获取G值分布比例
     * @param img
     * @return
     */
    public Map<Integer, Double> getGRate(Image img);

    /**
     * 获取G值累计分布比例
     * @param img
     * @return
     */
    public Map<Integer, Double> getGAccumulateRate(Image img);

    /**
     * 获取Active分布
     * @param img
     * @return
     */
    public Map<Integer, Integer> getActiveList(Image img);

    /**
     * 获取Active值分布比例
     * @param img
     * @return
     */
    public Map<Integer, Double> getActiveRate(Image img);

    /**
     * 获取Active值累计分布比例
     * @param img
     * @return
     */
    public Map<Integer, Double> getActiveAccumulateRate(Image img);

    /**
     * 获取增强矩阵
     * @param img
     * @param theta
     * @return
     */
    public int[][] getEnhanceMatrix(Image img, double theta);

    public int[][] getGasMap(Image img, int base, int top);

    public int getDirection(int[][] map, int x, int y);

    public int[][] getHistogram(Image img);
}
