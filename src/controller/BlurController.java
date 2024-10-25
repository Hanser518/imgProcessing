package controller;

import entity.IMAGE;
import service.threadPool.ThreadPoolConV;
import service.threadPool.core.ThreadPoolCore;
import service.threadPool.ThreadPoolStrange;
import service.threadPool.core.ThreadPoolReflectCore;
import service.threadPool.thread.ConVCalc;
import service.threadPool.thread.ConVCalc2;
import service.threadPool.thread.ConVStrange;

import static controller.StylizeController.calcService;

public class BlurController {
    static ThreadPoolCore conV;
    static ThreadPoolReflectCore refConv;

    /**
     * 获取一个半径为1的高斯滤波图像
     *
     * @param img
     * @return IMAGE
     */
    public IMAGE quickGasBlur(IMAGE img) {
        conV = new ThreadPoolConV(img.getPixelMatrix(), calcService.getGasKernel(1), 32);
        conV.start();
        return new IMAGE(conV.getData());
    }

    /**
     * 获取一个半径为size的高斯滤波图像，计算中的最大线程数由maxThreadCount决定
     *
     * @param img
     * @param size
     * @param maxThreadCount
     * @return
     */
    public IMAGE getGasBlur(IMAGE img, int size, int maxThreadCount) {
        double[][] kernel = calcService.getGasKernel(size);
        try {
            refConv = new ThreadPoolReflectCore(img.getPixelMatrix(), kernel, maxThreadCount, new ConVCalc());
            refConv.start();
            return new IMAGE(refConv.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getPixelMatrix(), kernel, maxThreadCount);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }

    public IMAGE getQuickGasBlur(IMAGE img, int size, int maxThreadCount) {
        size = (int) (size * 1.33);
        double[][] kernel = calcService.getGasKernel(size);
        try {
            if (size > 10) {
                double[][] kernel1 = calcService.getGasKernel((int) (size * 0.7));
                refConv = new ThreadPoolReflectCore(img.getPixelMatrix(), kernel1, maxThreadCount, new ConVCalc2());
                refConv.start();
                double[][] kernel2 = calcService.getGasKernel(size - (int) (size * 0.6));
                refConv = new ThreadPoolReflectCore(refConv.getData(), kernel2, maxThreadCount, new ConVCalc2());
                refConv.start();
            } else {
                refConv = new ThreadPoolReflectCore(img.getPixelMatrix(), kernel, maxThreadCount, new ConVCalc2());
                refConv.start();
            }
            return new IMAGE(refConv.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getPixelMatrix(), kernel, maxThreadCount);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }

    public IMAGE getStrangeBlur(IMAGE img, int size) {
        double[][] kernel = calcService.getGasKernel(size);
        try {
            refConv = new ThreadPoolReflectCore(img.getPixelMatrix(), kernel, 24, new ConVStrange());
            refConv.start();
            return new IMAGE(refConv.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getPixelMatrix(), kernel, 24);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }
}
