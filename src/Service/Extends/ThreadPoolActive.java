package Service.Extends;

import Service.Thread.ActiveConfirm;
import Service.CORE.ThreadPoolCore;

import java.util.Arrays;
import java.util.Stack;

public class ThreadPoolActive extends ThreadPoolCore {
    Stack<ActiveConfirm> leisureThreads = new Stack<>();
    public ThreadPoolActive(int[][] requestData, double[][] ConVKernel, int searchRadius, int MaxThreadCount) {
        super(requestData, ConVKernel, MaxThreadCount);
        initLeisureThread();
        initKernel(searchRadius);
    }

    protected void initKernel(int radius){
        double[][] kernel = new double[radius * 2 + 1][radius * 2 + 1];
        for(int i = 0;i < radius * 2 + 1;i ++){
            Arrays.fill(kernel[i], 1);
        }
        this.fillKernel = kernel;
    }

    // 压入未激活的处理线程
    protected void initLeisureThread(){
        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new ActiveConfirm());
        }
    }

    @Override
    protected void leisurePush() {
        leisureThreads.push(new ActiveConfirm());
    }

    @Override
    protected void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                ActiveConfirm ac = leisureThreads.pop();
                int index = eventIndex.pop();
                ac = new ActiveConfirm(ePools[index], data, fillKernel, 32);
                Thread t = new Thread(ac);
                threadPool.add(t);
                t.start();
                System.out.print(ePools[index].index + "#");
            } else {
                break;
            }
        }
    }
}
