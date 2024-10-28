package controller;

import entity.IMAGE;
import threadPool.pool.ThreadPoolConV;
import threadPool.core.ThreadPoolCore;
import threadPool.core.ThreadPoolReflectCore;
import threadPool.thread.ConVCalc;
import threadPool.thread.ConVCalc2;
import threadPool.thread.ConVStrange;

import static controller.StylizeController.calcService;
import static controller.StylizeController.imgServ;

public class BlurController {
    static ThreadPoolCore conV;
    static ThreadPoolReflectCore conV2;
    static ProcessingController prcCtrl = new ProcessingController();

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
            conV2 = new ThreadPoolReflectCore(img.getPixelMatrix(), kernel, maxThreadCount, new ConVCalc());
            conV2.start();
            return new IMAGE(conV2.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getPixelMatrix(), kernel, maxThreadCount);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }

    public IMAGE getQuickGasBlur(IMAGE img, int size, int maxThreadCount) {
        long set = System.currentTimeMillis();
        size = size * 0.9 > 1 ? (int)(size * 0.9) : 1;
        double[][] kernel = calcService.getGasKernel(size);
        int step = (int) (1 + Math.sqrt(size));
        System.out.println(step);
        int[][] matrix = imgServ.getThumbnail(img, Math.min(step, 5));
        try {
            if (size > 5) {
                double[][] kernel1 = calcService.getGasKernel((int) (size * 0.66));
                conV2 = new ThreadPoolReflectCore(matrix, kernel1, maxThreadCount, new ConVCalc2());
                conV2.start();
                double[][] kernel2 = calcService.getGasKernel(size - (int) (size * 0.66));
                conV2 = new ThreadPoolReflectCore(conV2.getData(), kernel2, maxThreadCount, new ConVCalc2());
                conV2.start();
            } else {
                conV2 = new ThreadPoolReflectCore(matrix, kernel, maxThreadCount, new ConVCalc2());
                conV2.start();
            }
            System.out.println(System.currentTimeMillis() - set);
            return prcCtrl.resizeImage(new IMAGE(conV2.getData()), (double) img.getWidth() / conV2.getData().length, prcCtrl.RESIZE_ENTIRETY);
        } catch (Exception e) {
            conV = new ThreadPoolConV(matrix, kernel, maxThreadCount);
            conV.start();
        }
        System.out.println(System.currentTimeMillis() - set);
        return new IMAGE(conV.getData());
    }

    public IMAGE getStrangeBlur(IMAGE img, int size) {
        double[][] kernel = calcService.getGasKernel(size);
        try {
            conV2 = new ThreadPoolReflectCore(img.getPixelMatrix(), kernel, 24, new ConVStrange());
            conV2.start();
            return new IMAGE(conV2.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getPixelMatrix(), kernel, 24);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }
}
