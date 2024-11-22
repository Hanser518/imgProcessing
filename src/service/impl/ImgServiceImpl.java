package service.impl;

import algorithm.edgeTrace.entity.Node;
import algorithm.edgeTrace.entity.Point;
import algorithm.edgeTrace.main.EdgeTrace;
import controller.EdgeController;
import entity.IMAGE;
import threadPool.thread.ConVCalc;
import threadPool.core.ThreadPoolCore;
import service.ImgService;
import threadPool.core.ThreadPoolReflectCore;

public class ImgServiceImpl implements ImgService {
    static private ThreadPoolCore conv;
    static private ThreadPoolReflectCore conv2;
    static EdgeController edgeCtrl = new EdgeController();

    private int[][] doubleKernelCalc(IMAGE px, double[][] kernel1, double[][] kernel2) throws Exception {
        // 获取x方向的sobel
        conv2 = new ThreadPoolReflectCore(px.getGrayMatrix(), kernel1, 24, new ConVCalc());
        conv2.start();
        int[][] sobelX = conv2.getData();
        // 获取y方向的sobel
        conv2 = new ThreadPoolReflectCore(px.getGrayMatrix(), kernel2, 24, new ConVCalc());
        conv2.start();
        int[][] sobelY = conv2.getData();
        // 合并计算结果
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value1 = sobelX[i][j] & 0xFF;
                int value2 = sobelY[i][j] & 0xFF;
                int value = (int) Math.sqrt(Math.pow(value1, 2) + Math.pow(value2, 2));
                value = value > 16 ? value : 0;
                result[i][j] = 255 << 24 | value << 16 | value << 8 | value;
            }
        }
        return result;
    }

    @Override
    public int[][] getSobelEdge(IMAGE px) throws Exception {
        double[][] kernelX = new double[][]{{1, 2, 1}, {0, 0, 0}, {-1, -2, -1},};
        double[][] kernelY = new double[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1},};
        return doubleKernelCalc(px, kernelX, kernelY);
    }

    @Override
    public int[][] getPrewittEdge(IMAGE px) throws Exception {
        double[][] kernelX = new double[][]{{1, 1, 1}, {0, 0, 0}, {-1, -1, -1},};
        double[][] kernelY = new double[][]{{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1},};
        return doubleKernelCalc(px, kernelX, kernelY);
    }

    @Override
    public int[][] getMarrEdge(IMAGE px) throws Exception {
        double[][] kernel = new double[][]{{0, -1, 0}, {-1, 4, -1}, {0, -1, 0},};
        kernel = new double[][]{
                {3, -1, 0},
                {-1, -2, -1},
                {0, -1, 3}};
//        kernel = new double[][]{
//                { 0,-2, 0, 0, 0},
//                { 0, 0,-1, 0, 0},
//                { 2, 0, 0,-1, 0},
//                { 0, 2, 0, 0,-2},
//                { 0, 0, 2, 0, 0}
//        };
        conv2 = new ThreadPoolReflectCore(px.getGrayMatrix(), kernel, 24, new ConVCalc());
        // conv2 = new ThreadPoolReflectCore(px.getArgbMatrix(), kernel, 24, new ConVCalc());
        conv2.start();
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] data = conv2.getData();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = data[i][j] & 0xFF;
                // value = value > 8 ? 180 : 0;
                result[i][j] = 255 << 24 | value << 16 | value << 8 | value;
