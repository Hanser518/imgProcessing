package frame.constant;

import entity.Image;

import java.util.ArrayDeque;
import java.util.Queue;

public class PipeLineParam {

    public static final int MAX_LINE_SIZE = 4;

    public static final int MAX_PROCESSING_SIZE = 10;

    public static final Queue<Image> imageQueue = new ArrayDeque<>();

    public static boolean ALIVE = true;
}
