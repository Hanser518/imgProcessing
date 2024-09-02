package Service.Impl;

import Entity.IMAGE;
import Entity.PIXEL;
import Service.ICalculateService;

import java.util.*;

public class ICalculateServiceImpl implements ICalculateService {
    private IMAGE img = new IMAGE();

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
                             boolean multiThreads, boolean accurateCalculate) {
        int[][] rawP = img.getPixelMatrix();
        int[][] picFill = pixFill(rawP, kernel);
        // 进行卷积运算
        if (!multiThreads) {
            if (accurateCalculate) {
                for (int i = 0; i < img.getWidth(); i++) {
                    for (int j = 0; j < img.getHeight(); j++) {
                        rawP[i][j] = picMarCalc(picFill, kernel, i, j);
                    }
                }
            } else {
                for (int i = 0; i < img.getWidth(); i++) {
                    for (int j = 0; j < img.getHeight(); j++) {
                        if (i % 2 == 0 || j % 2 == 0)
                            rawP[i][j] = picMarCalc(picFill, kernel, i, j);
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
            IPMCTServerImpl[] conVs = new IPMCTServerImpl[threadCount];
            for (int i = 0; i < threadCount; i++) {
                conVs[i] = new IPMCTServerImpl(picFill, kernel, step * i, step, img.getWidth(), img.getHeight());
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
                    System.out.print("\rThread: ");
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
    public Map<Integer, Double> getGAccumulateRate(IMAGE img) {
        Map<Integer, Double> GAccumulateMap = new HashMap<>();
        Map<Integer, Integer> GMap = getGList(img);
        double total = img.getWidth() * img.getHeight();
        for (int i = 0; i < 8; i++) {
            double base = 0;
            if (i != 0)
                base = GAccumulateMap.get(i - 1);
            GAccumulateMap.put(i, GMap.get(i) / total + base);
        }
        return GAccumulateMap;
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
    public int picMarCalc(int[][] matrix, double[][] kernel, int x, int y) {
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
        r = Math.abs(r / rate);
        g = Math.abs(g / rate);
        b = Math.abs(b / rate);
        // System.out.println(r + " " + g + " " + b);
        int px = (255 << 24) | ((int) r << 16) | ((int) g << 8) | (int) b;
        return px;
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
        int x = (int) (Math.random() * width);  // 对应点位坐标
        int y = (int) (Math.random() * height);
        gasMap[x][y] = base;    // 初始化
        List<PIXEL> stack = new ArrayList<>();// 存量栈
        stack.add(new PIXEL(x, y));    // 初始化
        while (!stack.isEmpty()) {
            int dir = getDirection(gasMap, x, y);
            switch (dir) {
                case 0:
                    y -= 1;
                    break;
                case 1:
                    x += 1;
                    break;
                case 2:
                    y += 1;
                    break;
                case 3:
                    x -= 1;
                    break;
            }
            if (dir == -1) {
                stack.remove(stack.get(stack.size() - 1));// 移除栈顶
                if (stack.size() != 0) {
                    PIXEL tp = stack.get(stack.size() - 1);
                    x = tp.x;
                    y = tp.y;
                }
            } else {
                PIXEL tp = stack.get(stack.size() - 1);
                int bX = tp.x;
                int bY = tp.y;
                gasMap[x][y] = (int) (gasMap[bX][bY] + (Math.random() - 0.4) * 4);
                gasMap[x][y] = gasMap[x][y] > top ? top : gasMap[x][y];
                gasMap[x][y] = gasMap[x][y] < base ? base : gasMap[x][y];
                stack.add(new PIXEL(x, y));
            }
            // System.out.println("#" + x + " " + y);
        }
        return gasMap;
    }

    public int getDirection(int[][] map, int x, int y) {
        List<Integer> backDir = new ArrayList<>();
        int u = 0, r = 0;
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                u = -1;
                r = 0;
            }
            else if (i == 1) {
                u = 0;
                r = 1;
            }
            else if (i == 2) {
                u = 1;
                r = 0;
            }
            else if (i == 3) {
                u = 0;
                r = -1;
            }
            try {
                if (map[x + r][y + u] == 0) {
                    backDir.add(i);
                }
            } catch (Exception ignored) {
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
}
