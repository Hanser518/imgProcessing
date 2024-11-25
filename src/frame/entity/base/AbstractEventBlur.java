package frame.entity.base;

import entity.Image;
import frame.entity.Event;
import frame.entity.record.Local;

public class AbstractEventBlur extends Event {
    /**
     * 初始化
     *
     * @param img
     * @param kernel
     */
    public AbstractEventBlur(Image img, double[][] kernel, Local local) {
        super(img, kernel, local);
    }

    @Override
    public void function() {
        for (int i = 0; i < local.width(); i++) {
            for (int j = 0; j < local.height(); j++) {
                result[i][j] = matrixCalc(data, kernel, i + local.x(), j + local.y());
            }
        }
    }

    private int matrixCalc(int[][] matrix, double[][] kernel, int x, int y){
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
