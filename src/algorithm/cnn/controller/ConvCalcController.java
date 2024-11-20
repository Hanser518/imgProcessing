package algorithm.cnn.controller;

import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.service.ConvCalcService;
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
        return convService.activeCalc(convService.convCalc(img, FEATURE_MODE), ACTIVATION_MODE);
    }

    public List<ImageCNN> poolingCalc(List<ImageCNN> imgList, Integer poolingCalc){
        List<ImageCNN> resultList = new ArrayList<>();
        resultList = convService.poolingCalc(imgList, poolingCalc);
        return resultList;
    }


}
