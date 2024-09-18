package Service.Thread;

import Entity.EventPool;

public class ActiveConfirm implements Runnable {
    EventPool ep;
    int[][] data;
    double[][] kernel;
    int threshold;


    public ActiveConfirm() {

    }

    public ActiveConfirm(EventPool ep, int[][] data, double[][] kernel, int threshold) {
        this.ep = ep;
        this.data = data;
        this.kernel = kernel;
        this.threshold = threshold;
    }

    // 活性检测
    public int picMarCalc(int[][] matrix, double[][] kernel, int x, int y) {
        double r = 0, g = 0, b = 0;
        int rate = 0;
        int active = 0;
        for (int i = 0; i < kernel.length; i ++) {
            for(int j = 0;j < kernel.length; j ++){
                if(kernel[i][j] == 1){
                    r = ((matrix[x + i][y + j] >> 16) & 0xFF) > threshold ? 1 : 0;
                    g = ((matrix[x + i][y + j] >> 8) & 0xFF) > threshold ? 1 : 0;
                    b = (matrix[x + i][y + j] & 0xFF) > threshold ? 1 : 0;
                    active += r + g + b > 1 ? 1 : 0;
                    rate ++;
                }
            }
        }
        if(active > rate * 0.13) return 1;
        else return 0;
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
