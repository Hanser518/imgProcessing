package threadPool.thread;


import entity.EventPool;
import threadPool.core.ThreadCore;

public class ConVCalc2 extends ThreadCore {

    public ConVCalc2() {
        super();
    }


    public ConVCalc2(EventPool ep) {
        super(ep);
    }

    private int getStep(){
        return (int) (Math.sqrt(kernel.length) / 2) + 1;
    }

    private int getSide() {
        return (int) (kernel.length / 1.4) + 1;
    }

    @Override
    public int matrixCalc(int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        int step = 1;
        int side = kernel.length;
//        if(data.length * data[0].length > 1.2e7){
//            step = getStep();
//        }else{
//            side = getSide();
//        }
        step = getStep();
        for (int i = 0; i < kernel.length; i += step) {
            for (int j = 0; j < kernel[0].length; j += step) {
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
