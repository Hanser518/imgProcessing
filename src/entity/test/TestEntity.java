package entity.test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 载入时就对数据进行复杂度分析并切分
 * 即将EventPool和IMAGE类相结合，执行多线程任务时不需要再去进行数据切分
 */
public class TestEntity implements Runnable {

    /**
     * 图像源文件
     */
    private final BufferedImage bufferFile;

    /**
     * 像素存储
     */
    private int[][] argbMatrix;
    private int[][] hsvMatrix;

    /**
     * 图像宽高
     */
    private final int width;
    private final int height;

    /**
     * 图像块列表
     */
    private List<imageBlock> imageBlockList = new ArrayList<>();

    /**
     * 文件初始化情况
     */
    private volatile boolean initialized = false;


    public TestEntity(String path) throws IOException {
        bufferFile = ImageIO.read(new File(path));
        width = bufferFile.getWidth();
        height = bufferFile.getHeight();
        int[] argbList = bufferFile.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < argbList.length; i++) {
            int x = i % width;
            int y = i / width;
            argbMatrix[x][y] = argbList[i];
        }
        this.run();
    }


    public TestEntity(int[][] argbMatrix) {
        width = argbMatrix.length;
        height = argbMatrix[0].length;
        bufferFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferFile.setRGB(i, j, argbMatrix[i][j]);
            }
        }
        this.argbMatrix = argbMatrix;
        this.run();
    }

    /**
     * 将输入的argb矩阵转换为hsv矩阵
     * <p> 兼容rgb矩阵
     *
     * @param argbMatrix argb/rgb矩阵
     * @return hsv矩阵
     */
    public static int[][] transARGB2HSV(int[][] argbMatrix) {
        // 0-H, 1-S, 2-V
        int width = argbMatrix.length;
        int height = argbMatrix[0].length;
        int[][] hsvMatrix = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double r = ((argbMatrix[i][j] >> 16) & 0xFF) / 255.0;
                double g = ((argbMatrix[i][j] >> 8) & 0xFF) / 255.0;
                double b = ((argbMatrix[i][j]) & 0xFF) / 255.0;
                double min = Math.min(r, Math.min(g, b));
                double v = Math.max(r, Math.max(g, b));

                double delta = v - min;
                double s = delta / (Math.abs(v) + 2.2204460492503131e-16);
                delta = 60.0 / (delta + 2.2204460492503131e-16);

                double h;
                if (v == r) {
                    h = (g - b) * delta;
                } else if (v == g) {
                    h = (b - r) * delta + 120;
                } else {
                    h = (r - g) * delta + 240;
                }
                h = h < 0 ? h + 360 : h;
                int ih = (int) (h / 2);
                int is = (int) (s * 255);
                int iv = (int) (v * 255);
                hsvMatrix[i][j] = (ih << 16) | (is << 8) | iv;
            }
        }
        return hsvMatrix;
    }

    /**
     * 将输入的hsv矩阵转换为argb矩阵
     *
     * @param hsvMatrix hsv矩阵
     * @return argb矩阵
     */
    public static int[][] transHSV2ARGB(int[][] hsvMatrix) {
        int width = hsvMatrix.length;
        int height = hsvMatrix[0].length;
        int[][] argbMatrix = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double h = (hsvMatrix[i][j] >> 16) & 0xFF * 2;
                double s = ((hsvMatrix[i][j] >> 8) & 0xFF) / 255.0;
                double v = (hsvMatrix[i][j] & 0xFF) / 255.0;
                int h1 = (int) Math.floor(h / 60);
                double f = h / 60 - h1;
                double p = v * (1 - s);
                double q = v * (1 - f * s);
                double t = v * (1 - (1 - f) * s);
                int r = 0, g = 0, b = 0;
                switch (h1) {
                    case 0:
                        r = (int) (v * 255);
                        g = (int) (t * 255);
                        b = (int) (p * 255);
                        break;
                    case 1:
                        r = (int) (q * 255);
                        g = (int) (v * 255);
                        b = (int) (p * 255);
                        break;
                    case 2:
                        r = (int) (p * 255);
                        g = (int) (v * 255);
                        b = (int) (t * 255);
                        break;
                    case 3:
                        r = (int) (p * 255);
                        g = (int) (q * 255);
                        b = (int) (v * 255);
                        break;
                    case 4:
                        r = (int) (t * 255);
                        g = (int) (p * 255);
                        b = (int) (v * 255);
                        break;
                    case 5:
                        r = (int) (v * 255);
                        g = (int) (p * 255);
                        b = (int) (q * 255);
                        break;
                }
                argbMatrix[i][j] = (255 << 24) | ((r) << 16) | ((g) << 8) | (b);
            }
        }
        return argbMatrix;
    }

    /**
     * 文件图像载入后，会优先满足变量bufferFile和argbMatrix的生成
     * 其余的数据将会在额外线程中创建
     */
    @Override
    public void run() {
        this.hsvMatrix = transARGB2HSV(argbMatrix);
        // 初始化文件块
        int initBlockSize = 100;
        imageBlock[][] blockMatrix = new imageBlock[width / initBlockSize + 1][height / initBlockSize + 1];
        for (int i = 0; i < blockMatrix.length; i++) {
            for (int j = 0; j < blockMatrix[0].length; j++) {
                int blockWidth = i * initBlockSize < width ? initBlockSize : width - (i - 1) * initBlockSize;
                int blockHeight = j * initBlockSize < height ? initBlockSize : height - (j - 1) * initBlockSize;
                if (blockHeight > 0 && blockWidth > 0) {
                    blockMatrix[i][j] = new imageBlock(argbMatrix, i * initBlockSize, j * initBlockSize, blockWidth, blockHeight);
                }
            }
        }
        System.out.println("ok");
        initialized = true;
    }

    public BufferedImage getImg() {
        return this.bufferFile;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int[][] getHSVMatrix() {
        while (!initialized) {
            Thread.onSpinWait();
        }
        return this.hsvMatrix;
    }

    public int[][] getArgbMatrix() {
        return this.argbMatrix;
    }

    public List<imageBlock> getImageBlockList() {
        return imageBlockList;
    }
}
