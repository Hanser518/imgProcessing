package algorithm.cnn;

import algorithm.cnn.entity.ImagePool;
import algorithm.cnn.service.ConvCalcService;
import algorithm.cnn.service.ImageService;

import java.util.Scanner;

public class Main {
    private static ConvCalcService convCalc = new ConvCalcService();
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ImagePool ip1 = new ImagePool("output/7820");
        System.out.println("7820: " + ip1.getPoolSize());
        ImageService.trans2TrainPool(ip1);
        System.out.println(ip1.getPoolSize());

        convCalc.edgeCalc(ip1);
        System.out.println(ip1.getPoolSize());


        ImagePool ip2 = new ImagePool("output/Red");
        System.out.println("Red: " + ip2.getPoolSize());
        ImageService.trans2TrainPool(ip2);
        System.out.println(ip2.getPoolSize());

        convCalc.edgeCalc(ip2);
        System.out.println(ip2.getPoolSize());
    }
}
