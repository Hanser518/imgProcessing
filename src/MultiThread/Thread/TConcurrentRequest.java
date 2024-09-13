package MultiThread.Thread;


import MultiThread.entity.EventPool;

public class TConcurrentRequest implements Runnable{
    EventPool ep;
    int[][] data;
    double[][] kernel;


    public TConcurrentRequest() {

    }

    public TConcurrentRequest(EventPool ep, int[][] data, double[][] kernel){
        this.ep = ep;
        this.data = data;
        this.kernel = kernel;
    }

    public int picMarCalc(int[][] matrix, double[][] kernel, int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[0].length; j++) {
                r += kernel[i][j] * ((matrix[x + i][y + j] >> 16) & 0xFF);
                g += kernel[i][j] * ((matrix[x + i][y + j] >> 8) & 0xFF);
                b += kernel[i][j] * (matrix[x + i][y + j] & 0xFF);
                rate += kernel[i][j];
            }
        }
        rate = rate < 10e-2 ? 1.0 : rate;
        r = Math.abs(r / rate);
        g = Math.abs(g / rate);
        b = Math.abs(b / rate);
        // System.out.println(r + " " + g + " " + b);
        int px = (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
        return px;
    }

    @Override
    public void run() {
        for(int i = ep.sx;i < ep.sx + ep.width;i ++){
            for(int j = ep.sy;j < ep.sy + ep.height;j ++){
                ep.result[i - ep.sx][j - ep.sy] = picMarCalc(data, kernel, i, j);
            }
        }
        System.out.print(ep.index + "|");
    }
}
