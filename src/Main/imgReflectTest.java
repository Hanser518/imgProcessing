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
        String fileName = "7820";
        IMAGE px = new IMAGE(fileName + ".jpg");

        // px = adCtrl.adjustSatAndVal(px, 72, 16);
        long set1;

        Class<?> clz = ThreadPoolReflectCore.class;
        Field[] fields = clz.getDeclaredFields();
        for(Field f: fields){
            System.out.println(f.getName());
        }

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(1), 24, new PaperBlur());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(1), 24, new ConfActive());
        conv.customMethod("setThreshold", 32);
        // conv.setThreshold(32);
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(1), 24, new ConVCalc());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);

        set1 = System.currentTimeMillis();
        conv = new ThreadPoolReflectCore(px.getPixelMatrix(), calcService.getGasKernel(1), 24, new ConVStrange());
        conv.start();
        System.out.println((System.currentTimeMillis() - set1) / 1000.0);

        imgCtrl2.showImg(px, "reflect");
    }
}
