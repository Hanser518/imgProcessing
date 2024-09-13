package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;
import MultiThread.controller.ConcurrentRequestController;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.io.IOException;

public class poolTest {
    static ICalculateService calcService = new ICalculateServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        String fileName = "rx78";
        int blurSize = 100;
        IMAGE px = new IMAGE(fileName + ".jpg");
        int[][] m = px.getPixelMatrix();
        ConcurrentRequestController crc =
                new ConcurrentRequestController(m, calcService.getGasKernel(blurSize), 4);

        long set = System.currentTimeMillis();
        crc.start();
        System.out.println("ok");
        System.out.println((System.currentTimeMillis() - set) / 1000.0);

        // 开启超线程
        imgCtrl.openMultiThreads();
        long set3 = System.currentTimeMillis();
        IMAGE gasImage3 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set3) / 1000.0);
    }
}
