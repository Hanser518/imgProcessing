package threadPool.thread;

import entity.EventPool;
import threadPool.core.ThreadCore;

public class PaperBlur extends ThreadCore {

    public PaperBlur(){
        super();
    }

    public PaperBlur(EventPool ep){
        super(ep);
    }

    // 获取不同位置的步长
    private int getStep(int loc, int len) {
        int dis = Math.abs(len / 2 - loc) + 1;
        return (int) (Math.sqrt(kernel.length) / 2) + 1;
//        if(dis % 3 != 0)
//            return (int) Math.log(dis) + dis % 3;
//        else
//            return (int) Math.log(kernel.length) + 1;
    }

    @Override
    public int matrixCalc(int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        int len = kernel.length;
        int step = (int) (Math.sqrt(kernel.length) / 2) + 1;
        for (int i = 0; i < kernel.length; i += step) {
            for(int j = 0;j < kernel.length; j += step){
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
