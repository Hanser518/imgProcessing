package controller;

import algorithm.wpfo.main.WPFO;
import entity.IMAGE;
import threadPool.pool.ThreadPoolConV;
import threadPool.core.ThreadPoolCore;
import threadPool.core.ThreadPoolReflectCore;
import threadPool.thread.ConVCalc;
import threadPool.thread.ConVCalc2;
import threadPool.thread.ConVCalc3;
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
        conV = new ThreadPoolConV(img.getArgbMatrix(), calcService.getGasKernel(1), 32);
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
            conV2 = new ThreadPoolReflectCore(img.getArgbMatrix(), kernel, maxThreadCount, new ConVCalc());
            conV2.start();
            return new IMAGE(conV2.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getArgbMatrix(), kernel, maxThreadCount);
            conV.start();
        }
        return new IMAGE(conV.getData());
    }

    public IMAGE getQuickGasBlur(IMAGE img, int size, int maxThreadCount) {
        long set = System.currentTimeMillis();
        // 获取原始卷积核
        double[][] kernel = calcService.getGasKernel(size);
        // 计算步长和计算矩阵
        int step = Math.min((int) (1 + Math.sqrt(size) / 4), 5);
        int[][] matrix = img.getWidth() * img.getHeight() * step < 1e7 ?
                img.getArgbMatrix() : imgServ.getThumbnail(img, step);
        try {
            if (size > 5) {
                recursionImgCalc(step, matrix, size, 0);
            } else {
                conV2 = new ThreadPoolReflectCore(matrix, kernel, maxThreadCount, new ConVCalc());
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

    private void recursionImgCalc(int step, int[][] matrix, int size, int count) throws Exception {
        int kernelSize = recursionGetSize(step, count, size);
        double[][] kernel = calcService.getGasKernel(kernelSize);
        conV2 = new ThreadPoolReflectCore(matrix, kernel, 32, new ConVCalc2());
        conV2.start();
        if (step - 1 > count) {
            recursionImgCalc(step, conV2.getData(), size, ++count);
        }
    }

    private int recursionGetSize(int step, int count, int size) {
        if (step < 2) {
            return size;
        } else if(size < 150){
            double sum = Math.pow(2, step) - 1;
            double sub = 1 / sum;
            return (int) (sub * (step - count) * size);
        } else {
            double sum = Math.pow(2, step) - 1;
            double sub = 1 / sum;
            return (int) (sub * (step - count + 1) * size);
        }
    }

    @Deprecated
    public IMAGE getFixQuickGasBlur(IMAGE img, int size) {
        long set = System.currentTimeMillis();
        double[][] kernel = calcService.getGasKernel(size);
        try {
            if (size > 5) {
                double[][] kernel1 = calcService.getGasKernel((int) (size * 0.66));
                int[][] fixMatrix = WPFO.getData(img);
                conV2 = new ThreadPoolReflectCore(img.getArgbMatrix(), kernel1, 16, new ConVCalc3());
                conV2.setFocusData(fixMatrix);
                conV2.start();
                double[][] kernel2 = calcService.getGasKernel(size - (int) (size * 0.66));
                conV2 = new ThreadPoolReflectCore(conV2.getData(), kernel2, 16, new ConVCalc3());
                conV2.setFocusData(fixMatrix);
                conV2.start();
            } else {
                conV2 = new ThreadPoolReflectCore(img.getArgbMatrix(), kernel, 16, new ConVCalc());
                conV2.start();
            }
            System.out.println(System.currentTimeMillis() - set);
            return prcCtrl.resizeImage(new IMAGE(conV2.getData()), (double) img.getWidth() / conV2.getData().length, prcCtrl.RESIZE_ENTIRETY);
            // return new IMAGE(conV2.getData());
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getArgbMatrix(), kernel, 16);
            conV.start();
        }

        return null;
    }

    public IMAGE getStrangeBlur(IMAGE img, int size) {
        long set = System.currentTimeMillis();
        double[][] kernel = calcService.getGasKernel(size);
        int[][] matrix = imgServ.getThumbnail(img, Math.min((int) (1 + Math.sqrt(size)), 5));
        try {
            conV2 = new ThreadPoolReflectCore(matrix, kernel, 24, new ConVStrange());
            conV2.start();
            System.out.println(System.currentTimeMillis() - set);
            return prcCtrl.resizeImage(new IMAGE(conV2.getData()), (double) img.getWidth() / conV2.getData().length, prcCtrl.RESIZE_ENTIRETY);
        } catch (Exception e) {
            conV = new ThreadPoolConV(img.getArgbMatrix(), kernel, 24);
            conV.start();
        }
        System.out.println(System.currentTimeMillis() - set);
        return new IMAGE(conV.getData());
    }
}
