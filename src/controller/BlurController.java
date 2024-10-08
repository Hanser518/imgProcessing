package controller;

import entity.IMAGE;
import service.threadPool.ThreadPoolConV;
import service.threadPool.core.ThreadPoolCore;
import service.threadPool.ThreadPoolStrange;

import static controller.StylizeController.calcService;

public class BlurController {
    static ThreadPoolCore conV;

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

    public IMAGE getStrangeBlur(IMAGE img, int size){
        double[][] kernel = calcService.getGasKernel(size);
        conV = new ThreadPoolStrange(img.getPixelMatrix(), kernel, 24);
        conV.start();
        return new IMAGE(conV.getData());
    }
}
