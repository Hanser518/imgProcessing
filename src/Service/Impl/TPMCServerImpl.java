package Service.Impl;

public class TPMCServerImpl implements Runnable{
    private ICalculateServiceImpl calculateServer = new ICalculateServiceImpl();
    private int[][] matrix;
    private double[][] kernel;
    private int start, step;
    private int W, H;

    public int[][] result;

    /**
     * 初始化线程参数
     * @param matrix
     * @param kernel
     * @param start
     * @param step
     */
    public TPMCServerImpl(int[][] matrix, double[][] kernel,
                          int start, int step, int W, int H) {
        this.matrix = matrix;
        this.kernel = kernel;
        this.start = start;
        this.step = step;
        this.W = W;
        this.H = H;
        this.result = new int[step][H];
    }

    @Override
    public void run() {
        for(int i = start;i < start + step && i < W;i ++){
            for(int j = 0;j < H - 1;j ++){
                result[i - start][j] = calculateServer.picMarCalc(matrix, kernel, i, j, true);
            }
        }
    }
}
