package Service.Impl;

import Entity.IMAGE;
import Service.ThreadPool.ThreadPoolConV;
import Service.ThreadPool.CORE.ThreadPoolCore;
import Service.imgService;

public class imgServiceImpl implements imgService {
    static private ThreadPoolCore conv;

    private int[][] doubleKernelCalc(IMAGE px, double[][] kernel1, double[][] kernel2) {
        // 获取x方向的sobel
        conv = new ThreadPoolConV(px.getGrayMatrix4(), kernel1, 16);
        conv.start();
        int[][] sobelX = conv.getData();
        // 获取y方向的sobel
        conv = new ThreadPoolConV(px.getGrayMatrix4(), kernel2, 16);
        conv.start();
        int[][] sobelY = conv.getData();
        // 合并计算结果
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value1 = px.getArgbParams(sobelX[i][j])[1];
                int value2 = px.getArgbParams(sobelY[i][j])[1];
                int value = (int) Math.sqrt(Math.pow(value1, 2) + Math.pow(value2, 2));
                value = value > 16 ? value : 0;
                result[i][j] = px.getPixParams(new int[]{255, value, value, value});
            }
        }
        return result;
    }

    @Override
    public int[][] getSobelEdge(IMAGE px) {
        double[][] kernelX = new double[][]{{1, 2, 1}, {0, 0, 0}, {-1, -2, -1},};
        double[][] kernelY = new double[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1},};
        return doubleKernelCalc(px, kernelX, kernelY);
    }

    @Override
    public int[][] getPrewittEdge(IMAGE px) {
        double[][] kernelX = new double[][]{{1, 1, 1}, {0, 0, 0}, {-1, -1, -1},};
        double[][] kernelY = new double[][]{{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1},};
        return doubleKernelCalc(px, kernelX, kernelY);
    }

    @Override
    public int[][] getMarrEdge(IMAGE px) {
        double[][] kernel = new double[][]{{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1},};
        conv = new ThreadPoolConV(px.getGrayMatrix4(), kernel, 16);
        conv.start();
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] data = conv.getData();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = px.getArgbParams(data[i][j])[1];
                value = value > 16 ? value * 8 : 0;
                result[i][j] = px.getPixParams(new int[]{255, value, value, value});
            }
        }
        return result;
    }


    @Override
    public int[][] dilateImg(IMAGE px, int radius) {
        double[][] kernel = new double[radius * 2 + 1][radius * 2 + 1];
//        conv = new ThreadPoolActive(px.getPixelMatrix(), kernel, radius, 24);
//        conv.start();
//        int[][] ac = conv.getData();
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = px.getPixelMatrix();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double act = 0;
                for (int k = -radius; k < radius * 2 + 1; k++) {
                    for (int l = -radius; l < radius * 2 + 1; l++) {
                        try {
                            int r = (result[i + k][j + l] >> 16) & 0xFF;
                            if (r == 187)
                                act += 0.67;
                            else if (r >= 32)
                                act++;
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (act > Math.pow(radius * 2 + 1, 2) / 3.0)
                    result[i][j] = px.getPixParams(new int[]{255, 187, 187, 187});
//                int value = ac[i][j] * 255;
//                result[i][j] = px.getPixParams(new int[]{255, value, value, value});
            }
        }
        return result;
    }

    public int[][] erosionImg(IMAGE px, int radius) {
        double[][] kernel = new double[radius * 2 + 1][radius * 2 + 1];
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = px.getPixelMatrix();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double act = 0;
                for (int k = -radius; k < radius * 2 + 1; k++) {
                    for (int l = -radius; l < radius * 2 + 1; l++) {
                        try {
                            int r = (result[i + k][j + l] >> 16) & 0xFF;
                            if (r >= 32)
                                act++;
                        } catch (Exception ignored) {
                        }
                    }
                }
//                if (act > Math.pow(radius * 2 + 1, 2) * 0.85)
//                    result[i][j] = px.getPixParams(new int[]{255, 187, 187, 187});
//                else
//                    result[i][j] = px.getPixParams(new int[]{255, 0, 0, 0});
                if (act <= Math.pow(radius * 2 + 1, 2) * 0.85)
                    result[i][j] = px.getPixParams(new int[]{255, 0, 0, 0});
            }
        }
        return result;
    }
}
