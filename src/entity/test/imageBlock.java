package entity.test;

/**
 * 图像块
 */
public class imageBlock {

    /**
     * 图像块的起始位点
     */
    private final int x;
    private final int y;

    /**
     * 图像块宽高
     */
    private int width, height;

    /**
     * 图像块数据
     */
    private int[][] argbMatrix;

    public imageBlock(int[][] argbData, int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        argbMatrix = new int[width][height];
        for(int i = x;i < x + width;i ++){
            System.arraycopy(argbData[i], y, argbMatrix[i - x], 0, height);
        }
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
}
