package service.impl;

import entity.Image;
import entity.PIXEL;
import service.TUGServiceImpl;
import service.ICalculateService;
import service.IPictureService;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class IPictureServiceImpl implements IPictureService {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    static int[][] imgData = null;

    @Override
    public Image getSubImage(Image img, int width, int height, int startX, int startY) {
        int W = width + startX > img.getWidth() ? img.getWidth() - startX : width;
        int H = height + startY > img.getHeight() ? img.getHeight() - startY : height;
        int[][] px = new int[W][H];
        if (W == 0 || H == 0) {
            System.out.println(width + " " + height + " " + startX + " " + startY);
            System.out.println(W + " " + H);
        }
        int[][] raw = img.getArgbMatrix();
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                px[i][j] = raw[i + startX][j + startY];
            }
        }
        return new Image(px);
    }

    @Override
    public Image getCombineImage(List<Image> images, boolean horizontal) {
        int width = 0;
        int height = 0;
        for (entity.Image img : images) {
            if (horizontal) {
                width += img.getWidth();
                height = img.getHeight();
            } else {
                width = img.getWidth();
                height += img.getHeight();
            }
        }
        int[][] px = new int[width][height];
        if (horizontal) {
            int x = 0;
            for (int count = 0; count < images.size(); count++) {
                int[][] raw = images.get(count).getArgbMatrix();
                for (int i = 0; i < raw.length; i++) {
                    for (int j = 0; j < raw[0].length; j++) {
                        px[x][j] = raw[i][j];
                    }
                    x++;
                }
            }
        } else {
            int y = 0;
            for (int count = 0; count < images.size(); count++) {
                int[][] raw = images.get(count).getArgbMatrix();
                for (int j = 0; j < raw[0].length; j++) {
                    for (int i = 0; i < raw.length; i++) {
//                        if(y > 120){
//                            System.out.println(i + " " + j + " " + raw.length + " " + raw[0].length);
//                        }
                        px[i][y] = raw[i][j];
                    }
                    y++;
                }
            }
        }
        return new Image(px);
    }

    @Override
    public Image getReizedImage(entity.Image img, int w, int h) {
        BufferedImage raw = img.getRawFile();
        java.awt.Image scaledImage = raw.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        // 将调整尺寸后的图像转换为BufferedImage
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(scaledImage, 0, 0, null);
        return new Image(result);
    }

    @Override
    public entity.Image getUltraGas(Image img, int baseSize, int maxSize) {
        int[][] map = img.getArgbMatrix();
        int[][] result = new int[img.getWidth()][img.getHeight()];
        int[][] maxFill = calculateServer.pixFill(map, calculateServer.getGasKernel(maxSize));
        // System.out.printf("Building Map...\n");
        // int[][] Test.gasMap = calculateServer.getGasMap(img, baseSize, maxSize);
        double[][] maxKernel = calculateServer.getGasKernel(maxSize);
        int minStep = Math.min(maxFill.length, maxFill[0].length) / Math.max(maxKernel.length, maxKernel[0].length) + 1;
        int threadCount = (int) Math.sqrt(Math.max(maxKernel.length, maxKernel[0].length)) * 2;
        threadCount = Math.min(threadCount, 32);
        threadCount = Math.min(threadCount, Math.min(maxFill.length, maxFill[0].length) / minStep);
        // int threadCount = Math.max(kernel.length, kernel[0].length);
        int step = maxFill.length / threadCount + 1;
        Thread[] threads = new Thread[threadCount];
        TUGServiceImpl[] conVs = new TUGServiceImpl[threadCount];
        for (int i = 0; i < threadCount; i++) {
            conVs[i] = new TUGServiceImpl(maxFill, baseSize, maxSize, step * i, step, img.getWidth(), img.getHeight());
            threads[i] = new Thread(conVs[i]);
            threads[i].start();
        }
        System.out.println();
        ICalculateServiceImpl.threadProcessing(threadCount, threads);
        for (int c = 0; c < threadCount; c++) {
            for (int i = step * c; i < step * (c + 1) && i < result.length; i++) {
                for (int j = 0; j < result[0].length; j++) {
                    result[i][j] = conVs[c].result[i - step * c][j];
                }
            }
        }
        return new Image(result);
    }

    @Override
    public Image getGrayImage(entity.Image img) {
        BufferedImage raw = img.getRawFile();
        BufferedImage result = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(raw, 0, 0, null);
        return new Image(result);
    }

    @Override
    public Image getCalcGray(entity.Image img) {
        int[][] gray = img.getGrayMatrix();
        return new entity.Image(gray);
    }

    @Override
    public Image getEdge(entity.Image img, boolean multiThreads, boolean accurateCalculate, boolean erosion, boolean pureEdge) {
        double[][] kernel_x = {
                {1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}
        };
        double[][] kernel_y = {
                {1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}
        };
        Image gray = getGrayImage(img);
        // Sobel计算边缘
        int[][] sobelX = calculateServer.convolution(gray, kernel_x, multiThreads, accurateCalculate, true).getArgbMatrix();
        int[][] sobelY = calculateServer.convolution(gray, kernel_y, multiThreads, accurateCalculate, true).getArgbMatrix();
        int[][] matrix = new int[img.getWidth()][img.getHeight()];
        int[][] edge = new int[img.getWidth()][img.getHeight()];
        List<PIXEL> pointList = new ArrayList<>();
        PIXEL p = new PIXEL();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int r, g, b;
                int xValue = sobelX[i][j];
                int yValue = sobelY[i][j];
                r = (int) Math.sqrt(Math.pow((xValue >> 16) & 0xFF, 2) + Math.pow((yValue >> 16) & 0xFF, 2));
                g = (int) Math.sqrt(Math.pow((xValue >> 8) & 0xFF, 2) + Math.pow((yValue >> 8) & 0xFF, 2));
                b = (int) Math.sqrt(Math.pow((xValue) & 0xFF, 2) + Math.pow((yValue) & 0xFF, 2));
                matrix[i][j] = 255 << 24 | r << 16 | g << 8 | b;
                if (r > 160) {
                    p = new PIXEL(new int[]{255, r, g, b}, i, j);
                    pointList.add(p);
                    // edge[i][j] = img.getGrayPixel(pm);
                } else {
                    edge[i][j] = 0;
                }
            }
        }
        if (!pureEdge) {
            if (erosion) {
                return calculateServer.erosion(new entity.Image(matrix));
            }
            return new Image(matrix);
        }
        // 尝试在matrix中获取一个阈值点
        int[][] map = new entity.Image(matrix).getGrayMatrix();
        if (erosion) {
            entity.Image eros = calculateServer.erosion(new Image(matrix));
            map = eros.getGrayMatrix();
        }
        while (!pointList.isEmpty()) {
            PIXEL stackTop = pointList.get(pointList.size() - 1);
            PIXEL result = broadcastSearch(map, stackTop);
            if (result == null) {
                pointList.remove(pointList.size() - 1);
            } else {
                pointList.add(result);
//                edge[result.x][result.y] = img.getGrayPixel(result.argb);
            }
            // System.out.print("\r" + pointList.size());
        }
        System.out.println();
