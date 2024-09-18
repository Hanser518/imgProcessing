package Service;

import Entity.EventPool;
import Service.Impl.ICalculateServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ThreadPoolService {
    public int[][] data;
    public double[] kernel;
    public double[] param;
    public double[][] fillKernel;
    public int width, height;
    public int threadCount;
    public int threadCountLimit = 24;
    public int blockSize = 240;
    public EventPool[] ePools;
    public Stack<Integer> eventIndex = new Stack<>();
    public List<Thread> threadPool = new ArrayList<>();

    private final ICalculateService calcService = new ICalculateServiceImpl();

    public ThreadPoolService(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        this.data = requestData;
        this.width = requestData.length;
        this.height = requestData[0].length;
        this.fillKernel = ConVKernel;
        this.kernel = getKernel((ConVKernel.length - 1) / 2);
        this.threadCountLimit = MaxThreadCount < 0 ? 999 : MaxThreadCount;
        this.threadCount = Math.min((int) Math.sqrt(kernel.length) + 3, threadCountLimit);
        init();
    }

    private void init() {
        blockSize = (int) (800 * 800 / Math.sqrt(width * height));
        // System.out.println(blockSize);
        // 对图像进行填充处理，此时数据矩阵长宽发生改变，原矩阵长宽存储于width和height中
        data = calcService.pixFill(data, fillKernel);
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
                // int hStep = j * blockSize + blockSize < height ? blockSize : height - j * blockSize;
                EventPool ep = new EventPool(i * blockSize, j * blockSize, wStep, hStep);
                int index = i * hCount + j;
                ep.setIndex(index);
                ePools[i * hCount + j] = ep;
                eventIndex.add(index);
            }
        }
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

    public int[][] getData(){
        return data;
    }

    public void start(){
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
                    leisurePush();
                }
            }
            index.forEach(num -> {
                threadPool.remove(num);
            });
            initThread();
            if ((System.currentTimeMillis() - set) / 1000.0 > 2) {
                if (threadCount < threadCountLimit) {
                    threadCount++;
                    leisurePush();
                    System.out.print(threadCount + "&");
                }
                set = System.currentTimeMillis();
                System.out.print("\n@" + eventIndex.size() + ".");
                System.out.print("\n&" + threadPool.size()+ ".");

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

    protected abstract void leisurePush();

    protected int[][] combineData(){
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

    protected abstract void initThread();
}
