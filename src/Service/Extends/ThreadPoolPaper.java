package Service.Extends;


import Service.Thread.PaperBlurCalc;
import Service.ThreadPoolService;

import java.util.Stack;

public class ThreadPoolPaper extends ThreadPoolService {
    Stack<PaperBlurCalc> leisureThreads = new Stack<>();

    public ThreadPoolPaper(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        super(requestData, ConVKernel, MaxThreadCount);
        initLeisureThread();
    }

    @Override
    protected void leisurePush() {
        leisureThreads.push(new PaperBlurCalc());
    }

    // 压入未激活的处理线程
    protected void initLeisureThread(){
        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new PaperBlurCalc());
        }
    }

    @Override
    protected void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                PaperBlurCalc cr = leisureThreads.pop();
                int index = eventIndex.pop();
                cr = new PaperBlurCalc(ePools[index], data, fillKernel);
                cr.setConvKernel(kernel);
                cr.setParam(param);
                Thread t = new Thread(cr);
                threadPool.add(t);
                t.start();
                System.out.print(ePools[index].index + "#");
            }else{
                break;
            }
        }
    }
}
