package algorithm.cnn.entity;

import java.awt.image.BufferedImage;

public class ImageCNN extends Image2 {
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
