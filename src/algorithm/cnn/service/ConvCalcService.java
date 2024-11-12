package algorithm.cnn.service;

import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.entity.ImagePool;
import entity.IMAGE;
import service.imgService;
import service.impl.imgServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class ConvCalcService {
    static private final imgService imgServ = new imgServiceImpl();

    public ImagePool edgeCalc(ImagePool pool){
        List<ImageCNN> imgList = pool.getImageList();
        List<ImageCNN> result = new ArrayList<>();
        for(ImageCNN img : imgList){
            try {
                int[][] res = imgServ.getSobelEdge(new IMAGE(img.getArgbMatrix()));
                result.add(new ImageCNN(res));
            }catch (Exception ignored){
            }
        }
        pool.setImageList(result);
        return pool;
    }

}