//        for(int i = 0;i < img.getWidth();i ++) {
//            for (int j = 0; j < img.getHeight(); j++) {
//                matrix[i][j] = img.getPixParams(new int[]{255, edge[i][j], edge[i][j], edge[i][j]});
//            }
//        }
        return new entity.Image(matrix);
    }

    private PIXEL broadcastSearch(int[][] map, PIXEL loc) {
        PIXEL result = null;
        // 以自身为中心，搜索周围2圈
        for (int count = 1; count <= 2 && result == null; count++) {
            for (int i = loc.x - count; i < loc.x + count && result == null; i++) {
                for (int j = loc.y - count; j < loc.y + count && result == null; j++) {
                    try {
                        if (map[i][j] > 16 && i != loc.x && j != loc.y) {
                            result = new PIXEL(new int[]{255, 255, 255, 255}, i, j);
                            map[i][j] = 0;
                            return result;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        return result;
    }

    @Override
    public Image getEnhanceImage(Image img, double theta) {
        int[][] enhanceMatrix = calculateServer.getEnhanceMatrix(img, theta);
        int[][] rawMatrix = img.getArgbMatrix();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                rawMatrix[i][j] = enhanceMatrix[i][j];
            }
        }
        return new Image(rawMatrix);
    }

    @Override
    public Image getEnhanceImage2(entity.Image img) {
        int[][] rawMatrix = img.getArgbMatrix();
        // int lumen = img.getPixParams(new int[]{255, 10, 10, 10});
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int r = (rawMatrix[i][j] >> 16) & 0xFF;
                int g = (rawMatrix[i][j] >> 8) & 0xFF;
                int b = (rawMatrix[i][j]) & 0xFF;
                r = r * 1.05 > 255 ? 255 : (int) (r * 1.05);
                g = g * 1.05 > 255 ? 255 : (int) (g * 1.05);
                b = b * 1.05 > 255 ? 255 : (int) (b * 1.05);
                rawMatrix[i][j] = 255 << 24 | r << 16 | g << 8 | b;
            }
        }
        return new entity.Image(rawMatrix);
    }

    @Override
    public Image getGammaFix(Image img, double param) {
        // 得到灰度图
        int[][] gray = img.getGrayMatrix();
        int[][] px = img.getArgbMatrix();
        // 根据输入的参数进行gamma修正，采用对数法
        // 对函数y = ln((x + 1.0/param) * param)求导，得到导数表达式gamma（g）
        // 函数g = param / (x * param + 1)，对g进行增强
        // 函数g` = Math.pow(param, 2) / (x * param + 1)，以g`为增强曲线
        int w = img.getWidth();
        int h = img.getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int r, g, b;
                double num = Math.pow(param, 1) / Math.pow(gray[i][j], 1.5) + 0.9;
                r = (int) (gray[i][j] * num);
                g = (int) (gray[i][j] * num);
                b = (int) (gray[i][j] * num);
                px[i][j] = 255 << 24 | r << 16 | g << 8 | b;
            }
        }
        return new entity.Image(px);
    }

    public void imgData(entity.Image p) {
        imgData = p.getArgbMatrix();
    }

    public entity.Image getSubImage(int width, int height, int startX, int startY) {
        if (imgData == null) return null;
        int W = width + startX > imgData.length ? imgData.length - startX : width;
        int H = height + startY > imgData[0].length ? imgData[0].length - startY : height;
        int[][] px = new int[W][H];
        if (W == 0 || H == 0) {
            System.out.println(width + " " + height + " " + startX + " " + startY);
            System.out.println(W + " " + H);
        }
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                px[i][j] = imgData[i + startX][j + startY];
            }
        }
        return new Image(px);
    }
}
