package Service.Impl;

public class IUGTServiceImpl implements Runnable{
    private ICalculateServiceImpl calculateServer = new ICalculateServiceImpl();
    private int[][] matrix;
    private int[][] gasMap;
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
    public IUGTServiceImpl(int[][] matrix, int[][] gasMap, int maxSize, int start, int step, int W, int H) {
        this.matrix = matrix;
        this.start = start;
        this.step = step;
        this.gasMap = gasMap;
        this.maxSize = maxSize;
        this.W = W;
        this.H = H;
        this.result = new int[step][H];
    }

    @Override
    public void run() {
        for(int i = start;i < start + step && i < W;i ++){
            for(int j = 0;j < H - 1;j ++){
                int rand = gasMap[start][j];
                double[][] kernel = calculateServer.getGasKernel(rand);
                result[i - start][j] = calculateServer.picMarCalc(matrix, kernel, i + (maxSize - rand) / 2, j + (maxSize - rand) / 2);
            }
        }
    }
}
