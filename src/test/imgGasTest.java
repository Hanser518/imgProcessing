package test;

import controller.BlurController;
import controller.ImgController;
import discard.ImgProcessingController;
import entity.IMAGE;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

import java.io.IOException;

public class imgGasTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static BlurController blurCtrl = new BlurController();

    public static void main(String[] args) throws IOException {
        String fileName = "bus";
        IMAGE px = new IMAGE(fileName + ".jpg");
        IMAGE gas = new IMAGE();

        // 开启超线程
        imgCtrl.closeMultiThreads();

        int gasSize = 100;

        long set1 = System.currentTimeMillis();
        // IMAGE gas1 = blurCtrl.getGasBlur(px, gasSize, 32);
        // imgCtrl2.showImg(gas1, "gas");
        long set2 = System.currentTimeMillis();

        IMAGE gas2 = blurCtrl.getQuickGasBlur(px, gasSize, 32);
        imgCtrl2.showImg(gas2, "quick");
        long set3 = System.currentTimeMillis();

        long set4 = System.currentTimeMillis();
        // IMAGE gasImage3 = imgCtrl.getGasImage(px, gasSize);

        // imgCtrl2.showImg(gasImage3, "gasImage3");

        System.out.println((set2 - set1) / 1000.0);
        System.out.println((set3 - set2) / 1000.0);
        System.out.println((System.currentTimeMillis() - set4) / 1000.0);
    }
}
