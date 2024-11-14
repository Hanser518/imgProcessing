package algorithm.cnn.controller;

import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.service.ConvCalcService;
import service.imgService;
import service.impl.imgServiceImpl;
import threadPool.core.ThreadPoolReflectCore;

import java.util.ArrayList;
import java.util.List;

public class ConvCalcController {

    public static Integer FEATURE_EASY = 0;
    public static Integer FEATURE_NORMAL = 1;
    public static Integer FEATURE_PRECISION = 2;

    public static Integer ACTIVATION_MIN = 10;
    public static Integer ACTIVATION_MAX = 11;
    public static Integer ACTIVATION_AVG = 12;
    public static Integer ACTIVATION_MEDIUM = 13;

    private static ThreadPoolReflectCore conv2;
    private static final ConvCalcService convService = new ConvCalcService();

    public List<ImageCNN> convCalc(List<ImageCNN> imgList, Integer FEATURE_MODE, Integer ACTIVATION_MODE) {
        List<ImageCNN> resultList = new ArrayList<>();
        for(ImageCNN img : imgList){
            resultList.addAll(convCalc(img, FEATURE_MODE, ACTIVATION_MODE));
        }
        return resultList;
    }

    public List<ImageCNN> convCalc(ImageCNN img, Integer FEATURE_MODE, Integer ACTIVATION_MODE) {
        List<ImageCNN> resultList = new ArrayList<>();
        for(ImageCNN featureImg : convService.convCalc(img, FEATURE_MODE)){
            resultList.add(convService.activeCalc(featureImg, ACTIVATION_MODE));
        }
        return resultList;
    }

//    public List<ImageCNN> poolingCalc(List<ImageCNN> imgList, Integer poolingCalc){
//        List<ImageCNN> resultList = new ArrayList<>();
//        for(ImageCNN img : imgList){
//            resultList.add(poolingCalc(img, poolingCalc));
//        }
//        return resultList;
//    }

//    public ImageCNN poolingCalc(ImageCNN img, Integer POOLING_MODE){
//        return convService.poolingCalc(img, POOLING_MODE);
//    }


}
