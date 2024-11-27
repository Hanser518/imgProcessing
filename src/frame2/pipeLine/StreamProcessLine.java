package frame2.pipeLine;

import entity.Image;
import frame2.entity.Event;
import frame2.exception.ThreadException;

import java.util.ArrayDeque;
import java.util.Queue;

import static frame2.constant.PipeLineParam.*;

public class StreamProcessLine extends Thread {

    public void insertTask(Class<? extends Event> eventClass, Image image, double[][] kernel, Integer threadThreshold) {
        try {
            Event event = eventClass.getConstructor(Image.class, double[][].class, Integer.class).newInstance(image, kernel, threadThreshold);
            loadToPendingQueue(event);
        } catch (Exception e) {
            System.out.println("StreamProcessLine ERROR: " + e.getMessage());
        }
    }

    public void insertEvent(Event event) {
        loadToPendingQueue(event);
    }

    private void loadToPendingQueue(Event event) {
        System.out.println();
        System.out.println("pendingEventQueue: \t" + pendingEventQueue.size());
        System.out.println("AVAILABLE_THREAD: \t" + AVAILABLE_THREAD);
        System.out.println("processingEventQueue: \t" + processingEventQueue.size());
        System.out.println("EventThreadThreshold: \t" + event.getThreadThreshold());
        System.out.println("PredictAvailableCount: \t" + (AVAILABLE_THREAD - event.getThreadThreshold()));
        System.out.println();
        pendingEventQueue.add(event);
    }

    public void test() {
        System.out.println("INSERT pendingEventQueue " + pendingEventQueue.size() + "|" + pendingEventQueue.isEmpty());
    }

    private void transitionQueueState() {
        Queue<Event> cache = new ArrayDeque<>();
        do {
            Event event = pendingEventQueue.poll();
            // 尝试将事件压入处理队列
            // 当存在充足可用线程时，将事件压入处理队列
            if (event != null) {
                // 当存在充足可用线程时，将事件压入处理队列
                if (AVAILABLE_THREAD > event.getThreadThreshold()) {
                    AVAILABLE_THREAD -= event.getThreadThreshold();
                    event.start();
                    processingEventQueue.add(event);
                } else {
                    // System.out.println("The available threads are insufficient. Procedure");
                    System.out.print("...");
                    cache.add(event);
                }
            }
        } while (!pendingEventQueue.isEmpty());
        pendingEventQueue.addAll(cache);
    }

    // 查询队列状态，及时释放资源
    private void checkQueueState() {
        for (Thread t : processingEventQueue) {
            if (!t.isAlive()) {
                Event event = processingEventQueue.poll();
                if (event != null) {
                    IMAGE_QUEUE.add(event.getResult());
                    AVAILABLE_THREAD += event.getThreadThreshold();
                }
            }
        }
        // 触发溢出事件
        if (pendingEventQueue.size() > MAX_PENDING_SIZE) {
            pendingEventQueue.remove();
            ThreadException.overflowException();
        }
    }

    @Override
    public void run() {
        while (PIPELINE_ALIVE) {
            transitionQueueState();
            checkQueueState();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
