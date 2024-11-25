package entity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IMAGE {
    /**
     * 图像源文件
     */
    private BufferedImage rawFile;

    /**
     * 图像宽高
     */
    private int width;
    private int height;

    /**
     * 像素存储
     */
    private int[][] argbMatrix;
    protected double[][][] hsvMatrix;


    /**
     * 载入图像
     *
     * @param path
     * @throws IOException
     */
    public IMAGE(String path) throws IOException {
        rawFile = ImageIO.read(new File("./photo/" + path));
        width = rawFile.getWidth();
        height = rawFile.getHeight();
    }

    public IMAGE(String path, int num) throws IOException {
        rawFile = ImageIO.read(new File(path));
        width = rawFile.getWidth();
        height = rawFile.getHeight();
    }

    public IMAGE(int[][] argbMatrix) {
        width = argbMatrix.length;
        height = argbMatrix[0].length;
        this.argbMatrix = argbMatrix;
    }

    public IMAGE(BufferedImage img) {
        rawFile = img;
        width = rawFile.getWidth();
        height = rawFile.getHeight();
    }

    public IMAGE() {
        width = 100;
        height = 100;
        rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rawFile.setRGB(i, j, (255 << 24) | (255 << 16) | (255 << 8) | (255));
            }
        }

    }

    public IMAGE(int width, int height, int value) {
        this.width = width;
        this.height = height;
        rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rawFile.setRGB(i, j, value);
            }
        }

    }

    /**
     * 获取图像数据
     *
     * @return
     */
    public BufferedImage getImg() {
        if (rawFile == null) {
            rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    rawFile.setRGB(i, j, argbMatrix[i][j]);
                }
            }
        }
        return rawFile;
    }

    /**
     * 获取高度
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取宽度
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取图像数据列表形式
     *
     * @return
     */
    public int[] getPixelList() {
        int[] pixelList = new int[width * height];
        getArgbMatrix();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelList[i * height + j] = argbMatrix[i][j];
            }
        }
        return pixelList;
    }

    /**
     * 获取图像矩阵
     *
     * @return
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
     */
    public int[][] getGrayMatrix() {
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
     * 默认为 255 | value | value | value 格式
     *
     * @param argbMatrix argb/rgb矩阵
     * @return gray矩阵
     */
    public static int[][] ARGB2GRAY(int[][] argbMatrix) {
        int width = argbMatrix.length;
        int height = argbMatrix[0].length;
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = argbMatrix[i][j] >> 16 & 0xFF;
                int g = argbMatrix[i][j] >> 8 & 0xFF;
                int b = argbMatrix[i][j] & 0xFF;
                int value = (int) (r * 0.287 + g * 0.589 + b * 0.114);
                result[i][j] = (255 << 24) | (value << 16) | (value << 8) | value;
            }
        }
        return result;
    }

    public static int[][] GRAY2ARGB(int[][] grayMatrix) {
        int width = grayMatrix.length;
        int height = grayMatrix[0].length;
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double value = (grayMatrix[i][j] & 0xFF) / 1000.0;
                int r = (int) (value * 287);
                int g = (int) (value * 589);
                int b = (int) (value * 114);
                result[i][j] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        }
        return result;
    }

    public boolean activeTest(int px) {
        int r, g, b;
        r = (px >> 16) & 0xFF;
        g = (px >> 8) & 0xFF;
        b = px & 0xFF;
        if (r > 15 || g > 15 || b > 15) {
            return true;
        }
        return false;
    }

    public int getAcValue(int px) {
        int r = (px >> 16) & 0xFF;
        int g = (px >> 8) & 0xFF;
        int b = px & 0xFF;
        int value = 0;
        int activeThreshold = 32;
        if (r > activeThreshold && g > activeThreshold && b > activeThreshold)
            value = (int) (0.3 * r + 0.5 * g + 0.2 * b);
        else {
            if (r <= activeThreshold) {
                if (g <= activeThreshold || b <= activeThreshold)
                    value = Math.max(g, b);
                else
                    value = (int) (0.77 * g + 0.23 * b);
            } else if (g <= activeThreshold) {
                if (b <= activeThreshold)
                    value = r;
                else
                    value = (int) (0.6 * r + 0.4 * b);
            } else {
                value = (int) (0.375 * r + 0.625 * g);
            }
        }
        return value;
    }

    public int getPixParams(int[] arr) {
        return arr[0] << 24 | arr[1] << 16 | arr[2] << 8 | arr[3];
    }
}
