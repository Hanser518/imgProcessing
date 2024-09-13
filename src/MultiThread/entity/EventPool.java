package MultiThread.entity;

public class EventPool {
    public int[][] result;
    public int index;  // 事务标识符
    public int sx, sy; // 线程识别点位
    public int width, height;  // 单个线程处理范围
    public boolean locked = false; // 事务锁🔒

    // 初始化
    public EventPool(int x, int y, int width, int height){
        this.sx = x;
        this.sy = y;
        this.width = width;
        this.height = height;
        result = new int[width][height];
    }

    public void setIndex(int index){
        this.index = index;
    }

    // 对事务进行上锁
    public boolean isLocked() {
        if(!locked) {
            locked = true;
            return false;
        }else{
            return true;
        }
    }

    // 获取事务数据
    public int[][] getResult(){
        if(!locked){
            locked = true;
            return result;
        }else{
            return null;
        }
    }
}