//                int r = (data[i][j] >> 16) * 0xFF;
//                int g = (data[i][j] >> 8) * 0xFF;
//                int b = data[i][j] * 0xFF;
//                result[i][j] = px.getPixParams(new int[]{255, r * 2, g * 2, b * 2});
            }
        }
        return result;
    }


    @Override
    public int[][] paddingImg(IMAGE px, int radius) {
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = px.getArgbMatrix();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double act = 0;
                for (int k = -radius; k < radius * 2 + 1; k++) {
                    for (int l = -radius; l < radius * 2 + 1; l++) {
                        try {
                            int r = (result[i + k][j + l] >> 16) & 0xFF;
                            if (r == 187) {
                                act += 0.67;
                            } else if (r >= 32) {
                                act++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (act > Math.pow(radius * 2 + 1, 2) / 3.0) {
                    result[i][j] = 255 << 24 | 187 << 16 | 187 << 8 | 187;
                }
            }
        }
        return result;
    }

    @Override
    public int[][] dilateImg(IMAGE px, int radius) {
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = new int[width][height];
        int[][] rawData = px.getGrayMatrix();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = rawData[i][j];
                int count = 0;
                int ac = 0;
                for (int k = -radius; k < radius * 2 + 1; k++) {
                    for (int l = -radius; l < radius * 2 + 1; l++) {
                        try {
                            if (rawData[i + k][j + l] >= 32) {
                                ac++;
                            }
                            count++;
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (ac > count / 2 || value > 32) {
                    result[i][j] = (255 << 24) | (187 << 16) | (187 << 8) | 187;
                } else {
                    result[i][j] = (255 << 24) | (0);
                }
            }
        }
        return result;
    }

    @Override
    public int[][] erosionImg(IMAGE px, int radius) {
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] result = px.getArgbMatrix();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double act = 0;
                for (int k = -radius; k < radius * 2 + 1; k++) {
                    for (int l = -radius; l < radius * 2 + 1; l++) {
                        try {
                            int r = (result[i + k][j + l] >> 16) & 0xFF;
                            int g = (result[i + k][j + l] >> 8) & 0xFF;
                            int b = result[i + k][j + l] & 0xFF;
                            int value = (int) (r * 0.33 + g * 0.33 + b * 0.33);
                            if (value >= 32) {
                                act++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (act <= Math.pow(radius * 2 + 1, 2) * 0.85) {
                    result[i][j] = 255 << 24 | 0 << 16 | 0 << 8 | 0;
                }
            }
        }
        return result;
    }

    @Override
    public int[][] traceImg(IMAGE px) throws Exception {
        int width = px.getWidth();
        int height = px.getHeight();
        double[][][] hsv = px.getHsvMatrix();
        IMAGE edge = edgeCtrl.getImgEdge(px, EdgeController.SOBEL);
        EdgeTrace edgeTrace = new EdgeTrace(edge);
        edgeTrace.start(EdgeTrace.PATTERN_ONE);
        for (Node node : edgeTrace.getPathList()) {
            if (node.getNodeSize() > 8) {
                trackAndDilate(node, width, height, hsv);
            }
        }
        return px.HSV2RGB(hsv);
    }

    @Override
    public int[][] getThumbnail(IMAGE px, int step) {
        System.out.println("Thumbnail");
        int[][] matrix = px.getArgbMatrix();
        int width = px.getWidth();
        int height = px.getHeight();
        int resWidth = width / step;
        int resHeight = height / step;
        resWidth = resWidth > 0 ? resWidth : 1;
        resHeight = resHeight > 0 ? resHeight : 1;
        int[][] result = new int[resWidth][resHeight];
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                result[i][j] = matrix[i * step][j * step];
            }
        }
        return result;
    }

    private void trackAndDilate(Node node, int width, int height, double[][][] hsv) {
        if (node.getNext() != null) {
            trackAndDilate(node.getNext(), width, height, hsv);
        }
        if (node.getNodeType() == Node.LEAF_NODE && node.getPointSize() < 8) {
            return;
        }
        for (Point p : node.getPointList()) {
            for (int i = 0; i < 16; i++) {
                int[] nextDir = EdgeTrace.getNextCoordinate(i, p.getPx(), p.getPy());
                if (nextDir[0] > -1 && nextDir[0] < width && nextDir[1] > -1 && nextDir[1] < height) {
                    hsv[nextDir[0]][nextDir[1]] = hsv[p.getPx()][p.getPy()];
                }
            }
        }
    }
}

