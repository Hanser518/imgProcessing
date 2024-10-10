package service.impl;

import entity.IMAGE;
import service.ICalculateService;
import service.IPictureService;

import java.util.*;

public class ICalculateServiceImpl implements ICalculateService {
    private IMAGE img = new IMAGE();
    private IPictureService picService = new IPictureServiceImpl();

    @Override
    public double[][] getGasKernel(int size,
                                   double theta) {
        int kernelSize = size * 2 + 1;
        double[][] gasKernel = new double[kernelSize][kernelSize];
        double[] w = new double[kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            w[i] = Math.exp(-1.0 * (i - size) * (i - size) / 2 * theta * theta);
        }
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                gasKernel[i][j] = w[i] * w[j];
                // System.out.printf("%.2f\t", gasKernel[i][j]);
            }
            // System.out.println();
        }
        return gasKernel;
    }

    @Override
    public double[][] getGasKernel(int size) {
        double theta = Math.sqrt(Math.log(0.1) * -1.0 * 2 / Math.pow(-size, 2));
        return getGasKernel(size, theta);
    }

    @Override
    public IMAGE convolution(IMAGE img,
                             double[][] kernel,
                             boolean multiThreads, boolean accurateCalculate, boolean negativeFix) {
        int[][] rawP = img.getPixelMatrix();
        int[][] picFill = pixFill(rawP, kernel);
        // 进行卷积运算
        if (!multiThreads) {
            if (accurateCalculate) {
                for (int i = 0; i < img.getWidth(); i++) {
                    for (int j = 0; j < img.getHeight(); j++) {
                        rawP[i][j] = picMarCalc(picFill, kernel, i, j, negativeFix);
                    }
                }
            } else {
                for (int i = 0; i < img.getWidth(); i++) {
                    for (int j = 0; j < img.getHeight(); j++) {
                        if (i % 2 == 0 || j % 2 == 0)
                            rawP[i][j] = picMarCalc(picFill, kernel, i, j, negativeFix);
                        else {
                            rawP[i][j] = 0;
                        }
                    }
                }
                for (int i = 0; i < img.getWidth(); i++) {
                    for (int j = 0; j < img.getHeight(); j++) {
                        if (rawP[i][j] == 0) {
                            int count = 0;
                            int[] arrR = img.getArgbParams(rawP[i][j]);
                            for (int k = 0; k < 4; k++) {
                                try {
                                    int p;
                                    if (k == 0)
                                        p = rawP[i - 1][j];
                                    else if (k == 1)
                                        p = rawP[i][j - 1];
                                    else if (k == 2)
                                        p = rawP[i + 1][j];
                                    else
                                        p = rawP[i][j + 1];
                                    int[] arrP = img.getArgbParams(p);
                                    for (int l = 0; l < 4; l++)
                                        arrR[l] += arrP[l];
                                    count++;
                                } catch (Exception e) {

                                }
                            }
                            for (int l = 0; l < 4; l++)
                                arrR[l] /= count;
                            rawP[i][j] = img.getPixParams(arrR);
                        }
                    }
                }
            }
        } else {
            int minStep = Math.min(picFill.length, picFill[0].length) / Math.max(kernel.length, kernel[0].length) + 1;
            int threadCount = (int) Math.sqrt(Math.max(kernel.length, kernel[0].length)) + 3;
            threadCount = Math.min(threadCount, 32);
            threadCount = Math.min(threadCount, Math.min(picFill.length, picFill[0].length) / minStep);
            // int threadCount = Math.max(kernel.length, kernel[0].length);
            int step = picFill.length / threadCount + 1;
            Thread[] threads = new Thread[threadCount];
            TPMCServerImpl[] conVs = new TPMCServerImpl[threadCount];
            for (int i = 0; i < threadCount; i++) {
                conVs[i] = new TPMCServerImpl(picFill, kernel, step * i, step, img.getWidth(), img.getHeight());
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
                    System.out.print("\rService.Extends.Thread: ");
                    for (int i = 0; i < threadCount; i++) {
                        if (i < count) System.out.print("O  ");
                        else System.out.print("A  ");
                    }
                }
                if (count == threads.length) break;
            }
            System.out.print("\n");
            for (int c = 0; c < threadCount; c++) {
                for (int i = step * c; i < step * (c + 1) && i < rawP.length; i++) {
                    for (int j = 0; j < rawP[0].length; j++) {
                        rawP[i][j] = conVs[c].result[i - step * c][j];
                    }
                }
            }
        }
        return new IMAGE(rawP);
    }

    @Override
    public IMAGE erosion(IMAGE img) {
        int[][] rawP = img.getPixelMatrix();
        int[][] result = new int[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int count = 0;      // 有效搜索数
                int activeCount = 0;// 活跃数
                int size = 3;       // 搜索范围
                int[] p = new int[4];
                for (int k = -size; k <= size; k++) {
                    for (int l = -size; l <= size; l++) {
                        try {
                            p = img.getArgbParams(rawP[i + k][j + l]);
                            if (img.getGrayPixel(p) > 16)
                                activeCount++;
                            count++;
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (activeCount < (count - 1) / 2) {
                    result[i][j] = img.getPixParams(new int[]{255, 0, 0, 0});
                } else {
                    for (int n = 1; n < 4; n++)
                        p[n] = p[n] * 1.5 > 255 ? 255 : (int) (p[n] * 1.5);
                    result[i][j] = img.getPixParams(p);
                }
            }
        }
        return new IMAGE(result);
    }

    @Override
    public Map<Integer, Integer> getGList(IMAGE img) {
        Map<Integer, Integer> GMap = new HashMap<>();
        for (int i = 0; i < 8; i++)
            GMap.put(i, 0);
        int[] list = img.getPixelList();
        for (int px : list) {
            int[] pixel = img.getArgbParams(px);
            int gray = (int) (0.299 * pixel[1] + 0.587 * pixel[2] + 0.114 * pixel[3]);
            int G = (int) (Math.log(gray) / Math.log(2));
            G = Math.max(G, 0);
            GMap.put(G, GMap.get(G) + 1);
        }
        return GMap;
    }

    @Override
    public Map<Integer, Double> getGRate(IMAGE img) {
        Map<Integer, Double> GAccumulateMap = new HashMap<>();
        Map<Integer, Integer> GMap = getGList(img);
        double total = img.getWidth() * img.getHeight();
        for (int i = 0; i < 8; i++) {
            GAccumulateMap.put(i, GMap.get(i) / total);
        }
        return GAccumulateMap;
    }

    @Override
    public Map<Integer, Double> getGAccumulateRate(IMAGE img) {
        Map<Integer, Double> GAccumulateMap = new HashMap<>();
        Map<Integer, Double> GRate = getGRate(img);
        for (int i = 0; i < 8; i++) {
            double base = GRate.get(i);
            if (i != 0)
                base += GAccumulateMap.get(i - 1);
            GAccumulateMap.put(i, base);
        }
        return GAccumulateMap;
    }

    @Override
    public Map<Integer, Integer> getActiveList(IMAGE img) {
        Map<Integer, Integer> ActiveMap = new HashMap<>();
        for (int i = 0; i < 8; i++)
            ActiveMap.put(i, 0);
        int[] list = img.getPixelList();
        for (int px : list) {
            int gray = img.getAcValue(px);
            int G = (int) (Math.log(gray) / Math.log(2));
            G = Math.max(G, 0);
            // System.out.println(G + " " + gray);
            ActiveMap.put(G, ActiveMap.get(G) + 1);
        }
        return ActiveMap;
    }

    @Override
    public Map<Integer, Double> getActiveRate(IMAGE img) {
        Map<Integer, Double> Ac2Map = new HashMap<>();
        Map<Integer, Integer> GMap = getActiveList(img);
        double total = img.getWidth() * img.getHeight();
        for (int i = 0; i < 8; i++) {
            Ac2Map.put(i, GMap.get(i) / total);
        }
        return Ac2Map;
    }

    @Override
    public Map<Integer, Double> getActiveAccumulateRate(IMAGE img) {
        Map<Integer, Double> Ac2Map = new HashMap<>();
        Map<Integer, Double> GRate = getActiveRate(img);
        for (int i = 0; i < 8; i++) {
            double base = GRate.get(i);
            if (i != 0)
                base += Ac2Map.get(i - 1);
            Ac2Map.put(i, base);
        }
        return Ac2Map;
    }

    @Override
    public int[][] getEnhanceMatrix(IMAGE img, double theta) {
        int width = img.getWidth();
        int height = img.getHeight();

        Map<Integer, Integer> GMap = getGList(img);
        System.out.println("G_list:");
        for (int i = 0; i < GMap.size(); i++) {
            System.out.println(i + ":" + GMap.get(i) + "\t\t");
        }
        int crest;  // 图像最高有效灰度级数
        int sub_sum = 0;
        for (crest = GMap.size() - 1; crest > 0; crest--) {
            if (GMap.get(crest) > width * height * 0.05 - sub_sum) break;
            else sub_sum += GMap.get(crest);
        }
        int trough; // 图像最低有效灰度级数
        sub_sum = 0;
        for (trough = 0; trough < crest; trough++) {
            if (GMap.get(trough) > width * height * 0.05 - sub_sum) break;
            else sub_sum += GMap.get(trough);
        }

        // 计算获取两个阈值
        double goldenRatio = 0.618;
        double threshold1 = (crest - trough) * (goldenRatio) + trough;
        double threshold2 = threshold1 * (goldenRatio);

        // 计算阈值所对应的灰度值
        double[] G_ratio = new double[GMap.size()];
        G_ratio[0] = (double) GMap.get(0) / (width * height);
        for (int i = 1; i < GMap.size(); i++) {
            G_ratio[i] = G_ratio[i - 1] + (double) GMap.get(i) / (width * height);
        }
        System.out.println("\nG_ratio:");
        for (int i = 0; i < GMap.size(); i++) {
            System.out.println(G_ratio[i] + "\t\t"); //i + ":" +
            // if(i % 2 == 1) System.out.println();
        }
        int index1 = (int) (255 * (G_ratio[(int) threshold1]
                + (G_ratio[(int) threshold1 + 1] - G_ratio[(int) threshold1])
                * (threshold1 - (int) threshold1)));
        int index2 = (int) (255 * (G_ratio[(int) threshold2]
                + (G_ratio[(int) threshold2 + 1] - G_ratio[(int) threshold2])
                * (threshold2 - (int) threshold2)));
        System.out.println("\nCrest:" + crest + "\tTrough:" + trough);
        System.out.println("Threshold1:" + threshold1 + "\tThreshold2:" + threshold2);
        System.out.println("Index1:" + index1 + "\tIndex2:" + index2);

        // 对图像进行增幅
        double maxIllu = Math.min(theta * Math.pow(crest - trough, 4) * 2, 256);
        System.out.println("maxIllumination:" + maxIllu);
        double a1 = maxIllu / (1 - 2 * index1 + index1 * index1);
        double b1 = -a1 * index1 * 2;
        double c1 = maxIllu - a1 - b1 + 1;
        double top = a1 * index2 * index2 + b1 * index2 + c1;
        double a2 = (maxIllu - top) / (1 - 2 * index2 + index2 * index2);
        double b2 = -a2 * index2 * 2;
        double c2 = maxIllu - a2 - b2 - top + 1;
        System.out.println("a1:" + a1 + "\tb1:" + b1 + "\tc1:" + c1);
        System.out.println("a2:" + a2 + "\tb2:" + b2 + "\tc2:" + c2);

        int[][] matrix = img.getPixelMatrix();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] pixel = img.getArgbParams(matrix[i][j]);
                int r = pixel[1];
                int g = pixel[2];
                int b = pixel[3];
                int gray = (int) (r * 0.299 + g * 0.587 + b * 0.114);
                double num = 1;
                if (gray <= index1) {
                    num = (a1 * gray * gray + b1 * gray + c1) / 255 + 1;        // 第一次增幅
                }
                if (gray <= index2) {
                    double c = (a2 * gray * gray + b2 * gray + c2) / 255 + 1;   // 第二次增幅
                    num *= c;
                }
                double max = Math.max(r, Math.max(g, b));
                while (max * num > 254.0)
                    num *= (250.0 / (max * num));
                int[] newPx = new int[]{pixel[0], (int) (r * num), (int) (g * num), (int) (b * num)};
                matrix[i][j] = img.getPixParams(newPx);
            }
        }
        return matrix;
    }

    // 图片矩阵计算
    @Override
    public int picMarCalc(int[][] matrix, double[][] kernel, int x, int y, boolean negativeFix) {
        double r = 0, g = 0, b = 0;
        double rate = 0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[0].length; j++) {
                r += kernel[i][j] * ((matrix[x + i][y + j] >> 16) & 0xFF);
                g += kernel[i][j] * ((matrix[x + i][y + j] >> 8) & 0xFF);
                b += kernel[i][j] * (matrix[x + i][y + j] & 0xFF);
                rate += kernel[i][j];
            }
        }
        rate = rate < 10e-2 ? 1.0 : rate;
        if(negativeFix) {
            r = Math.abs(r / rate);
            g = Math.abs(g / rate);
            b = Math.abs(b / rate);
        }else {
            r = Math.max(r / rate, 0);
            g = Math.max(g / rate, 0);
            b = Math.max(b / rate, 0);
        }
        // System.out.println(r + " " + g + " " + b);
        return (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
    }

    @Override
    // 图像填充
    public int[][] pixFill(int[][] img, double[][] kernel) {
        int width = img.length;
        int height = img[0].length;
        int xSide = kernel.length / 2;
        int ySide = kernel[0].length / 2;
        int[][] result = new int[width + 2 * xSide][height + 2 * ySide];
        for (int i = 0; i < width + 2 * xSide; i++) {
            for (int j = 0; j < height + 2 * ySide; j++) {
                if (i < xSide) {
                    if (j < ySide) result[i][j] = img[0][0];
                    else if (j < ySide + height) result[i][j] = img[0][j - ySide];
                    else result[i][j] = img[0][height - 1];
                } else if (i >= width + xSide) {
                    if (j < ySide) result[i][j] = img[width - 1][0];
                    else if (j < ySide + height) result[i][j] = img[width - 1][j - ySide];
                    else result[i][j] = img[width - 1][height - 1];
                } else if (j < ySide) {
                    result[i][j] = img[i - xSide][0];
                } else if (j >= height + ySide) {
                    result[i][j] = img[i - xSide][height - 1];
                } else {
                    result[i][j] = img[i - xSide][j - ySide];
                }
            }
        }
        return result;
    }

    @Override
    public int[][] getGasMap(IMAGE img, int base, int top) {
        int width = img.getWidth();     // 宽
        int height = img.getHeight();   // 高
        int[][] gasMap = new int[width][height];// 高斯图，用于记录对应点位高斯核半径
        // 多线程切分，每200*200为一个单位
        int baseSize = 600;
        int threadCount = (width / baseSize + 1) * (height / baseSize + 1);
        System.out.println(threadCount);
        TGMServiceImpl[] tgm = new TGMServiceImpl[threadCount];
        Thread[] threads = new Thread[threadCount];
        for(int i = 0;i < (width / baseSize + 1);i ++){
            for(int j = 0;j < (height / baseSize + 1);j ++){
                int w = (i + 1) * baseSize < width ? baseSize : width - i * baseSize;
                int h = (j + 1) * baseSize < height ? baseSize : height - j * baseSize;
                // System.out.println(i + " " + j + " " + w + " " + h);
                tgm[i * (height / baseSize + 1) + j] = new TGMServiceImpl(w, h, base, top);
                threads[i * (height / baseSize + 1) + j] = new Thread(tgm[i * (height / baseSize + 1) + j]);
                threads[i * (height / baseSize + 1) + j].start();
                // System.out.println(i + " " + j + " " + (i * (height / baseSize + 1) + j));
            }
        }
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
                System.out.print("\rService.Extends.Thread: ");
                for (int i = 0; i < threadCount; i++) {
                    if (i < count) System.out.print("O ");
                    else System.out.print("A ");
                }
            }
            if (count == threads.length) break;
        }
        System.out.print("\n");
        for(int i = 0;i < (width / baseSize + 1);i ++){
            for(int j = 0;j < (height / baseSize + 1);j ++){
                int w = (i + 1) * baseSize < width ? baseSize : width - i * baseSize;
                int h = (j + 1) * baseSize < height ? baseSize : height - j * baseSize;
                // System.out.printf("%3d,%3d,W=%3d,H=%3d\n", i, j, tgm[(i * (height / baseSize + 1) + j)].map.length, tgm[(i * (height / baseSize + 1) + j)].map[0].length);
                for(int k = 0;k < w;k ++){
                    for(int l = 0;l < h;l ++){
                        gasMap[k + i * baseSize][l + j * baseSize] = tgm[(i * (height / baseSize + 1) + j)].map[k][l];
                    }
                }
            }
        }
        return gasMap;
    }

    @Override
    public int getDirection(int[][] map, int x, int y) {
        List<Integer> backDir = new ArrayList<>();
        int u = 0, r = 0;
        for(int i = -1;i <= 1;i ++){
            for(int j = -1;j <= 1;j ++){
                try {
                    if (map[x + i][y + j] == 0) {
                        backDir.add((i + 1) * 3 + (j + 1));
                    }
                } catch (Exception ignored) {
                }
            }
        }
        if (backDir.size() == 0) {
            return -1;
        }
        int rand = (int) (Math.random() * backDir.size());
//        backDir.forEach((value) -> {
//            System.out.print(value + " ");
//        });
        return backDir.get(rand);
    }

    @Override
    public int[][] getHistogram(IMAGE img) {
        double[][] kernelX = new double[][]{
                { -1, 0, 1},
                { -2, 0, 2},
                { -1, 0, 1}
        };
        double[][] kernelY = new double[][]{
                { 1, 2, 1},
                { 0, 0, 0},
                {-1,-2,-1}
        };
        int w = img.getWidth();
        int h = img.getHeight();
        IMAGE gray = picService.getGrayImage(img);
        int[][] sobelX = convolution(gray, kernelX, true, true, true).getPixelMatrix();
        int[][] sobelY = convolution(gray, kernelY, true, true, true).getPixelMatrix();
        double[][] angle = new double[w][h];    // 角度
        int[][] gradient = new int[w][h];       // 梯度值
        for(int i = 0;i < w;i ++){
            for(int j = 0;j < h;j ++){
                int[] sx = img.getArgbParams(sobelX[i][j]);
                int[] sy = img.getArgbParams(sobelY[i][j]);
                int[] gd = new int[4];
                for(int c = 0;c < 4;c ++){
                    gd[c] = (int) Math.sqrt(Math.pow(sx[c], 2) + Math.pow(sy[c], 2));
                }
                gradient[i][j] = img.getPixParams(gd);
                if(sx[1] != 0)
                    angle[i][j] = Math.atan((double) sy[1] / sx[1]);
                else
                    angle[i][j] = Math.atan((double) sy[1] / 1.01);
            }
        }
        int[][] thetaMap = new int[w][h];
        int[][] result = new int[w][h];
        int dirCount = 4;
        for(int i = 0;i < w;i ++){
            for(int j = 0;j < h;j ++){
                int theta = (int)(angle[i][j] / Math.PI * 2 * dirCount);
                if(img.getArgbParams(gradient[i][j])[1] <= 32)
                    theta = -1;
                thetaMap[i][j] = theta;
            }
        }
        int[][] tm1 = new int[w / 4][h / 4];
        int blockSize = 8;
        int step = blockSize / 2;
        for(int i = 0;i < w - blockSize;i += step){
            for(int j = 0;j < h - blockSize;j += step){
                Map<Integer, Integer> thetaCount = new HashMap<>();
                int maxCount = 0;
                int maxTheta = -1;
                for(int x = i;x < i + blockSize;x ++){
                    for(int y = j;y < j + blockSize;y ++){
                        int counts = thetaCount.computeIfAbsent(thetaMap[x][y], value -> 0) + 1;
                        if(counts > maxCount){
                            maxCount = counts;
                            maxTheta = thetaMap[i][j];
                        }
                        thetaCount.put(thetaMap[x][y], counts);
                    }
                }
                tm1[i / 4][j / 4] = maxTheta;
            }
        }
        for(int i = 0;i < tm1.length;i ++){
            for(int j = 0;j < tm1[0].length;j ++){
                int theta = tm1[i][j];
                combineMatrix(result, theta, i * 4, j * 4);
                switch (theta){
                    case 0:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 101, 101, 101}); break;
                    case 1:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 101, 101, 188}); break;
                    case 2:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 101, 188, 188}); break;
                    case 3:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 101, 188, 101}); break;
                    case 4:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 188, 188, 101}); break;
                    case 5:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 188, 101, 101}); break;
                    case 6:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 188, 101, 188}); break;
                    case 7:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 188, 188, 188}); break;
                    default:
                        result[i * 4][j * 4] = img.getPixParams(new int[]{255, 0, 0, 0}); break;
                }
            }
        }
        return result;
    }

    public int[][] getSubMatrix(int[][] matrix, int sx, int sy, int width, int height){
        int[][] result = new int[width][height];
        for(int i = sx;i < sx + width;i ++){
            for(int j = sy;j < sy + height;j ++){
                result[i - sx][j - sy] = matrix[i][j];
            }
        }
        return result;
    }


    public int[][] DBScanFilter(int[][] matrix, int radius){
        return null;
    }

    public void combineMatrix(int[][] matrix, int theta, int x, int y){
        int[][][] basic = new int[4][4][4];
        basic[0] = new int[][]{
                {-10132123,-10132123,-10132123,-10132123},
                {-10132123,-10132123,-10132123,-16777216},
                {-10132123,-10132123,-16777216,-16777216},
                {-10132123,-16777216,-16777216,-10132123}
        };
        basic[1] = new int[][]{
                {-10132036,-10132036,-10132036,-10132036},
                {-16777216,-10132036,-10132036,-10132036},
                {-16777216,-16777216,-10132036,-10132036},
                {-10132036,-16777216,-16777216,-10132036}
        };
        basic[2] = new int[][]{
                {-10109764,-16777216,-16777216,-10109764},
                {-16777216,-16777216,-10109764,-10109764},
                {-16777216,-10109764,-10109764,-10109764},
                {-10109764,-10109764,-10109764,-10109764}
        };
        // red, 竖向
        basic[3] = new int[][]{
                {-4430491,-16777216,-16777216, -4430491},
                {-4430491, -4430491,-16777216,-16777216},
                {-4430491, -4430491, -4430491,-16777216},
                {-4430491, -4430491, -4430491, -4430491}
        };
        if(theta == -1){
            for(int i = 0;i < basic[0].length;i ++){
                for(int j = 0;j < basic[0][0].length;j ++){
                    matrix[i + x][j + y] = -16777216;
                }
            }
        }else{
            for(int i = 0;i < basic[0].length;i ++){
                for(int j = 0;j < basic[0][0].length;j ++){
                    try{
                        matrix[i + x][j + y] = basic[theta][i][j];
                    }catch (Exception ignored){

                    }
                }
            }
        }
    }
}
