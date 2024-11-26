package frame.constant;

import entity.Image;
import frame.entity.Event;

import java.util.ArrayDeque;
import java.util.Queue;

public class PipeLineParam {

    public static volatile int MAX_LINE_SIZE = 4;

    public static volatile int MAX_PROCESSING_SIZE = 12;

    public static int AVAILABLE_THREAD = 10;

    public static volatile Queue<Event> pendingEventQueue = new ArrayDeque<>();

    public static volatile Queue<Event> processingEventQueue = new ArrayDeque<>();

    public static volatile Queue<Image> IMAGE_QUEUE = new ArrayDeque<>();

    public static volatile boolean PIPELINE_ALIVE = true;
}
