package frame2.entity.base;

import entity.Image;
import frame2.entity.Event;
import frame2.entity.record.Local;

public class AbstractEventBlur extends Event {


    /**
     * 初始化
     *
     * @param img
     * @param kernel
     * @param threadThreshold
     */
    public AbstractEventBlur(Image img, double[][] kernel, Integer threadThreshold) {
        super(img, kernel, threadThreshold);
    }

    private int getStep(){
        return (int) (Math.sqrt(kernel.length) / 2) + 1;
    }

    private int matrixCalc(int[][] matrix, double[][] kernel, int x, int y){
        double r = 0, g = 0, b = 0;
        double rate = 0;
        int step = getStep();
        for (int i = 0; i < kernel.length; i += step) {
            for(int j = 0;j < kernel.length; j += step){
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

    @Override
    protected void function(Local local) {
        for (int i = 0; i < local.width(); i++) {
            for (int j = 0; j < local.height(); j++) {
                result[i + local.x()][j + local.y()] = matrixCalc(data, kernel, i + local.x(), j + local.y());
            }
        }
    }
}
