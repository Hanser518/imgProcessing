package algorithm.cnn.entity;

import algorithm.cnn.core.EventCore;

public class EventConv extends EventCore {
    @Override
    public void setData(int[][] data) {
        this.data = data;
        this.result = new int[data.length][data[0].length];
    }

    @Override
    public void setKernel(double[][] kernel) {
        this.kernel = kernel;
    }

    @Override
    public void setStep(int step) {
        this.step = step;
        this.result = new int[data.length / step][data[0].length / step];
    }

    @Override
    public void func() {
        // System.out.println(data.length - kernel.length);
        for (int i = 0; i < data.length - kernel.length / 2 * 2; i += step) {
            for (int j = 0; j < data[0].length - kernel[0].length / 2 * 2; j += step) {
                result[i / step][j / step] = matrixCalc(data, kernel, i, j);
            }
        }
    }

    public static int matrixCalc(int[][] matrix, double[][] kernel, int x, int y){
        double r = 0, g = 0, b = 0;
        double rate = 0;
        for (int i = 0; i < kernel.length; i ++) {
            for(int j = 0;j < kernel.length; j ++){
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
        return (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
    }
}
