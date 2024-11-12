package algorithm.cnn.service;

import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.entity.ImagePool;
import entity.IMAGE;
import service.imgService;
import service.impl.imgServiceImpl;
import threadPool.core.ThreadPoolReflectCore;

import java.util.ArrayList;
import java.util.List;

public class ConvCalcService {
    public static enum FEATURE {EASY, NORMAL, PRECISION}

    private static final imgService imgServ = new imgServiceImpl();
    private static ThreadPoolReflectCore conv2;

    public ImagePool convCalc(ImagePool pool, FEATURE... model){
        FEATURE processModel;
        if(model.length < 1){
            processModel = FEATURE.NORMAL;
        }else{
            processModel = model[0];
        }
        return null;
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
}
