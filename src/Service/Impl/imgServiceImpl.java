package Service.Impl;

import Entity.IMAGE;
import Service.Extends.ThreadPoolActive;
import Service.Extends.ThreadPoolConV;
import Service.ThreadPoolService;
import Service.imgService;

public class imgServiceImpl implements imgService {
    static private ThreadPoolService conv;

    @Override
    public int[][] getSobelEdge(IMAGE px) {
        double[][] kernelX = new double[][]{{ 1, 2, 1}, { 0, 0, 0}, {-1,-2,-1},};
        double[][] kernelY = new double[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1},};
        // 获取x方向的sobel
        conv = new ThreadPoolConV(px.getGrayMatrix4(), kernelX, 16);
        conv.start();
        int[][] sobelX = conv.getData();
        // 获取y方向的sobel
        conv = new ThreadPoolConV(px.getGrayMatrix4(), kernelY, 16);
        conv.start();
        int[][] sobelY = conv.getData();
        // 合并计算结果
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = new int[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int value1 = px.getArgbParams(sobelX[i][j])[1];
                int value2 = px.getArgbParams(sobelY[i][j])[1];
                int value = (int) Math.sqrt(Math.pow(value1, 2) + Math.pow(value2, 2));
                value = value > 10 ? value : 0;
                result[i][j] = px.getPixParams(new int[]{255, value, value, value});
            }
        }
        return result;
    }

    @Override
    public int[][] dilateImg(IMAGE px) {
        double[][] kernel = new double[7][7];
        conv = new ThreadPoolActive(px.getPixelMatrix(), kernel, 3, 24);
        conv.start();
        int[][] ac = conv.getData();
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = new int[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int value = ac[i][j] * 255;
                result[i][j] = px.getPixParams(new int[]{255, value, value, value});
            }
        }
        return result;
    }
}
