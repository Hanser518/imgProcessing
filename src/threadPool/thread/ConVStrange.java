package threadPool.thread;

import entity.EventPool;
import threadPool.core.ThreadCore;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

public class ConVStrange extends ThreadCore {
    static ICalculateService calcService = new ICalculateServiceImpl();
    public static double[][] kernels = null;

    public ConVStrange() {
        super();
    }

    public ConVStrange(EventPool ep) {
        super(ep);
        if(kernels == null)
            setK();
    }

    // 获取不同位置的步长
    private int getStep(int loc, int len) {
        int dis = Math.abs(len / 2 - loc);
        return (int) (Math.sqrt(dis + 1)) + 1;
    }

    public static void setK(){
        kernels = calcService.getGasKernel(kernel.length / 2);
    }

    @Override
    public int matrixCalc(int x, int y) {
        setK();
        double r = 0, g = 0, b = 0;
        double rate = 0;
        // (x + y) % random > 5 || (x + y) % 100 > random || (Math.abs(x - y) % 100 > random)
        if (Math.random() > 0.075) {
            int s = (kernel.length - kernels.length) / 2;
            int len = kernels.length;
            for (int i = s; i < kernels.length + s; i += getStep(i, len)) {
                for (int j = s; j < kernels.length + s; j += getStep(i, len)) {
                    r += kernels[i - s][j - s] * ((data[x + i][y + j] >> 16) & 0xFF);
                    g += kernels[i - s][j - s] * ((data[x + i][y + j] >> 8) & 0xFF);
                    b += kernels[i - s][j - s] * ((data[x + i][y + j]) & 0xFF);
                    rate += kernels[i - s][j - s];
                }
            }
        } else {
            int len = kernel.length;
            for (int i = 0; i < kernel.length; i += getStep(i, len)) {
                for (int j = 0; j < kernel.length; j += getStep(j, len)) {
                    r += kernel[i][j] * ((data[x + i][y + j] >> 16) & 0xFF);
                    g += kernel[i][j] * ((data[x + i][y + j] >> 8) & 0xFF);
                    b += kernel[i][j] * ((data[x + i][y + j]) & 0xFF);
                    rate += kernel[i][j];
                }
            }
        }
        rate = rate < 10e-2 ? 1.0 : rate;
        r = Math.abs(r / rate);
        g = Math.abs(g / rate);
        b = Math.abs(b / rate);
        return (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
    }
}
