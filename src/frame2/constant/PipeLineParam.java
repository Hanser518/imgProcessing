package frame2.constant;

import entity.Image;
import frame2.entity.Event;

import java.util.ArrayDeque;
import java.util.Queue;

public class PipeLineParam {

    public static volatile int MAX_LINE_SIZE = 4;

    public static volatile int MAX_PROCESSING_SIZE = 12;

    public static int AVAILABLE_THREAD = 10;

    public static int MAX_THREAD_SIZE = 16;

    public static int MAX_PENDING_SIZE = 10;

    public static int EXCEPTION_OVERFLOW_COUNT = 0;

    public static volatile Queue<Event> pendingEventQueue = new ArrayDeque<>();

    public static volatile Queue<Event> processingEventQueue = new ArrayDeque<>();

    public static volatile Queue<Image> IMAGE_QUEUE = new ArrayDeque<>();

    public static volatile boolean PIPELINE_ALIVE = true;
}
