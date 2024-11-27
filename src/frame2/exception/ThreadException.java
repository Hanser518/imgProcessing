package frame2.exception;

import static frame2.constant.PipeLineParam.*;

public class ThreadException {

    public static void overflowException() {
        EXCEPTION_OVERFLOW_COUNT++;
        System.out.println("THREAD_EXCEPTION: OVERFLOW_" + EXCEPTION_OVERFLOW_COUNT);
        if (EXCEPTION_OVERFLOW_COUNT >= 3 && AVAILABLE_THREAD < MAX_THREAD_SIZE) {
            AVAILABLE_THREAD += 2;
            EXCEPTION_OVERFLOW_COUNT = 0;
        }
    }
}
