package algorithm.cnn;

import algorithm.cnn.controller.ConvCalcController;
import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.entity.ImagePool;
import algorithm.cnn.service.ConvCalcService;
import algorithm.cnn.service.ImageService;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static ConvCalcService convCalc = new ConvCalcService();
    private static ConvCalcController convController = new ConvCalcController();
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ImagePool ip1 = new ImagePool("output/7820");
        System.out.println("7820: " + ip1.getPoolSize());

        ImageService.trans2TrainPool(ip1);
        List<ImageCNN> list = convController.convCalc(ip1.getImageList(), ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
//        list = convController.poolingCalc(list, 11);

//        list = convController.convCalc(list, ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
//        list = convController.poolingCalc(list, 11);
//
//        list = convController.convCalc(list, ConvCalcController.FEATURE_EASY, ConvCalcController.ACTIVATION_MAX);
//        list = convController.poolingCalc(list, 11);

        System.out.println(list.size());


//        ImagePool ip2 = new ImagePool("output/Red");
//        System.out.println("Red: " + ip2.getPoolSize());
//        ImageService.trans2TrainPool(ip2);
//        System.out.println(ip2.getPoolSize());
//
//        convCalc.edgeCalc(ip2);
//        System.out.println(ip2.getPoolSize());
    }
}
