package algorithm.cnn.entity;

import entity.core.ImageCore;

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
        super();
        this.width = argbMatrix.length;
        this.height = argbMatrix[0].length;
        this.argbMatrix = argbMatrix;
    }
}
