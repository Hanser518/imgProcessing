package Service.Extends;


import Service.Thread.ConVCalc;
import Service.CORE.ThreadPoolCore;

import java.util.Stack;

public class ThreadPoolConV extends ThreadPoolCore {
    Stack<ConVCalc> leisureThreads = new Stack<>();

    public ThreadPoolConV(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        super(requestData, ConVKernel, MaxThreadCount);
        initLeisureThread();
    }

    @Override
    protected void leisurePush() {
        leisureThreads.push(new ConVCalc());
    }

    // 压入未激活的处理线程
    protected void initLeisureThread(){
        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new ConVCalc());
        }
    }

    @Override
    protected void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                ConVCalc cc = leisureThreads.pop();
                int index = eventIndex.pop();
                cc = new ConVCalc(ePools[index], data, fillKernel);
                Thread t = new Thread(cc);
                threadPool.add(t);
                t.start();
                System.out.print(ePools[index].index + "#");
            } else {
                break;
            }
        }
    }
}
