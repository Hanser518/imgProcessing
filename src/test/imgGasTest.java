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
        String fileName = "Red";
        IMAGE px = new IMAGE(fileName + ".jpg");
        IMAGE gas = blurCtrl.getGasBlur(px, 1, 32);
        imgCtrl2.showImg(gas, "gas");


        // 开启超线程
        imgCtrl.openMultiThreads();
        long set3 = System.currentTimeMillis();
        IMAGE gasImage3 = imgCtrl.getGasImage(px, 36);
        System.out.println((System.currentTimeMillis() - set3) / 1000.0);
        imgCtrl.save(gasImage3, "gasImage_3");
    }
}
