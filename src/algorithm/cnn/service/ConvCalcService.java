package algorithm.cnn.service;

import algorithm.cnn.core.ThreadPoolCenter;
import algorithm.cnn.entity.*;
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

    public static Integer count1445687 = 0;

    private static final imgService imgServ = new imgServiceImpl();
    private static ThreadPoolReflectCore conv2;

    public List<ImageCNN> convCalc(ImageCNN img, Integer model) {
        List<ImageCNN> resultList = new ArrayList<>();
        model = Math.min(Math.max(model, FEATURE_EASY), FEATURE_PRECISION);
        featureCalc(img, resultList, (model + 1) * 6);
        return resultList;
    }

    private void featureCalc(ImageCNN img, List<ImageCNN> list, int count) {
        try {
            List<EventConV> ecList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                EventConV ec = new EventConV();
                ImageCNN px = (ImageCNN) ImageCNN.fillImageEdge(img, DatabaseKernel.features[i].length / 2, DatabaseKernel.features[i][0].length / 2);
                ec.setData(px.getGrayMatrix());
                ec.setKernel(DatabaseKernel.features[i]);
                ec.setStep(1);
                ecList.add(ec);
            }
            ThreadPoolCenter TPC = new ThreadPoolCenter(ecList, 8);
            TPC.start();
            ecList = (List<EventConV>) TPC.getEventList();
            for (EventConV ec : ecList) {
                list.add(new ImageCNN(ec.getResult()));
            }
        } catch (Exception e) {
            System.out.println("featureCalc ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ImageCNN activeCalc(ImageCNN img, Integer activationMode) {
        ThreadCore treadType;
        if (activationMode.equals(ACTIVATION_MAX)) {
            treadType = new ConvMax();
        } else if (activationMode.equals(ACTIVATION_AVG)) {
            treadType = new ConvMax();
        } else {
            treadType = new ConVCalc();
        }
        try {
            conv2 = new ThreadPoolReflectCore(img.getArgbMatrix(), DatabaseKernel.matrix[3], 24, treadType);
            conv2.start();
            System.out.println(++count1445687);
            return new ImageCNN(conv2.getData());
        } catch (Exception e) {
            return img;
        }
    }

    public List<ImageCNN> activeCalc(List<ImageCNN> imgList, Integer activationMode) {
        List<ImageCNN> result = new ArrayList<>();
        try {
//            List<EventMax> ecList = new ArrayList<>();
//            for (int i = 0; i < imgList.size(); i++) {
//                EventMax ec = new EventMax();
//                ImageCNN px = (ImageCNN) ImageCNN.fillImageEdge(imgList.get(i), DatabaseKernel.matrix[3].length / 2, DatabaseKernel.matrix[3].length / 2);
//                ec.setData(px.getArgbMatrix());
//                ec.setKernel(DatabaseKernel.matrix[3]);
//                ec.setStep(1);
//                ecList.add(ec);
//            }
//            ThreadPoolCenter TPC = new ThreadPoolCenter(ecList, 8);
//            TPC.start();
//            ecList = (List<EventMax>) TPC.getEventList();
            ThreadPoolCenter TPC2 = new ThreadPoolCenter(imgList, DatabaseKernel.matrix[3], EventMax.class, 8, true);
            TPC2.start();
            List<EventMax> ecList = (List<EventMax>) TPC2.getEventList();
            for (EventMax ec : ecList) {
                result.add(new ImageCNN(ec.getResult()));
            }
        } catch (Exception e) {
            return imgList;
        }
        return result;
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

    public List<ImageCNN> poolingCalc(List<ImageCNN> imgList, Integer poolingMode) {
        List<ImageCNN> result = new ArrayList<>();
        try {
            List<EventPooling> ecList = new ArrayList<>();
            for (int i = 0; i < imgList.size(); i++) {
                EventPooling ec = new EventPooling();
                ec.setData(imgList.get(i).getArgbMatrix());
                ec.setKernel(DatabaseKernel.matrix[2]);
                ec.setStep(DatabaseKernel.matrix[2].length);
                ecList.add(ec);
            }
            ThreadPoolCenter TPC = new ThreadPoolCenter(ecList, 8);
            TPC.start();
            ecList = (List<EventPooling>) TPC.getEventList();
            for (EventPooling ec : ecList) {
                result.add(new ImageCNN(ec.getResult()));
            }
        } catch (Exception e) {
            return imgList;
        }
        return result;
    }
}
