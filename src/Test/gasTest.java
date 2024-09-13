package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class gasTest {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        IMAGE testPx = new IMAGE("rx78.jpg");
        imgCtrl.closeMultiThreads();
        imgCtrl.openAccCalc();
        int size = 50;

        // 关闭超线程，精确计算
        long set1 = System.currentTimeMillis();
        // IMAGE gasImage1 = imgCtrl.getGasImage(testPx, size);
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);

        // 关闭超线程，非精确计算
        imgCtrl.closeAccCalc();
        long set2 = System.currentTimeMillis();
        // IMAGE gasImage2 = imgCtrl.getGasImage(testPx, size);
        System.out.println((System.currentTimeMillis() - set2) / 1000.0);

        // 开启超线程
        imgCtrl.openMultiThreads();
        long set3 = System.currentTimeMillis();
        IMAGE gasImage3 = imgCtrl.getGasImage(testPx, size);
        System.out.println((System.currentTimeMillis() - set3) / 1000.0);

        // imgCtrl.saveByName(gasImage1, "gasImage_1");
        // imgCtrl.saveByName(gasImage2, "gasImage_2");
        imgCtrl.saveByName(gasImage3, "gasImage_3");
    }
}
