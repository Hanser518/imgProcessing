package service.threadPool.thread;


import entity.EventPool;
import service.threadPool.core.ThreadCore;

public class ConVCalc extends ThreadCore {

    public ConVCalc() {
        super();
    }


    public ConVCalc(EventPool ep) {
        super(ep);
    }

    @Override
    public int matrixCalc(int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        for (int i = 0; i < kernel.length; i ++) {
            for(int j = 0;j < kernel.length; j ++){
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
