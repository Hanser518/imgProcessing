package algorithm.cnn.service;

import algorithm.cnn.entity.ImageCNN;
import algorithm.cnn.entity.ImagePool;

import java.util.ArrayList;
import java.util.List;

public class ImageService {

    public static ImagePool expandImagePool(ImagePool ip, int expandsMultiplier) {
        List<ImageCNN> imgList = ip.getImageList();
        List<ImageCNN> result = new ArrayList<>();
        if(expandsMultiplier <= 1){
            return ip;
        }
        for (ImageCNN img : imgList) {
            result.addAll(RandomSplit(img, expandsMultiplier, 100, 100));
        }
        ip.setImageList(result);
        return ip;
    }

    public static ImagePool trans2TrainPool(ImagePool ip){
        List<ImageCNN> imgList = ip.getImageList();
        List<ImageCNN> result = new ArrayList<>();
        for (ImageCNN img : imgList) {
            int expandsMultiplier;
            int width = img.getWidth();
            int height = img.getHeight();
            expandsMultiplier = (width / 100) * (height / 100);
            result.addAll(RandomSplit(img, expandsMultiplier, 100, 100));
        }
        ip.setImageList(result);
        return ip;

    }

    public static List<ImageCNN> RandomSplit(ImageCNN img, int count, int w, int h) {
        if (img.getWidth() < w) {
            w = img.getWidth();
        }
        if (img.getHeight() < h) {
            h = img.getHeight();
        }
        int widthLimit = img.getWidth() - w;
        int heightLimit = img.getHeight() - h;
        List<ImageCNN> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(getSubImageCNN(img, (int) (Math.random() * widthLimit), (int) (Math.random() * heightLimit), w, h));
        }
        return result;
    }

    public static ImageCNN getSubImageCNN(ImageCNN img, int x, int y, int w, int h) {
        int[][] argbMatrix = img.getArgbMatrix();
        int[][] subMatrix = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                subMatrix[i][j] = argbMatrix[i + x][j + y];
            }
        }
        return new ImageCNN(subMatrix);
    }
}
