package Controller;

import Entity.IMAGE;
import Service.Extends.ThreadPoolConV;
import Service.ThreadPoolService;

import static Controller.StylizeController.calcService;

public class BlurController {
    static ThreadPoolService conV;

    /**
     * 获取一个半径为1的高斯滤波图像
     * @param img
     * @return IMAGE
     */
    public IMAGE quickGasBlur(IMAGE img){
        conV = new ThreadPoolConV(img.getPixelMatrix(), calcService.getGasKernel(1), 32);
        conV.start();
        return new IMAGE(conV.getData());
    }

    /**
     * 获取一个半径为size的高斯滤波图像，计算中的最大线程数由maxThreadCount决定
     * @param img
     * @param size
     * @param maxThreadCount
     * @return
     */
    public IMAGE getGasBlur(IMAGE img, int size, int maxThreadCount){
        double[][] kernel = calcService.getGasKernel(size);
        conV = new ThreadPoolConV(img.getPixelMatrix(), kernel, maxThreadCount);
        conV.start();
        return new IMAGE(conV.getData());
    }


}
