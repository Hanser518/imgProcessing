package algorithm.cnn.core;

// 批量多线程处理输入图形

import entity.Image2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ThreadPoolCenter extends Thread{
    protected List<? extends EventCore> eventList;    // ⌛待处理事件列表
    protected int threadCount;  // 当前线程数量
    protected int threadCountLimit = 24;    // 最大线程数量


    protected List<Thread> threadPool = new ArrayList<>();  // 线程池
    protected List<? extends EventCore> resultList;    // ⌛待处理事件列表


    public ThreadPoolCenter(List<? extends Image2> imgList, double[][] kernel,
                            Class<? extends EventCore> eventClass,
                            Integer maxThreads, Boolean fillImage) {
        List<EventCore> eventList = new ArrayList<>();
        try{
            Constructor<? extends EventCore> constructorOfEvent = eventClass.getConstructor();
            Field filedData = eventClass.getDeclaredField("data");
            Field filedKernel = eventClass.getDeclaredField("kernel");
            for(Image2 img : imgList){
                EventCore en = constructorOfEvent.newInstance();
                if(fillImage) {
                    img = Image2.fillImageEdge(img, kernel.length / 2, kernel[0].length / 2);
                }
                filedData.set(en, img.getArgbMatrix());
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

    public <T extends Image2> ThreadPoolCenter(T img, Class<? extends EventCore> eventClass, double[][] kernel, Boolean fillImage){

        // 判断对图像进行填充
        if(fillImage == null || fillImage){
            img = Image2.fillImageEdge(img, kernel.length / 2, kernel[0].length / 2);
        }


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

    public List<? extends EventCore> getResultList(){
        return resultList;
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
        resultList = eventList;
    }
}
