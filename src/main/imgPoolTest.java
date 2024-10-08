package main;

import controller.*;
import discard.ImgProcessingController;
import entity.IMAGE;

import java.io.IOException;

public class imgPoolTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static BlurController blurCtrl = new BlurController();
    public static void main(String[] args) throws IOException {
        String fileName = "rx78";
        IMAGE px = new IMAGE(fileName + ".jpg");
        int blurSize = 1;

        // 使用线程池
        long set0 = System.currentTimeMillis();
        IMAGE gasImage0 = blurCtrl.getGasBlur(px, blurSize, -1);
        System.out.println((System.currentTimeMillis() - set0) / 1000.0);
        imgCtrl.saveByName(gasImage0, fileName, "poolGas");

        // quickGas
        long set1 = System.currentTimeMillis();
        IMAGE gasImage1 = blurCtrl.quickGasBlur(px);
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        imgCtrl.saveByName(gasImage1, fileName, "quickGas");

        // 开启多线程
        imgCtrl.openMultiThreads();
        long set2 = System.currentTimeMillis();
        IMAGE gasImage2 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set2) / 1000.0);
        imgCtrl.saveByName(gasImage2, fileName, "multiGas");

        // 关闭多线程，精确计算
        imgCtrl.closeMultiThreads();
        imgCtrl.openAccCalc();
        long set3 = System.currentTimeMillis();
        IMAGE gasImage3 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set3) / 1000.0);
        imgCtrl.saveByName(gasImage3, fileName, "gas");
    }
}
