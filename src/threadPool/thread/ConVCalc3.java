package threadPool.thread;


import entity.EventPool;
import threadPool.core.ThreadCore;

import java.lang.reflect.Method;

public class ConVCalc3 extends ThreadCore {
    public static int[][] fixMatrix = null;

    public ConVCalc3() {
        super();
    }

    private int getStep(int x, int y){
        if(fixMatrix[x][y] == 0){
            fixMatrix[x][y] = 1;
        }
        return (int) (Math.sqrt(kernel.length) / fixMatrix[x][y]) + 1;
    }

    public static void setFocusData(int[][] data){
        ConVCalc3.fixMatrix = data;
    }
    @Override
    public int matrixCalc(int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        int step = getStep(x, y);
        for (int i = 0; i < kernel.length; i += step) {
            for (int j = 0; j < kernel.length; j += step) {
                r += kernel[i][j] * ((data[x + i][y + j] >> 16) & 0xFF);
                g += kernel[i][j] * ((data[x + i][y + j] >> 8) & 0xFF);
                b += kernel[i][j] * ((data[x + i][y + j]) & 0xFF);
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
