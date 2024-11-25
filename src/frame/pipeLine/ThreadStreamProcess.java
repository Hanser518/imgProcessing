package frame.pipeLine;

import entity.Image;
import frame.entity.Event;
import frame.entity.record.Local;

import java.util.ArrayDeque;
import java.util.Queue;

import static frame.constant.PipeLineParam.*;

public class ThreadStreamProcess extends Thread {

    private final Queue<Event> pendingEventQueue = new ArrayDeque<>();
    private final Queue<Event> processingEventQueue = new ArrayDeque<>();
    private final Queue<Event> processedEventQueue = new ArrayDeque<>();


    public void insertTask(Image image, double[][] kernel, Class<? extends Event> eventClass) {
        try {
            Event event = eventClass.getConstructor(Image.class, double[][].class).newInstance(image, kernel);
            pendingEventQueue.add(event);
        } catch (Exception e) {
            System.out.println("ThreadStreamProcess ERROR: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        while (ALIVE) {
            while (!pendingEventQueue.isEmpty() && processingEventQueue.size() < MAX_LINE_SIZE) {
                Event event = pendingEventQueue.poll();
                event.start();
                processingEventQueue.add(event);
            }
            for (Thread t : processingEventQueue) {
                if (!t.isAlive()) {
                    Event event = processingEventQueue.poll();
                    processedEventQueue.add(event);
                    if (event != null) {
                        imageQueue.add(event.getResult());
                    }
                }
            }
        }
    }
}
