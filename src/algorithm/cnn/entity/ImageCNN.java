package algorithm.cnn.entity;

import algorithm.cnn.core.ImageCore;

import java.awt.image.BufferedImage;

public class ImageCNN extends ImageCore {
    /**
     * 载入图像
     *
     * @param path 文件路径
     */
    public ImageCNN(String path) {
        super(path);
    }

    public ImageCNN(int[][] argbMatrix){
        super(argbMatrix);
    }

    public ImageCNN(BufferedImage image){
        super(image);
    }
}
