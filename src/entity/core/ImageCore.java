package entity.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCore {
    public static int GRAY_SINGLE_MODE = 1;
    public static int GRAY_TREBLE_MODE = 3;

    /**
     * 图像源文件
     */
    protected BufferedImage rawFile;

    /**
     * 图像宽高
     */
    protected int width;
    protected int height;

    /**
     * 像素值矩阵
     */
    protected int[][] argbMatrix;
    private double[][][] hsvMatrix;

    /**
     * 载入图像
     *
     * @param path 文件路径
     */
    public ImageCore(String path) {
        try {
            rawFile = ImageIO.read(new File(path));
        } catch (IOException e) {
            rawFile = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        }
        width = rawFile.getWidth();
        height = rawFile.getHeight();
    }

    public ImageCore() {
    }

    /**
     * 获取图像数据
     */
    public BufferedImage getImg() {
        return rawFile;
    }

    /**
     * 获取高度
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取宽度
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取Argb图像矩阵
     */
    public int[][] getArgbMatrix() {
        if (this.argbMatrix == null) {
            this.argbMatrix = new int[width][height];
            int[] pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
            for (int i = 0; i < width * height; i++) {
                int x = i % width;
                int y = i / width;
                this.argbMatrix[x][y] = pixelList[i];
            }
        }
        return this.argbMatrix;
    }

    /**
     * 获取HSV矩阵
     */
    public double[][][] getHsvMatrix() {
        if (this.hsvMatrix == null) {
            if (this.argbMatrix != null) {
                hsvMatrix = ARGB2HSV(this.argbMatrix);
            } else {
                hsvMatrix = ARGB2HSV(getArgbMatrix());
            }
        }
        return hsvMatrix;
    }

    /**
     * <p>获取GRAY矩阵</p>
     * <P>默认转换为单值矩阵</P>
     *
     * @param model 可选参数
     */
    public int[][] getGrayMatrix(int... model) {
        if (this.argbMatrix == null) {
            getArgbMatrix();
        }
        return ARGB2GRAY(this.argbMatrix);
    }

    /**
     * 将输入的rgb/argb矩阵转换为hsv矩阵
     *
     * @param rgbMatrix rgb/argb矩阵
     * @return hsv矩阵
     */
    public static double[][][] ARGB2HSV(int[][] rgbMatrix) {
        // 0-H, 1-S, 2-V
        try {
            int width = rgbMatrix.length;
            int height = rgbMatrix[0].length;
            double[][][] hsv = new double[width][height][3];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    double r = ((rgbMatrix[i][j] >> 16) & 0xFF) / 255.0;
                    double g = ((rgbMatrix[i][j] >> 8) & 0xFF) / 255.0;
                    double b = ((rgbMatrix[i][j]) & 0xFF) / 255.0;
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
                    hsv[i][j] = new double[]{h, s, v};
                }
            }
            return hsv;
        } catch (Exception e) {
            return new double[1][1][1];
        }
    }

    /**
     * 将输入的hsv矩阵转换为argb矩阵
     *
     * @param hsvMatrix hsv矩阵
     * @return argb矩阵
     */
    public static int[][] HSV2RGB(double[][][] hsvMatrix) {
        int width = hsvMatrix.length;
        int height = hsvMatrix[0].length;
        int[][] rgb = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double h = hsvMatrix[i][j][0];
                double s = hsvMatrix[i][j][1];
                double v = hsvMatrix[i][j][2];
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
                rgb[i][j] = (255 << 24) | ((r) << 16) | ((g) << 8) | (b);
            }
        }
        return rgb;
    }

    /**
     * 将输入的argb/rgb矩阵转换为gray矩阵
     *
     * @param argbMatrix argb/rgb矩阵
     * @return gray矩阵（默认为 g | g | g 格式）
     */
    public static int[][] ARGB2GRAY(int[][] argbMatrix, int... model) {
        int width = argbMatrix.length;
        int height = argbMatrix[0].length;
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = argbMatrix[i][j] >> 16 & 0xFF;
                int g = argbMatrix[i][j] >> 8 & 0xFF;
                int b = argbMatrix[i][j] & 0xFF;
                int value = (int) (r * 0.287 + g * 0.589 + b * 0.114);
                if (model.length > 0) {
                    if (model[0] == ImageCore.GRAY_TREBLE_MODE) {
                        result[i][j] = (255 << 24) | (value << 16) | (value << 8) | value;
                    } else {
                        result[i][j] = value;
                    }
                }
            }
        }
        return result;
    }
}
