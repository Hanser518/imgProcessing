package Main;

import Controller.AdjustController;
import Controller.BlurController;
import Controller.ImgController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.ThreadPool.Thread.ConVCalc;
import Service.ThreadPool.Thread.ConVStrange;
import Service.ThreadPool.Thread.ConfActive;
import Service.ThreadPool.Thread.PaperBlur;
import Test.ThreadPoolReflectCore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;

import static Controller.StylizeController.calcService;

public class imgReflectTest {
    static ThreadPoolReflectCore conv;
    static ImgController imgCtrl2 = new ImgController();

    public static void main(String[] args) throws Exception {
        String fileName = "bus";
        IMAGE px = new IMAGE(fileName + ".jpg");
        IMAGE res = new IMAGE();

        long set1;

        imgCtrl2.showImg(px, "reflect");
        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(67), 24, new PaperBlur());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new IMAGE(conv.getData());
        imgCtrl2.showImg(res, "paper");

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(3), 24, new ConfActive());
        conv.customMethod("setThreshold", 32);
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new IMAGE(conv.getData());
        imgCtrl2.showImg(res, "ac");

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(30), 24, new ConVCalc());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new IMAGE(conv.getData());
        imgCtrl2.showImg(res, "conv");

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(67), 32, new ConVStrange());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new IMAGE(conv.getData());
        imgCtrl2.showImg(res, "strange");

    }
}
