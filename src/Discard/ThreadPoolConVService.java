package Discard;

import Service.ICalculateService;
import Service.Extends.Thread.ConVCalc;
import Entity.EventPool;
import Service.Impl.ICalculateServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ThreadPoolConVService {
    int[][] data;
    double[][] kernel;
    int width, height;
    int threadCount;
    int threadCountLimit = 24;
    int blockSize = 240;
    EventPool[] ePools;
    Stack<Integer> eventIndex = new Stack<>();
    Stack<ConVCalc> leisureThreads = new Stack<>();
    List<Thread> threadPool = new ArrayList<>();

    private final ICalculateService calcService = new ICalculateServiceImpl();

    public ThreadPoolConVService(){

    }

    /**
     * 输入原始数据、卷积核、初始线程数量
     *
     * @param requestData
     * @param ConVKernel
     * @param MaxThreadCount
     */
    public ThreadPoolConVService(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        this.data = requestData;
        this.width = requestData.length;
        this.height = requestData[0].length;
        this.kernel = ConVKernel;
        this.threadCountLimit = MaxThreadCount < 1 ? 999 : MaxThreadCount;
        this.threadCount = Math.min((int) Math.sqrt(kernel.length) + 3, this.threadCountLimit);

        init();
    }

    public int[][] getData(){
        return data;
    }

    /**
     * 请求初始化
     */
    private void init() {
        // 对图像进行填充处理，此时数据矩阵长宽发生改变，原矩阵长宽存储于width和height中
        data = calcService.pixFill(data, kernel);
        // 对矩阵数据进行任务分配及切分，每200*200为一个事件组，同时生成对应点位的标识符
        int w = data.length;
        int h = data[0].length;
        int wCount = width % blockSize == 0 ? width / blockSize : width / blockSize + 1;
        int hCount = height % blockSize == 0 ? height / blockSize : height / blockSize + 1;
        ePools = new EventPool[wCount * hCount];
        for (int i = 0; i < wCount; i++) {
            for(int j = 0;j < hCount;j ++){
                int wStep = i * blockSize + blockSize < width ? blockSize : width - i * blockSize;
                int hStep = j * blockSize + blockSize < height ? blockSize : height - j * blockSize;
                EventPool ep = new EventPool(i * blockSize, j * blockSize, wStep, hStep);
                int index = i * hCount + j;
                ep.setIndex(index);
                ePools[i * hCount + j] = ep;
                eventIndex.add(index);
            }
        }
        // 压入未激活的处理线程
        for (int i = 0; i < threadCount; i++) {
            leisureThreads.add(new ConVCalc());
        }
    }

    public void start() {
        initThread();
        long set = System.currentTimeMillis();
        while (!eventIndex.isEmpty()) {
            List<Thread> index = new ArrayList<>();
            // 遍历threadPool
            for (int i = 0; i < threadPool.size(); i++) {
                Thread t = threadPool.get(i);
                if (!t.isAlive()) {
                    // threadPool.remove(t);
                    index.add(t);
                    leisureThreads.push(new ConVCalc());
                }
            }
            index.forEach(num -> {
                threadPool.remove(num);
            });
            initThread();
            if ((System.currentTimeMillis() - set) / 1000.0 > 2) {
                if (threadCount < threadCountLimit) {
                    threadCount++;
                    leisureThreads.push(new ConVCalc());
                    System.out.print(threadCount + "&");
                }
                set = System.currentTimeMillis();
                System.out.print("\n@" + eventIndex.size() + ".");
            }
            // System.out.println("event:" + eventIndex.size() + "|leisure:" + leisureThreads.size() + "|thread:" + threadPool.size());
        }
        System.out.println("event empty now");
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
        System.out.println();
        data = combineData();
        System.out.println("ThreadCount:" + threadCount);
    }

    private int[][] combineData(){
        int wCount = width % blockSize == 0 ? width / blockSize : width / blockSize + 1;
        int hCount = height % blockSize == 0 ? height / blockSize : height / blockSize + 1;
        int[][] result = new int[width][height];
        for(int i = 0;i < wCount;i ++){
            for(int j = 0;j < hCount;j ++){
                int[][] ePool = ePools[i * hCount + j].result;
                for(int x = 0;x < ePool.length;x ++){
                    for(int y = 0;y < ePool[0].length;y ++){
                        result[x + i * blockSize][y + j * blockSize] = ePool[x][y];
                    }
                }
            }
        }
        return result;
    }

    private void initThread() {
        while (!leisureThreads.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                ConVCalc cc = leisureThreads.pop();
                int index = eventIndex.pop();
                cc = new ConVCalc(ePools[index]);
                Thread t = new Thread(cc);
                threadPool.add(t);
                t.start();
                System.out.print(ePools[index].index + "#");
            }else{
                break;
            }
        }
    }

}
