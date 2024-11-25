package frame.entity;

import entity.Image;
import frame.entity.record.Local;

import java.util.ArrayList;
import java.util.List;

import static frame.constant.EventParam.*;

public abstract class Event extends Thread {
    /**
     * 原始数据
     */
    protected int[][] data;

    /**
     * 处理结果
     */
    protected int[][] result;

    /**
     * 原始宽高
     */
    protected int width, height;

    /**
     * 卷积核
     */
    protected double[][] kernel;

    /**
     * 处理标识点
     */
    protected List<Local> localList = new ArrayList<>();

    /**
     * 初始化
     */
    public Event(Image img, double[][] kernel) {
        data = Image.fillImageEdge(img, kernel.length, kernel[0].length).getArgbMatrix();
        result = new int[img.getWidth()][img.getHeight()];
        this.kernel = kernel;
        for (int i = 0; i < width / MAX_EVENT_SIZE + 1; i++) {
            for (int j = 0; j < height / MAX_EVENT_SIZE + 1; j++) {
                int wStep = i * MAX_EVENT_SIZE + MAX_EVENT_SIZE < width ? MAX_EVENT_SIZE : width - i * MAX_EVENT_SIZE;
                int hStep = j * MAX_EVENT_SIZE + MAX_EVENT_SIZE < height ? MAX_EVENT_SIZE : height - j * MAX_EVENT_SIZE;
                Local local = new Local(i * MAX_EVENT_SIZE, j * MAX_EVENT_SIZE, wStep, hStep);
                localList.add(local);
            }
        }
    }

    /**
     * 主方法
     */
    public abstract void function();

    @Override
    public void run() {
        function();
    }

    public Image getResult() {
        return new Image(result);
    }
}
