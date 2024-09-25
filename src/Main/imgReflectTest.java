package Main;

import Controller.ImgController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.ThreadPool.Thread.PaperBlur;
import Test.ThreadPoolReflectCore;

import java.io.IOException;

import static Controller.StylizeController.calcService;

public class imgReflectTest {
    static ThreadPoolReflectCore conv;
    static ImgController imgCtrl2 = new ImgController();
    public static void main(String[] args) throws Exception {
        String fileName = "7820";
        IMAGE px = new IMAGE(fileName + ".jpg");

        double kernel = 0.0;
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(67), 24, new PaperBlur());
        conv.start();

        IMAGE gas = new IMAGE(conv.getData());
        imgCtrl2.showImg(gas, "reflect");
    }
}
