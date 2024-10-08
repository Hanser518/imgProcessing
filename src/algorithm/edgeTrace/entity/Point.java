package algorithm.edgeTrace.entity;

public class Point {

    /**
     * 点的横纵坐标
     */
    private final int px;
    private final int py;

    /**
     * 点的值
     */
    private final int value;

    public Point(int px, int py, int value) {
        this.px = px;
        this.py = py;
        this.value = value;
    }

    public int getArgbValue() {
        return (255 << 24) | (value << 16) | (value << 8) | value;
    }

    public int getPx() {
        return px;
    }

    public int getPy() {
        return py;
    }
}
