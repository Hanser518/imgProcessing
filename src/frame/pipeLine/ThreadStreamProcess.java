package frame.pipeLine;

import entity.Image;
import frame.entity.Event;

import java.util.ArrayDeque;
import java.util.Queue;

import static frame.constant.PipeLineParam.*;

public class ThreadStreamProcess extends Thread {

    public void insertTask(Class<? extends Event> eventClass, Image image, double[][] kernel, Integer threadThreshold) {
        try {
            long start = System.currentTimeMillis();
            Event event = eventClass.getConstructor(Image.class, double[][].class, Integer.class).newInstance(image, kernel, threadThreshold);
            System.out.println("\nTIME: \t" + (System.currentTimeMillis() - start));
            System.out.println("pendingEventQueue: \t" + pendingEventQueue.size());
            System.out.println("AVAILABLE_THREAD: \t" + AVAILABLE_THREAD);
            System.out.println("processingEventQueue: \t" + processingEventQueue.size());
            System.out.println("EventThreadThreshold: \t" + event.getThreadThreshold());
            System.out.println("PredictAvailableCount: \t" + (AVAILABLE_THREAD - event.getThreadThreshold()));
            System.out.println();
            pendingEventQueue.add(event);
        } catch (Exception e) {
            System.out.println("ThreadStreamProcess ERROR: " + e.getMessage());
        }
    }

    public void test() {
        System.out.println("INSERT pendingEventQueue " + pendingEventQueue.size() + "|" + pendingEventQueue.isEmpty());
    }

    @Override
    public void run() {
        while (PIPELINE_ALIVE) {
            Event event = pendingEventQueue.poll();
            // 尝试将事件压入处理队列
            if (event != null) {
                // 当存在充足可用线程时，将事件压入处理队列
                if (AVAILABLE_THREAD > event.getThreadThreshold()) {
                    AVAILABLE_THREAD -= event.getThreadThreshold();
                    event.start();
                    processingEventQueue.add(event);
                } else {
                    // System.out.println("The available threads are insufficient. Procedure");
                    System.out.print("...");
                    pendingEventQueue.add(event);
                }
            }
            // 查询对立状态，即使释放资源
            for (Thread t : processingEventQueue) {
                if (!t.isAlive()) {
                    event = processingEventQueue.poll();
                    if (event != null) {
                        IMAGE_QUEUE.add(event.getResult());
                        AVAILABLE_THREAD += event.getThreadThreshold();
                    }
                }
            }
            // 清除溢出事件
            if(pendingEventQueue.size() > 25){
                pendingEventQueue.remove();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
