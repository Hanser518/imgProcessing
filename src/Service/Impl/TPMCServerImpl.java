package Service.Impl;

public class TPMCServerImpl implements Runnable{
    private ICalculateServiceImpl calculateServer = new ICalculateServiceImpl();
    private int[][] matrix;
    private double[][] kernel;
    private double[] convKernel;
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
        this.convKernel = getKernel(kernel.length / 2);
        this.start = start;
        this.step = step;
        this.W = W;
        this.H = H;
        this.result = new int[step][H];
    }

    public double[] getKernel(int size){
        double theta = Math.sqrt(Math.log(0.1) * -1.0 * 2 / Math.pow(-size, 2));
        int kernelSize = size * 2 + 1;
        double[][] gasKernel = new double[kernelSize][kernelSize];
        double[] w = new double[kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            w[i] = Math.exp(-1.0 * (i - size) * (i - size) / 2 * theta * theta);
        }
        return w;
    }

    public int picMarCalc(int[][] matrix, int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        for (int i = 0; i < convKernel.length; i++) {
            r += convKernel[i] * ((matrix[x + i][y + convKernel.length / 2] >> 16) & 0xFF);
            g += convKernel[i] * ((matrix[x + i][y + convKernel.length / 2] >> 8) & 0xFF);
            b += convKernel[i] * ((matrix[x + i][y + convKernel.length / 2]) & 0xFF);
            rate += convKernel[i];
        }
        for (int j = 0; j < convKernel.length; j ++) {
            r += convKernel[j] * ((matrix[x + convKernel.length / 2][y + j] >> 16) & 0xFF);
            g += convKernel[j] * ((matrix[x + convKernel.length / 2][y + j] >> 8) & 0xFF);
            b += convKernel[j] * ((matrix[x + convKernel.length / 2][y + j]) & 0xFF);
            rate += convKernel[j];
        }

        rate = rate < 10e-2 ? 1.0 : rate;
        r = Math.abs(r / rate);
        g = Math.abs(g / rate);
        b = Math.abs(b / rate);
        // System.out.println(r + " " + g + " " + b);
        return (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
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
