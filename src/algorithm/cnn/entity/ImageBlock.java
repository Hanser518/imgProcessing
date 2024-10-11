package algorithm.cnn.entity;

import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

/**
 * 图像块
 */
public class ImageBlock {

    /**
     * 图像块的起始位点
     */
    private final int x;
    private final int y;

    /**
     * 图像原始宽高
     */
    private int rawWidth, rawHeight;

    /**
     * 图像块宽高
     */
    private int width, height;

    /**
     * 图像块数据
     */
    private int[][] argbMatrix;

    private int complexity;

    public ImageBlock(int[][] argbData, int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rawWidth = argbData.length;
        this.rawHeight = argbData[0].length;
        argbMatrix = new int[width][height];
        for(int i = x;i < x + width;i ++){
            System.arraycopy(argbData[i], y, argbMatrix[i - x], 0, height);
        }
        complexity = complexityDetection();
    }

    public static int complexityDetection(ImageBlock ib){
        // int[][] argbMatrix = ib.getArgbMatrix();
        return (int) (Math.random() * 5);
    }

    private int complexityDetection(){
        double[][] kernel = new double[][]{{3, -1, 0}, {-1, -2, -1}, {0, -1, 3}};
        int activeCount = 0;
        ICalculateService calcServ = new ICalculateServiceImpl();
        for(int i = 0;i < width - 2; i++){
            for(int j = 0;j < height - 2;j ++){
                int convResult = calcServ.picMarCalc(argbMatrix, kernel, i, j, true);
                double value = ((convResult >> 16) & 0xFF) * 0.29 + ((convResult >> 8) & 0xFF) * 0.59 + (convResult & 0xFF) * 0.12;
                if(value > 16){
                    activeCount ++;
                }
            }
        }

        return activeCount * 10 / (width *  height);
    }

    public int size(){
        return this.width * this.height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRawWidth() {
        return rawWidth;
    }

    public int getRawHeight() {
        return rawHeight;
    }

    public int[][] getArgbMatrix(){
        return this.argbMatrix;
    }

    public int getComplexity() {
        return complexity;
    }
}
