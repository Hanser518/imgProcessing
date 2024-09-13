package MultiThread.controller;

import MultiThread.Thread.TConcurrentRequest;
import MultiThread.entity.EventPool;
import Service.ICalculateService;
import Service.Impl.ICalculateServiceImpl;

import java.util.*;

public class ConcurrentRequestController {
    int[][] data;
    double[][] fillKernel;
    double[] calcKernel;
    int width, height;
    int threadCount;
    int threadCountLimit = 32;
    int blockSize = 300;
    Stack<EventPool> events = new Stack<>();
    Stack<TConcurrentRequest> cache = new Stack<>();
    Stack<TConcurrentRequest> leisureThreads = new Stack<>();
    List<Thread> threadPool = new ArrayList<>();
    boolean listLock = false;


    private final ICalculateService calcService = new ICalculateServiceImpl();

    /**
     * 输入原始数据、卷积核、初始线程数量
     *
     * @param requestData
     * @param ConVKernel
     * @param InitThreadCount
     */
    public ConcurrentRequestController(int[][] requestData, double[][] ConVKernel, int InitThreadCount) {
        this.data = requestData;
        this.width = requestData.length;
        this.height = requestData[0].length;
        this.fillKernel = ConVKernel;
        this.calcKernel = getKernel((ConVKernel.length - 1) / 2);
        int tc = (int) Math.sqrt(Math.max(fillKernel.length, fillKernel[0].length)) + 3;
        this.threadCount = Math.max(tc, InitThreadCount);
        init();
    }

    public double[] getKernel(int size){
        double theta = Math.sqrt(Math.log(0.1) * -1.0 * 2 / Math.pow(-size, 2));
        int kernelSize = size * 2 + 1;
        double[][] gasKernel = new double[kernelSize][kernelSize];
        double[] w = new double[kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            w[i] = Math.exp(-1.0 * (i - size) * (i - size) / 2 * theta * theta);
        }
        return w;
    }

    /**
     * 请求初始化
     */
    private void init() {
        // 对图像进行填充处理，此时数据矩阵长宽发生改变，原矩阵长宽存储于width和height中
        data = calcService.pixFill(data, fillKernel);
        // 对矩阵数据进行任务分配及切分，每200*200为一个事件组，同时生成对应点位的标识符
        int w = data.length;
        int h = data[0].length;
        int wCount = width % blockSize == 0 ? width / blockSize : width / blockSize + 1;
        int hCount = height % blockSize == 0 ? height / blockSize : height / blockSize + 1;
        for (int i = 0; i < wCount; i++) {
            for(int j = 0;j < hCount;j ++){
                int wStep = i * blockSize + blockSize < width ? blockSize : width - i * blockSize;
                int hStep = j * blockSize + blockSize < height ? blockSize : height - j * blockSize;
                EventPool ep = new EventPool(i * blockSize, j * blockSize, wStep, hStep);
                ep.setIndex(i * hCount + j);
                events.add(ep);
            }
        }
        // 压入未激活的处理线程
        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new TConcurrentRequest());
        }
    }

    public boolean start() {
        initThread();
        long set = System.currentTimeMillis();
        while (!events.isEmpty()) {
            List<Thread> index = new ArrayList<>();
            // 遍历threadPool
            for (int i = 0; i < threadPool.size(); i++) {
                Thread t = threadPool.get(i);
                if (!t.isAlive()) {
                    index.add(t);
                    leisureThreads.push(new TConcurrentRequest());
                }
            }
            index.forEach(num -> {
                threadPool.remove(num);
            });
            initThread();
            if ((System.currentTimeMillis() - set) / 1000.0 > 1) {
                if (threadCount < threadCountLimit) {
                    threadCount++;
                    leisureThreads.push(new TConcurrentRequest());
                    System.out.print(threadCount + "&");
                }
                set = System.currentTimeMillis();
                System.out.print("\n@" + events.size() + ".");
            }
            // System.out.println("event:" + events.size() + "|leisure:" + leisureThreads.size() + "|thread:" + threadPool.size());
        }
        int limit = threadPool.size();
        while (true) {
            int alive = 0;
            for (int i = 0; i < threadPool.size(); i++) {
                Thread t = threadPool.get(i);
                if (!t.isAlive()) {
                    alive++;
                }
            }
            if (limit == alive) break;
        }
        System.out.println("ThreadCount:" + threadCount);
        return true;
    }

    private void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!events.isEmpty()) {
                TConcurrentRequest cr = leisureThreads.pop();
                EventPool en = events.pop();
                cr = new TConcurrentRequest(en, data, calcKernel);
                Thread t = new Thread(cr);
                threadPool.add(t);
                t.start();
                System.out.print(en.index + "#");
            }
        }
    }

}
