package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;
import Service.ICalculateService;
import Service.IPictureService;
import Service.Impl.ICalculateServiceImpl;
import Service.Impl.IPictureServiceImpl;

import java.io.IOException;

public class gasMap {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static IPictureService picService = new IPictureServiceImpl();
    static ImgProcessingController imgCtrl = new ImgProcessingController();

    public static void main(String[] args) throws IOException {
        IMAGE px = new IMAGE("rx78.jpg");
//        int[][] map = calculateServer.getGasMap(px, 30, 80);
//        for(int i = 0;i < map.length;i ++){
//            for(int j = 0;j < map[i].length; j++){
//                System.out.printf("%2d ", map[i][j]);
//            }
//            System.out.printf("\n");
//        }

        int w = px.getWidth();
        int h = px.getHeight();
//        IMAGE gray = picService.getCalcGray(px);
//        calculateServer.getGList(gray).forEach((key, value) -> {
//            System.out.println(key + ": " + value + ",Rate= " + (float)value / w / h);
//        });
//        calculateServer.getGList(px).forEach((key, value) -> {
//            System.out.println(key + ": " + value + ",Rate= " + (float)value / w / h);
//        });

//        double param = 255;
//        for(int i = 1;i < 256;i ++){
//            double num = Math.pow(param, 1) / Math.pow(i, 1.5) + 0.9;
//            System.out.printf("%3d : %3.2f, %3d\n", i, num, (int)(i * num));
//        }

        System.out.println(px.getPixParams(new int[]{255, 101, 101, 101}));
        System.out.println(px.getPixParams(new int[]{255, 101, 101, 188}));
        System.out.println(px.getPixParams(new int[]{255, 101, 188, 188}));
        System.out.println(px.getPixParams(new int[]{255, 188, 101, 101}));
        System.out.println(px.getPixParams(new int[]{255, 0, 0, 0}));
//        System.out.println(px.getPixParams(new int[]{255,188, 94, 94}));
//        System.out.println(px.getPixParams(new int[]{255,188, 94,188}));
//        System.out.println(px.getPixParams(new int[]{255,188,188, 94}));
//        System.out.println(px.getPixParams(new int[]{255,188,188,188}));


        IMAGE gamma = picService.getGammaFix(px, 255);
        // imgCtrl.saveByName(gamma, "gamma");

        IMAGE his;
        imgCtrl.openMultiThreads();

//        IMAGE gasImage1 = imgCtrl.getGasImage(px, 3);
//        his = new IMAGE(calculateServer.getHistogram(gasImage1));
//        imgCtrl.saveByName(his, "his1");

        IMAGE gasImage2 = imgCtrl.getGasImage(px, 3);
        his = new IMAGE(calculateServer.getHistogram(gasImage2));
        // his = new IMAGE(calculateServer.getHistogram(px));
        imgCtrl.save(his, "his2");

//        IMAGE gasImage3 = imgCtrl.getGasImage(px, 13);
//        his = new IMAGE(calculateServer.getHistogram(gasImage3));
//        imgCtrl.saveByName(his, "his3");

//        IMAGE gasImage4 = imgCtrl.getGasImage(px, 27);
//        his = new IMAGE(calculateServer.getHistogram(gasImage4));
//        imgCtrl.saveByName(his, "his4");


    }
}
