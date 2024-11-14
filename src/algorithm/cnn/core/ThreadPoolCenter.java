package algorithm.cnn.core;

// 批量多线程处理输入图形

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ThreadPoolCenter extends Thread{
    protected List<? extends EventCore> eventList;    // ⌛待处理事件列表
    protected int threadCount;  // 当前线程数量
    protected int threadCountLimit = 24;    // 最大线程数量


    protected List<Thread> threadPool = new ArrayList<>();  // 线程池
    protected List<EventCore> resultList = new ArrayList<>();   //


    public ThreadPoolCenter(List<? extends ImageCore> imgList, double[][] kernel,
                            Class<? extends EventCore> eventClass,
                            Integer maxThreads, Boolean fillImage) {
        List eventList = new ArrayList<>();
        try{
            Constructor<? extends EventCore> constructorOfEvent = eventClass.getConstructor();
            Field filedData = eventClass.getDeclaredField("data");
            Field filedKernel = eventClass.getDeclaredField("kernel");
            for(ImageCore img : imgList){
                EventCore en = constructorOfEvent.newInstance();
                if(fillImage) {
                    img = ImageCore.fillImageEdge(img, kernel.length / 2, kernel[0].length / 2);
                }
                filedData.set(en, img.argbMatrix);
                filedKernel.set(en, kernel);
                eventList.add(en);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.eventList = eventList;
        initThreads(maxThreads);
    }

    public ThreadPoolCenter(List<? extends EventCore> eventList, Integer maxThreads){
        this.eventList = eventList;
        initThreads(maxThreads);
    }

    private void initThreads(Integer maxThreads){
        for(EventCore ec : eventList){
            Thread t = new Thread(ec);
            threadPool.add(t);
        }
        threadCountLimit = Math.max(threadCountLimit, maxThreads);
        threadCount = 0;
    }

    public List<? extends EventCore> getEventList(){
        return eventList;
    }

    @Override
    public void run(){
        do {
            for(Thread t : threadPool){
                if(t.getState().toString().equals("NEW") && threadCount < threadCountLimit){
                    threadCount ++;
                    // System.out.println("append thread");
                    t.start();
                }
                if(t.getState().toString().equals("TERMINATED")){
                    // System.out.println("remove thread");
                    threadCount --;
                }
            }
            // System.out.println(threadCount);
        }while (threadCount > 0);
    }
}
