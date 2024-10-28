package discard;

import service.ICalculateService;
import entity.EventPool;
import service.impl.ICalculateServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static threadPool.core.ThreadPoolReflectCore.getCombines;

public class ThreadPoolPaperService {
    int[][] data;
    double[] kernel;
    double[] param;
    double[][] fillKernel;
    int width, height;
    int threadCount;
    int threadCountLimit = 24;
    int blockSize = 240;
    EventPool[] ePools;
    Stack<Integer> eventIndex = new Stack<>();
    Stack<PaperBlurCalc> leisureThreads = new Stack<>();
    List<Thread> threadPool = new ArrayList<>();


    private final ICalculateService calcService = new ICalculateServiceImpl();

    public ThreadPoolPaperService(){

    }

    /**
     * 输入原始数据、卷积核、初始线程数量
     *
     * @param requestData
     * @param ConVKernel
     * @param MaxThreadCount
     */
    public ThreadPoolPaperService(int[][] requestData, double[][] ConVKernel, int MaxThreadCount) {
        this.data = requestData;
        this.width = requestData.length;
        this.height = requestData[0].length;
        this.fillKernel = ConVKernel;
        this.kernel = getKernel((ConVKernel.length - 1) / 2);
        this.threadCount = Math.min((int) Math.sqrt(kernel.length) + 3, MaxThreadCount);
        this.threadCountLimit = MaxThreadCount;
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

    public int[][] getData(){
        return data;
    }

    public double[] getIIRParam(int size){
        // 顺序为b0,b1,b2,b3,B
        double[] params = new double[5];
        double theta = Math.sqrt(Math.log(0.1) * -1.0 * 2 / Math.pow(-size, 2));
        double q;
        if(theta > 2.5d){
            q = 0.98711 * theta - 0.96330;
        }else{
            q = 3.97156 - 4.14554 * Math.sqrt(1 - 0.26891 * theta);
        }
        // 依次计算b0,b1,b2,b3,B
        params[0] = 1.57825 + (2.44413 * q) + (1.4281 * q * q) + (0.422205 * q * q * q);
        params[1] = (2.44413 * q) + (2.85619 * q * q) + (1.26661 * q * q * q);
        params[2] = -(1.4281 * q * q + 1.26661 * q * q * q);
        params[3] = 0.422205 * q * q * q;
        params[4] = 1 - ((params[1] + params[2] + params[3]) / params[0]);
        return params;
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
            leisureThreads.add(new PaperBlurCalc());
        }
    }

    public void start() {
        this.param = getIIRParam(kernel.length);
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
                    leisureThreads.push(new PaperBlurCalc());
                }
            }
            index.forEach(num -> {
                threadPool.remove(num);
            });
            initThread();
            if ((System.currentTimeMillis() - set) / 1000.0 > 2) {
                if (threadCount < threadCountLimit) {
                    threadCount++;
                    leisureThreads.push(new PaperBlurCalc());
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
        return getCombines(width, blockSize, height, ePools);
    }

    private void initThread() {
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
