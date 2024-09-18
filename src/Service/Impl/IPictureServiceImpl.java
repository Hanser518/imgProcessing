package Service.Impl;

import Entity.IMAGE;
import Entity.PIXEL;
import Service.TUGServiceImpl;
import Service.ICalculateService;
import Service.IPictureService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class IPictureServiceImpl implements IPictureService {
    static ICalculateService calculateServer = new ICalculateServiceImpl();
    @Override
    public IMAGE getSubImage(IMAGE img, int width, int height, int startX, int startY) {
        int W = width + startX > img.getWidth() ? img.getWidth() - startX : width;
        int H = height + startY > img.getHeight() ? img.getHeight() - startY : height;
        int[][] px = new int[W][H];
        int[][] raw = img.getPixelMatrix();
        for(int i = 0;i < W;i ++){
            for(int j = 0;j < H;j ++){
                px[i][j] = raw[i + startX][j + startY];
            }
        }
        return new IMAGE(px);
    }

    @Override
    public IMAGE getCombineImage(List<IMAGE> images, boolean horizontal) {
        int width = 0;
        int height = 0;
        for(IMAGE img: images){
            if(horizontal){
                width += img.getWidth();
                height = img.getHeight();
            }else{
                width = img.getWidth();
                height += img.getHeight();
            }
        }
        int[][] px = new int[width][height];
        if(horizontal){
            int x = 0;
            for(int count = 0;count < images.size();count ++){
                int[][] raw = images.get(count).getPixelMatrix();
                for(int i = 0;i < raw.length;i ++){
                    for(int j = 0;j < height;j ++){
                        px[x][j] = raw[i][j];
                    }
                    x ++;
                }
            }
        }else{
            int y = 0;
            for(int count = 0;count < images.size();count ++){
                int[][] raw = images.get(count).getPixelMatrix();
                for(int j = 0;j < raw[0].length;j ++){
                    for(int i = 0;i < width;i ++){
                        px[i][y] = raw[i][j];
                    }
                }
                y ++;
            }
        }
        return new IMAGE(px);
    }

    @Override
    public IMAGE getReizedImage(IMAGE img, int w, int h) {
        BufferedImage raw = img.getImg();
        Image scaledImage = raw.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        // 将调整尺寸后的图像转换为BufferedImage
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(scaledImage, 0, 0, null);
        return new IMAGE(result);
    }

    @Override
    public IMAGE getUltraGas(IMAGE img, int baseSize, int maxSize) {
        int[][] map = img.getPixelMatrix();
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
        int countBefore = -1;
        while (true) {
            int count = 0;
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    count++;
                }
            }
            if (count != countBefore) {
                countBefore = count;
                System.out.print("\rService.Thread: ");
                for (int i = 0; i < threadCount; i++) {
                    if (i < count) System.out.print("O ");
                    else System.out.print("A ");
                }
            }
            if (count == threads.length) break;
        }
        System.out.print("\n");
        for (int c = 0; c < threadCount; c++) {
            for (int i = step * c; i < step * (c + 1) && i < result.length; i++) {
                for (int j = 0; j < result[0].length; j++) {
                    result[i][j] = conVs[c].result[i - step * c][j];
                }
            }
        }
        return new IMAGE(result);
    }

    @Override
    public IMAGE getGrayImage(IMAGE img) {
        BufferedImage raw = img.getImg();
        BufferedImage result = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(raw, 0, 0, null);
        return new IMAGE(result);
    }

    @Override
    public IMAGE getCalcGray(IMAGE img) {
        int[][] gray = img.getGrayMatrix();
        return new IMAGE(gray);
    }

    @Override
    public IMAGE getEdge(IMAGE img, boolean multiThreads, boolean accurateCalculate, boolean erosion, boolean pureEdge) {
        double[][] kernel_x = {
                {1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}
        };
        double[][] kernel_y = {
                {1, 2, 1},
                {0, 0, 0},
                {-1,-2,-1}
        };
        IMAGE gray = getGrayImage(img);
        // Sobel计算边缘
        int[][] sobelX = calculateServer.convolution(gray, kernel_x, multiThreads, accurateCalculate, true).getPixelMatrix();
        int[][] sobelY = calculateServer.convolution(gray, kernel_y, multiThreads, accurateCalculate, true).getPixelMatrix();
        int[][] matrix = new int[img.getWidth()][img.getHeight()];
        int[][] edge = new int[img.getWidth()][img.getHeight()];
        List<PIXEL> pointList = new ArrayList<>();
        PIXEL p = new PIXEL();
        for(int i = 0;i < img.getWidth();i ++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int[] pm = {255, 0, 0, 0};
                int[] psx = img.getArgbParams(sobelX[i][j]);
                int[] psy = img.getArgbParams(sobelY[i][j]);
                pm[1] = (int) Math.sqrt(psx[1] * psx[1] + psy[1] * psy[1]);
                pm[2] = (int) Math.sqrt(psx[2] * psx[2] + psy[2] * psy[2]);
                pm[3] = (int) Math.sqrt(psx[3] * psx[3] + psy[3] * psy[3]);
                matrix[i][j] = img.getPixParams(pm);
                if(img.getGrayPixel(pm) > 160){
                    p = new PIXEL(pm, i, j);
                    pointList.add(p);
                    // edge[i][j] = img.getGrayPixel(pm);
                }else{
                    edge[i][j] = 0;
                }
            }
        }
        if(!pureEdge){
            if(erosion){
                return calculateServer.erosion(new IMAGE(matrix));
            }
            return new IMAGE(matrix);
        }
        // 尝试在matrix中获取一个阈值点
        int[][] map = img.getGrayMatrix(matrix);
        if(erosion){
            IMAGE eros = calculateServer.erosion(new IMAGE(matrix));
            map = img.getGrayMatrix(eros.getPixelMatrix());
        }
        while(!pointList.isEmpty()){
            PIXEL stackTop = pointList.get(pointList.size() - 1);
            PIXEL result = broadcastSearch(map, stackTop);
            if(result == null){
                pointList.remove(pointList.size() - 1);
            }else{
                pointList.add(result);
                edge[result.x][result.y] = img.getGrayPixel(result.argb);
            }
            // System.out.print("\r" + pointList.size());
        }
        System.out.println();
        for(int i = 0;i < img.getWidth();i ++) {
            for (int j = 0; j < img.getHeight(); j++) {
                matrix[i][j] = img.getPixParams(new int[]{255, edge[i][j], edge[i][j], edge[i][j]});
            }
        }
        return new IMAGE(matrix);
    }

    private PIXEL broadcastSearch(int[][] map, PIXEL loc){
        PIXEL result = null;
        // 以自身为中心，搜索周围2圈
        for(int count = 1; count <= 2 && result == null; count++){
            for(int i = loc.x - count;i < loc.x + count && result == null;i ++){
                for(int j = loc.y - count;j < loc.y + count && result == null;j ++){
                    try{
                        if(map[i][j] > 16 && i != loc.x && j != loc.y){
                            result = new PIXEL(new int[]{255, 255, 255, 255}, i, j);
                            map[i][j] = 0;
                            return result;
                        }
                    }catch (Exception e){

                    }
                }
            }
        }
        return result;
    }

    @Override
    public IMAGE getEnhanceImage(IMAGE img, double theta) {
        int[][] enhanceMatrix = calculateServer.getEnhanceMatrix(img, theta);
        int[][] rawMatrix = img.getPixelMatrix();
        for(int i = 0;i < img.getWidth();i ++){
            for(int j = 0;j < img.getHeight();j ++){
                rawMatrix[i][j] = enhanceMatrix[i][j];
            }
        }
        return new IMAGE(rawMatrix);
    }

    @Override
    public IMAGE getEnhanceImage2(IMAGE img) {
        int[][] rawMatrix = img.getPixelMatrix();
        // int lumen = img.getPixParams(new int[]{255, 10, 10, 10});
        for(int i = 0;i < img.getWidth();i ++){
            for(int j = 0;j < img.getHeight();j ++){
                int[] lumen = img.getArgbParams(rawMatrix[i][j]);
                lumen[1] = lumen[1] * 1.05 > 255 ? 255 : (int) (lumen[1] * 1.05);
                lumen[2] = lumen[2] * 1.05 > 255 ? 255 : (int) (lumen[2] * 1.05);
                lumen[3] = lumen[3] * 1.05 > 255 ? 255 : (int) (lumen[3] * 1.05);
                rawMatrix[i][j] = img.getPixParams(lumen);
            }
        }
        return new IMAGE(rawMatrix);
    }

    @Override
    public IMAGE getGammaFix(IMAGE img, double param) {
        // 得到灰度图
        int[][] g = img.getGrayMatrix();
        int[][] px = img.getPixelMatrix();
        // 根据输入的参数进行gamma修正，采用对数法
        // 对函数y = ln((x + 1.0/param) * param)求导，得到导数表达式gamma（g）
        // 函数g = param / (x * param + 1)，对g进行增强
        // 函数g` = Math.pow(param, 2) / (x * param + 1)，以g`为增强曲线
        int w = img.getWidth();
        int h = img.getHeight();
        for(int i = 0;i < w;i ++){
            for(int j = 0;j < h;j ++){
                int[] p = img.getArgbParams(px[i][j]);
                double num = Math.pow(param, 1) / Math.pow(g[i][j], 1.5) + 0.9;
                //System.out.println("raw:  " + p[1] + " " + p[2] + " " + p[3]);
                p[1] = (int) (g[i][j] * num);
                p[2] = (int) (g[i][j] * num);
                p[3] = (int) (g[i][j] * num);
                //System.out.println((Math.pow(param, 2) / (g[i][j] * param + 1)));
                //System.out.println("plus: " + p[1] + " " + p[2] + " " + p[3]);
                // g[i][j] = img.getPixParams(p);
                px[i][j] = img.getPixParams(p);
            }
        }
        return new IMAGE(px);
    }
}
