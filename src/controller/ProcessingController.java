package controller;

import entity.IMAGE;
import service.IAdjustService;
import service.impl.AdjustServiceImpl;

import java.util.List;

public class ProcessingController {
    // 缩放类型预设
    public final Integer RESIZE_ENTIRETY = 0;
    public final Integer RESIZE_LANDSCAPE = 1;
    public final Integer RESIZE_VERTICAL = 2;

    private static final IAdjustService adService = new AdjustServiceImpl();

    /**
     * 改变图像大小
     */
    public IMAGE resizeImage(IMAGE img, double radio, int type) {
        if (type == 0) {
            return adService.getReizedImage(img, (int) (img.getWidth() * radio), (int) (img.getHeight() * radio));
        } else if (type == 1) {
            return adService.getReizedImage(img, (int) (img.getWidth() * radio), img.getHeight());
        } else if (type == 2) {
            return adService.getReizedImage(img, img.getWidth(), (int) (img.getHeight() * radio));
        }
        return img;
    }

    public IMAGE combineImageList(List<IMAGE> imgList) {
        if (imgList.isEmpty()) {
            return null;
        }
        IMAGE backGround = imgList.get(0);
        int width = backGround.getWidth();
        int height = backGround.getHeight();
        int[][] result = new int[width][height];
        int step = width / imgList.size();
        System.out.println(step);
        for (int i = 0; i < imgList.size(); i++) {
            IMAGE proc = imgList.get(i);
            int procWidth = proc.getWidth();
            int procHeight = proc.getHeight();
            if (procHeight != height || procWidth != width) {
                double radio = 0;
                radio = Math.max((double) height / procHeight, (double) width / procWidth);
                proc = resizeImage(proc, radio, RESIZE_ENTIRETY);
            }
            int[][] procMatrix = proc.getPixelMatrix();
            for(int w = i * step;w < (i + 1) * step;w ++){
                for(int h = 0;h < height;h ++){
                    result[w][h] = procMatrix[w][h];
                }
            }
        }
        return new IMAGE(result);
    }
}
