package algorithm.cnn.service;

import algorithm.cnn.core.ThreadPoolCenter;
import algorithm.cnn.entity.EventConv;
import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.entity.ImagePool;
import algorithm.cnn.param.DatabaseKernel;
import entity.IMAGE;
import service.imgService;
import service.impl.imgServiceImpl;
import threadPool.core.ThreadCore;
import threadPool.core.ThreadPoolReflectCore;
import threadPool.thread.ConVCalc;
import threadPool.thread.ConvMax;

import java.util.ArrayList;
import java.util.List;

public class ConvCalcService {

    public static Integer FEATURE_EASY = 0;
    public static Integer FEATURE_NORMAL = 1;
    public static Integer FEATURE_PRECISION = 2;

    public static Integer ACTIVATION_MIN = 10;
    public static Integer ACTIVATION_MAX = 11;
    public static Integer ACTIVATION_AVG = 12;
    public static Integer ACTIVATION_MEDIUM = 13;

    public static Integer POOLING_MIN = 10;
    public static Integer POOLING_MAX = 11;
    public static Integer POOLING_AVG = 12;
    public static Integer POOLING_MEDIUM = 13;

    private static final imgService imgServ = new imgServiceImpl();
    private static ThreadPoolReflectCore conv2;

    public List<ImageCNN> convCalc(ImageCNN img, Integer model) {
        List<ImageCNN> resultList = new ArrayList<>();
        model = Math.min(Math.max(model, FEATURE_EASY), FEATURE_PRECISION);
        featureCalc(img, resultList, (model + 1) * 6);
        return resultList;
    }

    private void featureCalc(ImageCNN img, List<ImageCNN> list, int count){
        try{
            List<EventConv> ecList = new ArrayList<>();
            for(int i = 0;i < count;i ++){
                EventConv ec = new EventConv();
                ImageCNN px = (ImageCNN) ImageCNN.fillImageEdge(img, DatabaseKernel.features[i].length / 2, DatabaseKernel.features[i][0].length / 2);
                System.out.println(img.getWidth() + " " + px.getWidth());
                ec.setData(px.getArgbMatrix());
                ec.setKernel(DatabaseKernel.features[i]);
                ec.setStep(1);
                ecList.add(ec);
            }
            ThreadPoolCenter TPC = new ThreadPoolCenter(ecList, 8);
            TPC.start();
            ecList = (List<EventConv>)TPC.getEventList();
            for(EventConv ec : ecList){
                list.add(new ImageCNN(ec.getResult()));
            }
        } catch (Exception e) {
            System.out.println("featureCalc ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ImageCNN activeCalc(ImageCNN img, Integer activationMode) {
        ThreadCore treadType;
        if(activationMode.equals(ACTIVATION_MAX)){
            treadType = new ConvMax();
        }else if(activationMode.equals(ACTIVATION_AVG)){
            treadType = new ConvMax();
        } else{
            treadType = new ConVCalc();
        }
        try{
            conv2 = new ThreadPoolReflectCore(img.getArgbMatrix(), DatabaseKernel.matrix[3], 24, treadType);
            conv2.start();
            return new ImageCNN(conv2.getData());
        } catch (Exception e) {
            return img;
        }
    }


    @Deprecated
    public ImagePool edgeCalc(ImagePool pool) {
        List<ImageCNN> imgList = pool.getImageList();
        List<ImageCNN> result = new ArrayList<>();
        for (ImageCNN img : imgList) {
            try {
                int[][] res = imgServ.getSobelEdge(new IMAGE(img.getArgbMatrix()));
                result.add(new ImageCNN(res));
            } catch (Exception ignored) {
            }
        }
        pool.setImageList(result);
        return pool;
    }

//    public ImageCNN poolingCalc(ImageCNN img, Integer poolingMode) {
//        ThreadCore treadType;
//        if(poolingMode.equals(POOLING_MAX)){
//            // treadType = new PoolingMax();
//        }else if(poolingMode.equals(POOLING_AVG)){
//            treadType = new ConvMax();
//        } else{
//            treadType = new ConVCalc();
//        }
//        try{
//            conv2 = new ThreadPoolReflectCore(img.getArgbMatrix(), DatabaseKernel.matrix[3], 24, treadType);
//            conv2.start();
//            return new ImageCNN(conv2.getData());
//        } catch (Exception e) {
//            return img;
//        }
//    }
}
