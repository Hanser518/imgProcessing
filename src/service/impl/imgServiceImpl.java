package service.impl;

import algorithm.edgeTrace.entity.Node;
import algorithm.edgeTrace.entity.Point;
import algorithm.edgeTrace.main.EdgeTrace;
import controller.EdgeController;
import entity.IMAGE;
import service.threadPool.thread.ConVCalc;
import service.threadPool.core.ThreadPoolCore;
import service.imgService;
import service.threadPool.core.ThreadPoolReflectCore;

public class imgServiceImpl implements imgService {
    static private ThreadPoolCore conv;
    static private ThreadPoolReflectCore conv2;
    static EdgeController edgeCtrl = new EdgeController();

    private int[][] doubleKernelCalc(IMAGE px, double[][] kernel1, double[][] kernel2) throws Exception {
        // 获取x方向的sobel
        conv2 = new ThreadPoolReflectCore(px.getGrayMatrixInArgbModule(), kernel1, 24, new ConVCalc());
        conv2.start();
        int[][] sobelX = conv2.getData();
        // 获取y方向的sobel
        conv2 = new ThreadPoolReflectCore(px.getGrayMatrixInArgbModule(), kernel2, 24, new ConVCalc());
        conv2.start();
        int[][] sobelY = conv2.getData();
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
        double[][] kernel = new double[][]{{ 0,-1, 0}, {-1, 4,-1}, { 0,-1, 0},};
        kernel = new double[][]{{3, -1, 0}, {-1, -2, -1}, {0, -1, 3}};
        conv2 = new ThreadPoolReflectCore(px.getGrayMatrixInArgbModule(), kernel, 24, new ConVCalc());
        // conv2 = new ThreadPoolReflectCore(px.getPixelMatrix(), kernel, 24, new ConVCalc());
        conv2.start();
        int width = px.getWidth();
        int height = px.getHeight();
        int[][] data = conv2.getData();
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = px.getArgbParams(data[i][j])[1];
                // value = value > 8 ? 180 : 0;
                result[i][j] = px.getPixParams(new int[]{255, value, value, value});
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
        int[][] result = px.getPixelMatrix();
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
                    result[i][j] = px.getPixParams(new int[]{255, 187, 187, 187});
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
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int value = rawData[i][j];
                int count = 0;
                int ac = 0;
                for (int k = -radius; k < radius * 2 + 1; k++) {
                    for (int l = -radius; l < radius * 2 + 1; l++) {
                        try {
                            if (rawData[i + k][j + l] >= 32) {
                                ac ++;
                            }
                            count ++;
                        } catch (Exception ignored) {
                        }
                    }
                }
                if(ac > count / 2 || value > 32){
                    result[i][j] = (255 << 24) | (187 << 16) | (187 << 8) | 187;
                }else{
                    result[i][j] = (255 << 24) | (0);
                }
            }
        }
        return result;
    }

    @Override
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
                            if (r >= 32) {
                                act++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (act <= Math.pow(radius * 2 + 1, 2) * 0.85) {
                    result[i][j] = px.getPixParams(new int[]{255, 0, 0, 0});
                }
            }
        }
        return result;
    }

    @Override
    public int[][] traceImg(IMAGE px) throws Exception {
        int width = px.getWidth();
        int height = px.getHeight();
        double[][][] hsv = px.RGB2HSV();
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

    private void trackAndDilate(Node node, int width, int height, double[][][] hsv){
        if (node.getNext() != null) {
            trackAndDilate(node.getNext(), width, height, hsv);
        }
        if (node.getNodeType() == Node.LEAF_NODE && node.getPointSize() < 8) {
            return;
        }
        for (Point p : node.getPointList()) {
            for(int i = 0;i < 16;i ++){
                int[] nextDir = EdgeTrace.getNextCoordinate(i, p.getPx(), p.getPy());
                if(nextDir[0] > -1 && nextDir[0] < width && nextDir[1] > -1 && nextDir[1] < height){
                    hsv[nextDir[0]][nextDir[1]] = hsv[p.getPx()][p.getPy()];
//                    double hn = hsv[nextDir[0]][nextDir[1]][0];
//                    double sn = hsv[nextDir[0]][nextDir[1]][1];
//                    double vn = hsv[nextDir[0]][nextDir[1]][2];
//                    double hp = hsv[p.getPx()][p.getPy()][0];
//                    double sp = hsv[p.getPx()][p.getPy()][1];
//                    double vp = hsv[p.getPx()][p.getPy()][2];
//                    hsv[nextDir[0]][nextDir[1]][0] = Math.max(hn, hp); // hn > hp ? hn * 0.9 + hp * 0.1 : hn * 0.1 + hp * 0.9;
//                    hsv[nextDir[0]][nextDir[1]][1] = Math.max(sn, sp); // sn > sp ? sn * 0.9 + sp * 0.1 : sn * 0.1 + sp * 0.9;
//                    hsv[nextDir[0]][nextDir[1]][2] = Math.max(vn, vp); // vn > vp ? vn * 0.9 + vp * 0.1 : vn * 0.1 + vp * 0.9;
//                    if(sn > sp){
//                        hsv[nextDir[0]][nextDir[1]] = new double[]{hn, sn, vn};
//                    }else{
//                        hsv[nextDir[0]][nextDir[1]] = new double[]{hp, sp, vp};
//                    }
                }
            }
        }
    }
}

