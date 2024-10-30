package threadPool.pool;


import threadPool.core.ThreadPoolCore;
import threadPool.thread.ConVStrange;

import java.util.Stack;

@Deprecated
public class ThreadPoolStrange extends ThreadPoolCore {
    Stack<ConVStrange> leisureThreads = new Stack<>();

    public ThreadPoolStrange(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        super(requestData, ConVKernel, MaxThreadCount);
        initLeisureThread();
        ConVStrange.setData(data);
        ConVStrange.setKernel(fillKernel);
        ConVStrange.setK();
    }

    @Override
    protected void leisurePush() {
        leisureThreads.push(new ConVStrange());
    }

    // 压入未激活的处理线程
    protected void initLeisureThread() {
        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new ConVStrange());
        }
    }

    @Override
    protected void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                ConVStrange cS = leisureThreads.pop();
                int index = eventIndex.pop();
                cS = new ConVStrange(ePools[index]);
                Thread t = new Thread(cS);
                threadPool.add(t);
                t.start();
                // System.out.print(ePools[index].index + "#");
            } else {
                break;
            }
        }
    }
}
