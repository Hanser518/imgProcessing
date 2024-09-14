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
        IMAGE px = new IMAGE(fileName + ".png");
        int blurSize = 100;
        int[][] m = px.getPixelMatrix();
        // 创建并发请求，调用线程池进行计算
        ConcurrentRequestController crc = new ConcurrentRequestController(m, calcService.getGasKernel(blurSize), 32);
        long set = System.currentTimeMillis();
        crc.start();
        IMAGE gasImage1 = new IMAGE(crc.getData());
        System.out.println((System.currentTimeMillis() - set) / 1000.0);
        imgCtrl.saveByName(gasImage1, "gas1");

        // 开启超线程
        imgCtrl.openMultiThreads();
        long set3 = System.currentTimeMillis();
        IMAGE gasImage2 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set3) / 1000.0);
        imgCtrl.saveByName(gasImage2, "gas2");

        // 关闭超线程，精确计算
        imgCtrl.closeMultiThreads();
        imgCtrl.openAccCalc();
        long set1 = System.currentTimeMillis();
        IMAGE gasImage3 = imgCtrl.getGasImage(px, blurSize);
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        imgCtrl.saveByName(gasImage3, "gas3");
    }
}
