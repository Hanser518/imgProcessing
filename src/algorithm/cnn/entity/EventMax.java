package algorithm.cnn.entity;

import algorithm.cnn.core.EventCore;

public class EventMax extends EventCore {
    @Override
    public void setData(int[][] data) {
        this.data = data;
        this.result = new int[data.length][data[0].length];
    }

    @Override
    public void setKernel(double[][] kernel) {
        this.kernel = kernel;
        this.result = new int[data.length - kernel.length / 2 * 2][data[0].length - kernel[0].length / 2 * 2];
    }

    @Override
    public void setStep(int step) {
        this.step = step;
        this.result = new int[result.length / step][result[0].length / step];
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
        double mr = 0, mb = 0, mg = 0;
        double value = 0;
        for (int i = 0; i < kernel.length; i ++) {
            for(int j = 0;j < kernel.length; j ++){
                double r, g, b;
                r = (matrix[x + i][y + j] >> 16) & 0xFF;
                g = (matrix[x + i][y + j] >> 8) & 0xFF;
                b = (matrix[x + i][y + j]) & 0xFF;
                double v = r * 0.287 + g * 0.511 + b * 0.202;
                if(v > value){
                    mr = r;
                    mg = g;
                    mb = b;
                    value = v;
                }
            }
        }
        return (255 << 24) | ((int) mr << 16) | ((int) mg << 8) | (int) mb;
    }
}
