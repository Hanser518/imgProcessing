package MultiThread;

import Service.Impl.ICalculateServiceImpl;

public class TUGServiceImpl implements Runnable{
    private ICalculateServiceImpl calculateServer = new ICalculateServiceImpl();
    private int[][] matrix;
    private int base;
    private int maxSize;
    private int start, step;
    private int W, H;

    public int[][] result;

    /**
     * 初始化线程参数
     *
     * @param matrix
     * @param start
     * @param step
     */
    public TUGServiceImpl(int[][] matrix, int base, int maxSize, int start, int step, int W, int H) {
        this.matrix = matrix;
        this.start = start;
        this.step = step;
        this.base = base;
        this.maxSize = maxSize;
        this.W = W;
        this.H = H;
        this.result = new int[step][H];
    }

    @Override
    public void run() {
        for(int i = start;i < start + step && i < W;i ++){
            for(int j = 0;j < H - 1;j ++){
                int rand = (int)(Math.random() * base) + (maxSize - base);
                double[][] kernel = calculateServer.getGasKernel(rand);
                result[i - start][j] = calculateServer.picMarCalc(matrix, kernel, i + (maxSize - rand) / 2, j + (maxSize - rand) / 2, true);
            }
        }
    }
}
