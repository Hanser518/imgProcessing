package threadPool;


import threadPool.thread.PaperBlur;
import threadPool.core.ThreadPoolCore;

import java.util.Stack;

public class ThreadPoolPaper extends ThreadPoolCore {
    Stack<PaperBlur> leisureThreads = new Stack<>();

    public ThreadPoolPaper(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        super(requestData, ConVKernel, MaxThreadCount);
        initLeisureThread();
        PaperBlur.setData(data);
        PaperBlur.setKernel(fillKernel);
    }

    @Override
    protected void leisurePush() {
        leisureThreads.push(new PaperBlur());
    }

    // 压入未激活的处理线程
    protected void initLeisureThread() {

        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new PaperBlur());
        }
    }

    @Override
    protected void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                PaperBlur pb = leisureThreads.pop();
                int index = eventIndex.pop();
                pb = new PaperBlur(ePools[index]);
                Thread t = new Thread(pb);
                threadPool.add(t);
                t.start();
                // System.out.print(ePools[index].index + "#");
            } else {
                break;
            }
        }
    }
}
