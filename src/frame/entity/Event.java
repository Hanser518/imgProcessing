package frame.entity;

import entity.Image;
import frame.entity.record.Local;

import java.util.*;

import static frame.constant.EventParam.*;

public abstract class Event extends Thread {
    /**
     * 原始数据
     */
    protected int[][] data;

    /**
     * 处理结果
     */
    protected int[][] result;

    /**
     * 原始宽高
     */
    protected int width, height;

    /**
     * 卷积核
     */
    protected double[][] kernel;

    /**
     * 处理标识点
     */
    protected Queue<Local> localQueue = new ArrayDeque<>();

    /**
     * 最低启动线程
     */
    protected Integer threadThreshold;

    /**
     * 可用线程数
     */
    protected int availableThreadCount = 0;

    /**
     * 线程池
     */
    protected List<Thread> threadPool = new ArrayList<>();

    /**
     * 初始化
     */
    public Event(Image img, double[][] kernel, Integer threadThreshold) {
        data = Image.fillImageEdge(img, kernel.length, kernel[0].length).getArgbMatrix();
        result = new int[img.getWidth()][img.getHeight()];
        width = img.getWidth();
        height = img.getHeight();
        this.kernel = kernel;
        for (int i = 0; i < width / MAX_EVENT_SIZE + 1; i++) {
            for (int j = 0; j < height / MAX_EVENT_SIZE + 1; j++) {
                int wStep = i * MAX_EVENT_SIZE + MAX_EVENT_SIZE < width ? MAX_EVENT_SIZE : width - i * MAX_EVENT_SIZE;
                int hStep = j * MAX_EVENT_SIZE + MAX_EVENT_SIZE < height ? MAX_EVENT_SIZE : height - j * MAX_EVENT_SIZE;
                Local local = new Local(i * MAX_EVENT_SIZE, j * MAX_EVENT_SIZE, wStep, hStep);
                localQueue.add(local);
            }
        }
        this.threadThreshold = threadThreshold;
        this.availableThreadCount = threadThreshold;
    }

    public Integer getThreadThreshold() {
        return threadThreshold;
    }

    public void setAvailableThreadCount(Integer value) {
        this.availableThreadCount = value == null ? this.threadThreshold : value;
    }

    protected void initThread() {
        for (int i = 0; i < availableThreadCount; i++) {
            threadPool.add(null);
        }
    }

    /**
     * 主方法
     */
    protected abstract void function(Local local);

    @Override
    public void run() {
        long constTime = System.currentTimeMillis();
        initThread();
        System.out.println(localQueue.size());
        while (true) {
            for (int i = 0; i < availableThreadCount; i++) {
                Local local = localQueue.poll();
                if (local == null) {
                    break;
                }
                Thread t = threadPool.get(i);
                if (t == null || !t.isAlive()) {
                    t = new Thread(() -> function(local));
                    t.start();
                    threadPool.set(i, t);
                }else{
                    localQueue.add(local);
                }
            }
            if (localQueue.isEmpty()) {
                int count = threadPool.size();
                for(Thread t : threadPool){
                    if(!t.isAlive()){
                        count --;
                    }
                }
                if(count == 0){
                    break;
                }
            }
        }
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("Event take: " + (System.currentTimeMillis() - constTime));
    }

    public Image getResult() {
        return new Image(result);
    }
}
