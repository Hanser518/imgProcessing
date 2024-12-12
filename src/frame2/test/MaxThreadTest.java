package frame2.test;

import entity.Image;
import frame2.constant.PipeLineParam;
import frame2.entity.Event;
import frame2.entity.base.AbstractEventBlur;
import service.ICalculateService;
import service.impl.ICalculateServiceImpl;
import threadPool.core.ThreadPoolReflectCore;

import java.util.ArrayList;
import java.util.List;

public class MaxThreadTest {

    private final ICalculateService calcService = new ICalculateServiceImpl();

    /**
     * 基准处理耗时
     */
    private int baseInterval;

    /**
     * 基准处理事件
     */
    private Event baseEvent = null;

    /**
     * 标准处理图像
     */
    private Image baseImage = null;

    /**
     * 标准卷积核
     */
    private double[][] baseKernel = null;

    public static void main(String[] args) {
        new MaxThreadTest();
    }

    public MaxThreadTest() {
        int rgbValue = (255 << 24) | (187 << 16) | (104 << 8) | 54;
        baseKernel = calcService.getGasKernel(1);
        baseImage = new Image(400, 400, rgbValue);
        baseEvent = new AbstractEventBlur(baseImage, baseKernel, 1);
        updateBaseInterval();
        updateMaxThreadCount();
    }

    private void updateBaseInterval() {
        long set0 = System.currentTimeMillis();
        baseEvent.start();
        while (baseEvent.isAlive()) ;
        baseInterval = (int) (System.currentTimeMillis() - set0);
        System.out.println(baseInterval);
    }

    private void updateMaxThreadCount() {
        boolean access = false;
        int count = 3;
        while (!access) {
            count++;
            List<Event> prepare = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                prepare.add(new AbstractEventBlur(baseImage, baseKernel, 1));
            }
            for (Event e : prepare) {
                e.start();
            }
            boolean alive;
            do {
                alive = false;
                for (Event e : prepare) {
                    alive = alive || e.isAlive();
                }
            } while (alive);
            int threadInterval = prepare.get(0).getInterval();
            System.out.println(count + " " + threadInterval);
            if (threadInterval > baseInterval * 8) {
                access = true;
                PipeLineParam.MAX_THREAD_SIZE = Math.min(count - 1, 4);
                ThreadPoolReflectCore.setThreadCountLimit(Math.min(count - 1, 4));
            }
        }
    }
}
