package Entity;

public class EventPool {
    public int[][] result;
    public int index;  // 事务标识符
    public int sx, sy; // 线程识别点位
    public int width, height;  // 单个线程处理范围

    // 初始化
    public EventPool(int x, int y, int width, int height){
        this.sx = x;
        this.sy = y;
        this.width = width;
        this.height = height;
        result = new int[width][height];
    }

    // 设置标识符
    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return this.index;
    }
}
