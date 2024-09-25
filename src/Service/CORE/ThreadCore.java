package Service.CORE;

import Entity.EventPool;

public abstract class ThreadCore implements Runnable {
    protected EventPool ep;
    protected static int[][] data;
    protected static double[][] kernel;

    public ThreadCore(){

    }

    public ThreadCore(EventPool ep){
        this.ep = ep;
    }

    public static void setData(int[][] data){
        ThreadCore.data = data;
    }

    public static void setKernel(double[][] kernel){
        ThreadCore.kernel = kernel;
    }

    public abstract int matrixCalc(int x, int y);

    @Override
    public void run() {
        for (int i = ep.sx; i < ep.sx + ep.width; i++) {
            for (int j = ep.sy; j < ep.sy + ep.height; j++) {
                ep.result[i - ep.sx][j - ep.sy] = matrixCalc(i, j);
            }
        }
        // System.out.print(ep.index + "|");
    }
}
