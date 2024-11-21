package threadPool.core;

import entity.EventPool;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 最好不要动，因为我也看不懂了
 */
public class ThreadPoolReflectCore {
    protected int[][] data;     // 待处理数据
    protected double[][] kernel;// 卷积核
    protected int width, height;// 数据宽高
    protected int threadCount;  // 当前线程数量
    protected int threadCountLimit = 24;    // 最大线程数量
    protected int blockSize = 240;  // 单个事务块的最大单边长度

    protected Class<?> classOfThread; // 线程种类
    protected Object thread;
    protected Constructor<?> constructorOfThread;

    protected EventPool[] ePools;   // 事务池
    protected Stack<Integer> eventIndex = new Stack<>();    // 待处理事务序号列表
    protected List<Thread> threadPool = new ArrayList<>();  // 线程池
    protected Stack<Object> leisure = new Stack<>();        // 空闲线程

    public ThreadPoolReflectCore(int[][] requestData, double[][] ConVKernel, int MaxThreadCount, Object threadItem, boolean... fill) throws Exception {
        // 对图像进行填充处理，此时数据矩阵长宽发生改变，原矩阵长宽存储于width和height中
        ICalculateService calcService = new ICalculateServiceImpl();
        if(fill.length > 0){
            if(fill[0]){
                this.data = calcService.pixFill(requestData, ConVKernel);
            }else{
                this.data = requestData;
            }
        }else{
            this.data = calcService.pixFill(requestData, ConVKernel);
        }
        this.width = requestData.length;
        this.height = requestData[0].length;
        this.kernel = ConVKernel;
        this.threadCountLimit = MaxThreadCount < 0 ? 999 : Math.min(MaxThreadCount, 12);
        this.threadCount = Math.min((int) Math.sqrt(kernel.length) + 3, threadCountLimit);
        classOfThread = threadItem.getClass();
        initEvent();
        initLeisure();
        thread = threadItem;
        // 填充线程数据、卷积核
        Method setData = classOfThread.getMethod("setData", int[][].class);
        if (!setData.isDefault())
            setData.invoke(this.thread, (Object) data);
        Method setKernel = classOfThread.getMethod("setKernel", double[][].class);
        if (!setKernel.isDefault())
            setKernel.invoke(this.thread, (Object) kernel);
    }

    public void setThreshold(int threshold) throws Exception {
        try {
            Method set = classOfThread.getMethod("setThreshold", int.class);
            if (set.isDefault())
                System.out.println("Thread do not contain method: setThreshold");
            else
                set.invoke(thread, threshold);
        } catch (Exception ignored) {
        }
    }

    public void setFocusData(int[][] data) {
        try {
            Method set = classOfThread.getMethod("setFocusData", int[][].class);
            if (set.isDefault())
                System.out.println("Thread do not contain method: setFocusData");
            else
                set.invoke(thread, (Object) data);
        } catch (Exception ignored) {
        }
    }

    public void customMethod(String methodName, Object params) throws Exception {
        Method set = classOfThread.getMethod(methodName, params.getClass());
        set.invoke(thread, params);
    }

    private void initEvent() {
        // 计算blockSize
        int totalArea = width * height;
        int totalBlock = totalArea / 6400000 * (int) Math.sqrt(kernel.length) + 1;
        blockSize = Math.min(width, height) / totalBlock + 1;
        while ((width / blockSize) * (height / blockSize) < threadCountLimit) {
            blockSize -= 10;
        }
        // 对矩阵数据进行任务分配及切分，每最大blockSize*blockSize为一个事件组，同时生成对应点位的标识符
        int wCount = width % blockSize == 0 ? width / blockSize : width / blockSize + 1;
        int hCount = height % blockSize == 0 ? height / blockSize : height / blockSize + 1;
        ePools = new EventPool[wCount * hCount];
        for (int i = 0; i < wCount; i++) {
            for (int j = 0; j < hCount; j++) {
                int wStep = i * blockSize + blockSize < width ? blockSize : width - i * blockSize;
                int hStep = j * blockSize + blockSize < height ? blockSize : height - j * blockSize;
                EventPool ep = new EventPool(i * blockSize, j * blockSize, wStep, hStep);
                int index = i * hCount + j;
                ep.setIndex(index);
                ePools[i * hCount + j] = ep;
                eventIndex.add(index);
            }
        }
    }

    private void initLeisure() throws Exception {
        constructorOfThread = classOfThread.getConstructor();
        for (int i = 0; i < threadCount; i++) {
            leisure.push(constructorOfThread.newInstance());
        }
    }

    public int[][] getData() {
        return data;
    }

    public void start() throws Exception {
        initThread();
        long set = System.currentTimeMillis();
        long outSet = System.currentTimeMillis();
        while (!eventIndex.isEmpty()) {
            List<Thread> index = new ArrayList<>();
            // 遍历threadPool
            for (int i = 0; i < threadPool.size(); i++) {
                Thread t = threadPool.get(i);
                if (!t.isAlive()) {
                    // threadPool.remove(t);
                    index.add(t);
                    leisure.push(constructorOfThread.newInstance());
                }
            }
            index.forEach(num -> {
                threadPool.remove(num);
            });
            initThread();
            if ((System.currentTimeMillis() - set) / 1000.0 > 2) {
                if (threadCount < threadCountLimit) {
                    threadCount++;
                    leisure.push(constructorOfThread.newInstance());
                }
            }
            if ((System.currentTimeMillis() - outSet) / 100.0 > 1) {
                outSet = System.currentTimeMillis();
                System.out.printf("\r@%2d %2.4f percent", threadCount, (1 - (double) eventIndex.size() / ePools.length));
            }
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
            if ((System.currentTimeMillis() - outSet) / 100.0 > 1) {
                outSet = System.currentTimeMillis();
                System.out.printf("\r@ %2.4f percent", (1 - (double) eventIndex.size() / ePools.length));
            }
        }
        data = combineData();
        System.out.println("ThreadCount:" + threadCount + ", EventPool:" + ePools.length);

    }

    protected int[][] combineData() {
        return getCombines(width, blockSize, height, ePools);
    }

    public static int[][] getCombines(int width, int blockSize, int height, EventPool[] ePools) {
        int wCount = width % blockSize == 0 ? width / blockSize : width / blockSize + 1;
        int hCount = height % blockSize == 0 ? height / blockSize : height / blockSize + 1;
        int[][] result = new int[width][height];
        for (int i = 0; i < wCount; i++) {
            for (int j = 0; j < hCount; j++) {
                int[][] ePool = ePools[i * hCount + j].result;
                for (int x = 0; x < ePool.length; x++) {
                    System.arraycopy(ePool[x], 0, result[x + i * blockSize], j * blockSize, ePool[0].length);
                }
            }
        }
        return result;
    }

    protected void initThread() throws Exception {
        while (!leisure.isEmpty()) {
            if (!eventIndex.isEmpty()) {
                leisure.pop();
                int index = eventIndex.pop();
                thread = constructorOfThread.newInstance();
                Method setEvent = classOfThread.getMethod("setEvent", EventPool.class);
                setEvent.invoke(thread, ePools[index]);
                Thread next = new Thread((Runnable) thread);
                threadPool.add(next);
                next.start();
            } else {
                break;
            }
        }
    }
}
