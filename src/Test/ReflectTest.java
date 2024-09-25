package Test;

import Service.ThreadPool.Thread.PaperBlur;

public class ReflectTest extends ThreadPoolReflectCore{
    public ReflectTest(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) throws Exception {
        super(requestData, ConVKernel, MaxThreadCount, new PaperBlur());
    }
}
