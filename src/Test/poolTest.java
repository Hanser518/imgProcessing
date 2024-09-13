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
        String fileName = "index1";
        int blurSize = 1;
        IMAGE px = new IMAGE(fileName + ".png");
        int[][] m = px.getPixelMatrix();
        ConcurrentRequestController crc = new ConcurrentRequestController(m, calcService.getGasKernel(blurSize), 4);

        long set = System.currentTimeMillis();
        crc.start();
        System.out.println("ok");
        System.out.println((System.currentTimeMillis() - set) / 1000.0);

        // 开启超线程
        imgCtrl.openMultiThreads();
        long set3 = System.currentTimeMillis();
        IMAGE gasImage3 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set3) / 1000.0);

        // 关闭超线程，精确计算
        imgCtrl.closeMultiThreads();
        imgCtrl.openAccCalc();
        long set1 = System.currentTimeMillis();
        IMAGE gasImage1 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
    }
}
