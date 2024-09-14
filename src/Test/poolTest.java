package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;
import Controller.StylizeController;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class poolTest {
    static ICalculateService calcService = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static StylizeController styleCtrl = new StylizeController();
    public static void main(String[] args) throws IOException {
        String fileName = "index1";
        IMAGE px = new IMAGE(fileName + ".png");
        int blurSize = 67;

        // 使用线程池
        long set = System.currentTimeMillis();
        IMAGE gasImage1 = styleCtrl.transGrilleStyle(px, styleCtrl.GRILLE_REGULAR);
        System.out.println((System.currentTimeMillis() - set) / 1000.0);
        imgCtrl.saveByName(gasImage1, "gas1");

        // 开启多线程
//        imgCtrl.openMultiThreads();
//        long set3 = System.currentTimeMillis();
//        IMAGE gasImage2 = imgCtrl.getGasImage(px, blurSize);
//        System.out.println((System.currentTimeMillis() - set3) / 1000.0);
//        imgCtrl.saveByName(gasImage2, "gas2");
//
//        // 关闭多线程，精确计算
//        imgCtrl.closeMultiThreads();
//        imgCtrl.openAccCalc();
//        long set1 = System.currentTimeMillis();
//        IMAGE gasImage3 = imgCtrl.getGasImage(px, blurSize);
//        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
//        imgCtrl.saveByName(gasImage3, "gas3");
    }
}
