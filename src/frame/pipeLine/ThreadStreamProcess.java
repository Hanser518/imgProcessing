package frame.pipeLine;

import entity.Image;
import frame.entity.Event;

import java.util.ArrayDeque;
import java.util.Queue;

import static frame.constant.PipeLineParam.*;

public class ThreadStreamProcess extends Thread {

    private volatile Queue<Event> pendingEventQueue = new ArrayDeque<>();
    private volatile Queue<Event> processingEventQueue = new ArrayDeque<>();


    public void insertTask(Class<? extends Event> eventClass, Image image, double[][] kernel, Integer threadThreshold) {
        try {
            long start = System.currentTimeMillis();
            Event event = eventClass.getConstructor(Image.class, double[][].class, Integer.class).newInstance(image, kernel, threadThreshold);
            System.out.println("TIME: \t" + (System.currentTimeMillis() - start));
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
            if (event != null) {
                if (AVAILABLE_THREAD > event.getThreadThreshold()) {
                    AVAILABLE_THREAD -= event.getThreadThreshold();
                    event.start();
                    processingEventQueue.add(event);
                    System.out.println("EVENT start");
                } else {
                    System.out.println("The available threads are insufficient. Procedure");
                    pendingEventQueue.add(event);
                }
            }
            for (Thread t : processingEventQueue) {
                if (!t.isAlive()) {
                    event = processingEventQueue.poll();
                    if (event != null) {
                        IMAGE_QUEUE.add(event.getResult());
                        AVAILABLE_THREAD += event.getThreadThreshold();
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
