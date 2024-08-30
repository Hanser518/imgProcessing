package Service;

import Entity.IMAGE;

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

    /**
     * 卷积计算
     * @param img
     * @param kernel
     * @param multiThreads
     * @param accurateCalculate
     * @return
     */
    public IMAGE convolution(IMAGE img,
                             double[][] kernel,
                             boolean multiThreads, boolean accurateCalculate);

    /**
     * 侵蚀算法
     * @param img
     * @return
     */
    public IMAGE erosion(IMAGE img);

    /**
     * 获取灰度值分布
     * @param img
     * @return
     */
    public Map<Integer, Integer> getGList(IMAGE img);

    /**
     * 获取G值累计分布比例
     * @param img
     * @return
     */
    public Map<Integer, Double> getGAccumulateRate(IMAGE img);

    /**
     * 获取增强矩阵
     * @param img
     * @param theta
     * @return
     */
    public int[][] getEnhanceMatrix(IMAGE img, double theta);
}
