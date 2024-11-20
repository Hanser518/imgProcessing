package algorithm.cnn;

import algorithm.cnn.controller.ConvCalcController;
import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.entity.ImagePool;
import algorithm.cnn.service.ConvCalcService;
import algorithm.cnn.service.ImageService;
import controller.ImgController;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static ImgController imgCtrl2 = new ImgController();
    private static ConvCalcController convController = new ConvCalcController();
    public static void main(String[] args) {
        ImagePool ip1 = new ImagePool("output/7820");
        System.out.println("7820: " + ip1.getPoolSize());

        ImageService.trans2TrainPool(ip1, 100);
        List<ImageCNN> list = convController.convCalc(ip1.getImageList(), ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
        list = convController.poolingCalc(list, 11);
        for(int i = 0;i < 50;i ++){
            imgCtrl2.showImg(list.get((int) (list.size() * Math.random())).getRawFile(), "Index1." + i);
        }
        System.out.println(list.size());

        list = convController.convCalc(list, ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
        list = convController.poolingCalc(list, 11);
        for(int i = 0;i < 100;i ++){
            imgCtrl2.showImg(list.get((int) (list.size() * Math.random())).getRawFile(), "Index2." + i);
        }
        System.out.println(list.size());

        list = convController.convCalc(list, ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
        list = convController.poolingCalc(list, 11);
        for(int i = 0;i < 150;i ++){
            imgCtrl2.showImg(list.get((int) (list.size() * Math.random())).getRawFile(), "Index3." + i);
        }
        System.out.println(list.size());
//
//
//
//        ImagePool ip2 = new ImagePool("output/Red");
//        System.out.println("Red: " + ip2.getPoolSize());
//        ImageService.trans2TrainPool(ip2, 200);
//        List<ImageCNN> list2 = convController.convCalc(ip1.getImageList(), ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
//        list2 = convController.poolingCalc(list2, 11);
//        for(int i = 0;i < 10;i ++){
//            ImageCNN ep = list2.get(i);
//            System.out.println(i + " " + ep.getWidth() + " " + ep.getHeight());
//        }
//        System.out.println(list2.size());
//
//        list2 = convController.convCalc(list2, ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
//        list2 = convController.poolingCalc(list2, 11);
//        for(int i = 0;i < 10;i ++){
//            ImageCNN ep = list2.get(i);
//            System.out.println(i + " " + ep.getWidth() + " " + ep.getHeight());
//        }
//        System.out.println(list2.size());
//
//        list2 = convController.convCalc(list2, ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
//        list2 = convController.poolingCalc(list2, 11);
//        for(int i = 0;i < 10;i ++){
//            ImageCNN ep = list2.get(i);
//            System.out.println(i + " " + ep.getWidth() + " " + ep.getHeight());
//        }
//        System.out.println(list2.size());
    }
}
