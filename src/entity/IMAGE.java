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
     * 像素存储
     */
    private int[] pixelList;
    private int[] hsvList;

    private int[][] pixelMatrix;

    /**
     * 图像宽高
     */
    private int width, height;

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
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
    }

    public IMAGE(String path, int num) throws IOException {
        rawFile = ImageIO.read(new File(path));
        width = rawFile.getWidth();
        height = rawFile.getHeight();
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
    }

    public IMAGE(int[][] px) {
        width = px.length;
        height = px[0].length;
        rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rawFile.setRGB(i, j, px[i][j]);
            }
        }
        pixelMatrix = px;
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
    }

    public IMAGE(BufferedImage img) {
        rawFile = img;
        width = rawFile.getWidth();
        height = rawFile.getHeight();
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
    }

    public IMAGE() {
        width = 100;
        height = 100;
        rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rawFile.setRGB(i, j, (128 << 24) | (255 << 16) | (255 << 8) | (255));
            }
        }
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);

    }

    public IMAGE(int width, int height, int value) {
        width = width;
        height = height;
        rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rawFile.setRGB(i, j, value);
            }
        }
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);

    }

    /**
     * 获取图像数据
     *
     * @return
     */
    public BufferedImage getImg() {
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
     * 获取图像列表
     *
     * @return
     */
    public int[] getPixelList() {
        return pixelList;
    }

    public double[][][] RGB2HSV() {
        int[][] rgb = getPixelMatrix();
        return RGB2HSV(rgb);
    }

    public double[][][] RGB2HSV(int[][] rgb) {
        // 0-H, 1-S, 2-V
        double[][][] hsv = new double[width][height][3];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double r = ((rgb[i][j] >> 16) & 0xFF) / 255.0;
                double g = ((rgb[i][j] >> 8) & 0xFF) / 255.0;
                double b = ((rgb[i][j]) & 0xFF) / 255.0;
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
    }

    public int[][] HSV2RGB(double[][][] hsv) {
        int[][] rgb = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double h = hsv[i][j][0];
                double s = hsv[i][j][1];
                double v = hsv[i][j][2];
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
     * 获取图像矩阵
     *
     * @return
     */
    public int[][] getPixelMatrix() {
        if(pixelMatrix != null){
            return pixelMatrix;
        }
        int[][] matrix = new int[width][height];
        for (int i = 0; i < pixelList.length; i++) {
            int x = i % width;
            int y = i / width;
            matrix[x][y] = pixelList[i];
        }
        return matrix;
    }

    public int[][] getGrayMatrixInArgbModule() {
        int[][] m = getPixelMatrix();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] p = getArgbParams(m[i][j]);
                int value = (int) (p[1] * 0.287 + p[2] * 0.589 + p[3] * 0.114);
                result[i][j] = (254 << 24) | (value << 16) | (value << 8) | value;
            }
        }
        return result;
    }

    public int[][] getGrayMatrix(int[][] pixelMatrix) {
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] p = getArgbParams(pixelMatrix[i][j]);
                result[i][j] = (int) (p[1] * 0.287 + p[2] * 0.589 + p[3] * 0.114);
            }
        }
        return result;
    }

    public int[][] getGrayMatrix() {
        int[][] m = getPixelMatrix();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] p = getArgbParams(m[i][j]);
                result[i][j] = (int) (p[1] * 0.287 + p[2] * 0.589 + p[3] * 0.114);
            }
        }
        return result;
    }

    public int getGrayPixel(int[] p) {
        return (int) (p[1] * 0.29 + p[2] * 0.59 + p[3] * 0.12);
    }

    public int getGrayPixel(int p) {
        int[] array = new int[4];
        array[0] = (p >> 24) & 0xFF;
        array[1] = (p >> 16) & 0xFF;
        array[2] = (p >> 8) & 0xFF;
        array[3] = p & 0xFF;
        return getGrayPixel(array);
    }

    /**
     * 转换为argb数组
     *
     * @return
     */
    public int[] getArgbParams(int px) {
        int[] pixel = new int[4];
        pixel[0] = (px >> 24) & 0xFF;
        pixel[1] = (px >> 16) & 0xFF;
        pixel[2] = (px >> 8) & 0xFF;
        pixel[3] = (px) & 0xFF;
        return pixel;
    }

    /**
     * 转换为px参数
     *
     * @param argb
     * @return
     */
    public int getPixParams(int[] argb) {
        int px = (argb[0] << 24) | (argb[1] << 16) | (argb[2] << 8) | argb[3];
        return px;
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
}
