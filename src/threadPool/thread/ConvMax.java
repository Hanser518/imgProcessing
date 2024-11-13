package threadPool.thread;

import entity.EventPool;
import threadPool.core.ThreadCore;

public class ConvMax extends ThreadCore {

    public ConvMax() {
        super();
    }

    public ConvMax(EventPool ep) {
        super(ep);
    }

    @Override
    public int matrixCalc(int x, int y) {
        double mr = 0, mb = 0, mg = 0;
        double value = 0;
        for (int i = 0; i < kernel.length; i ++) {
            for(int j = 0;j < kernel.length; j ++){
                double r, g, b;
                r = (data[x + i][y + j] >> 16) & 0xFF;
                g = (data[x + i][y + j] >> 8) & 0xFF;
                b = (data[x + i][y + j]) & 0xFF;
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
