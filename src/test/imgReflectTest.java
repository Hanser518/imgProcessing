package test;

import controller.ImgController;
import entity.Image;
import threadPool.thread.ConVCalc;
import threadPool.thread.ConVStrange;
import threadPool.thread.ConfActive;
import threadPool.thread.PaperBlur;
import threadPool.core.ThreadPoolReflectCore;

import static controller.StylizeController.calcService;

public class imgReflectTest {
    static ThreadPoolReflectCore conv;
    static ImgController imgCtrl2 = new ImgController();

    public static void main(String[] args) throws Exception {
        String fileName = "bus";
        Image px = new Image(fileName + ".jpg");
        Image res = new Image();

        long set1;

        imgCtrl2.showImg(px, "reflect");
        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getArgbMatrix(), calcService.getGasKernel(67), 24, new PaperBlur());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new Image(conv.getData());
        imgCtrl2.showImg(res, "paper");

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getArgbMatrix(), calcService.getGasKernel(3), 24, new ConfActive());
        conv.customMethod("setThreshold", 32);
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new Image(conv.getData());
        imgCtrl2.showImg(res, "ac");

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getArgbMatrix(), calcService.getGasKernel(30), 24, new ConVCalc());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new Image(conv.getData());
        imgCtrl2.showImg(res, "conv");

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getArgbMatrix(), calcService.getGasKernel(67), 32, new ConVStrange());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);
        res = new Image(conv.getData());
        imgCtrl2.showImg(res, "strange");

    }
}
