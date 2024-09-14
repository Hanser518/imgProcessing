package MultiThread.Thread;


import MultiThread.entity.EventPool;

public class TConcurrentRequest implements Runnable {
    EventPool ep;
    int[][] data;
    double[][] kernel;
    double[] convKernel;
    double[] param;


    public TConcurrentRequest() {

    }

    public TConcurrentRequest(EventPool ep, int[][] data, double[][] kernel) {
        this.ep = ep;
        this.data = data;
        this.kernel = kernel;
    }

    public void setConvKernel(double[] k) {
        this.convKernel = k;
    }

    public void setParam(double[] param){
        this.param = param;
    }

    private int getStep() {
        if (kernel.length < 5) return 1;
        if (kernel.length < 15) return 2;
        else return (int) Math.sqrt(kernel.length) / 2;
    }

    private int getStep(int loc, int len) {
        int dis = Math.abs(len / 2 - loc) + 1;
        if(dis < 12) return 1;
        return (int) Math.log(dis - 11) + 1;
    }


    // 传统卷积
    public int picMarCalc(int[][] matrix, double[][] kernel, int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        for (int i = 0; i < kernel.length; i += getStep()) {
            for(int j = 0;j < kernel.length; j += getStep()){
                r += kernel[i][j] * ((matrix[x + i][y + j] >> 16) & 0xFF);
                g += kernel[i][j] * ((matrix[x + i][y + j] >> 8) & 0xFF);
                b += kernel[i][j] * ((matrix[x + i][y + j]) & 0xFF);
                rate += kernel[i][j];
            }
        }

        rate = rate < 10e-2 ? 1.0 : rate;
        r = Math.abs(r / rate);
        g = Math.abs(g / rate);
        b = Math.abs(b / rate);
        // System.out.println(r + " " + g + " " + b);
        return (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
    }

    // IIR高斯递归卷积
    public int IIRConV(int[][] matrix, int x, int y){
        double r = 0, g = 0, b = 0;
        double rate = 0;
        r += param[5] * (matrix[x - 1][y] - 2 * matrix[x][y] + matrix[x + 1][y]) + (param[1] * matrix[x - 1][y] + param[2] * matrix[x][y]);
        return 0;
    }

    @Override
    public void run() {
        for (int i = ep.sx; i < ep.sx + ep.width; i++) {
            for (int j = ep.sy; j < ep.sy + ep.height; j++) {
                ep.result[i - ep.sx][j - ep.sy] = picMarCalc(data, kernel, i, j);
            }
        }
        System.out.print(ep.index + "|");
    }
}
