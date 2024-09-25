package Service.Extends.Thread;

import Entity.EventPool;
import Service.CORE.ThreadCore;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

public class ConVStrange extends ThreadCore {
    static ICalculateService calcService = new ICalculateServiceImpl();
    public static double[][] kernels;

    public ConVStrange() {
        super();
    }

    public ConVStrange(EventPool ep) {
        super(ep);
    }

    // 获取不同位置的步长
    private int getStep(int loc, int len) {
        int dis = Math.abs(len / 2 - loc) + 1;
        return (int) (Math.sqrt(kernel.length) / 2) + 1;
    }

    public static void setK(){
        kernels = calcService.getGasKernel(kernel.length / 6);
    }

    @Override
    public int matrixCalc(int x, int y) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        // int value = Math.abs((x * y) % (x + y + 1));
        int random = (int) (Math.random() * 100) + 1;
        if ((x + y) % random > 5 || (x + y) % 100 > random || (Math.abs(x - y) % 100 > random)) {
            int s = (kernel.length - kernels.length) / 2;
            for (int i = s; i < kernels.length + s; i++) {
                for (int j = s; j < kernels.length + s; j++) {
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

//        int len = kernel.length;
//        for (int i = 0; i < kernel.length; i += getStep(i, len)) {
//            for(int j = 0;j < kernel.length; j += getStep(j, len)){
//                r += kernel[i][j] * ((data[x + i][y + j] >> 16) & 0xFF);
//                g += kernel[i][j] * ((data[x + i][y + j] >> 8) & 0xFF);
//                b += kernel[i][j] * ((data[x + i][y + j]) & 0xFF);
//                rate += kernel[i][j];
//            }
//        }
        rate = rate < 10e-2 ? 1.0 : rate;
        r = Math.abs(r / rate);
        g = Math.abs(g / rate);
        b = Math.abs(b / rate);
        return (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
    }
}
