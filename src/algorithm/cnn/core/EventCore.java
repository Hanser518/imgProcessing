package algorithm.cnn.core;

public abstract class EventCore implements Runnable{
    protected int[][] data;
    protected int[][] result;
    protected double[][] kernel;
    protected int step = 1;

    public EventCore() {
    }

    public abstract void setData(int[][] data);

    public abstract void setKernel(double[][] kernel);

    public abstract void setStep(int step);

    public abstract void func();

    public int[][] getResult() {
        return result;
    }

    @Override
    public void run() {
        func();
    }
}
