package Entity;

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

    public IMAGE(int[][] px) {
        width = px.length;
        height = px[0].length;
        rawFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rawFile.setRGB(i, j, px[i][j]);
            }
        }
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
    }

    public IMAGE(BufferedImage img){
        rawFile = img;
        width = rawFile.getWidth();
        height = rawFile.getHeight();
        pixelList = rawFile.getRGB(0, 0, width, height, null, 0, width);
    }

    public IMAGE(){

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

    /**
     * 获取图像矩阵
     *
     * @return
     */
    public int[][] getPixelMatrix() {
        int[][] matrix = new int[width][height];
        for (int i = 0; i < pixelList.length; i++) {
            int x = i % width;
            int y = i / width;
            matrix[x][y] = pixelList[i];
        }
        return matrix;
    }

    public int[][] getGrayMatrix(int[][] pixelMatrix) {
        int[][] result = new int[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int[] p = getArgbParams(pixelMatrix[i][j]);
                result[i][j] = (int)(p[1] * 0.287 + p[2] * 0.589 + p[3] * 0.114);
            }
        }
        return result;
    }

    public int[][] getGrayMatrix(){
        int[][] m = getPixelMatrix();
        int[][] result = new int[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int[] p = getArgbParams(m[i][j]);
                result[i][j] = (int)(p[1] * 0.287 + p[2] * 0.589 + p[3] * 0.114);
            }
        }
        return result;
    }

    public int getGrayPixel(int[] p){
        return (int)(p[1] * 0.287 + p[2] * 0.589 + p[3] * 0.114);
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
}
