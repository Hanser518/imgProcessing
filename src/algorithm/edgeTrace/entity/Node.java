package algorithm.edgeTrace.entity;

import java.util.List;


/**
 * @author Administrator
 */
public class Node {
    public static final int ROOT_NODE = 0;
    public static final int PATH_NODE = 1;
    public static final int LEAF_NODE = 2;
    public static final int ATOM_NODE = 3;

    /**
     * 节点类型，0根节点，1路径节点，2叶子节点
     */
    private int nodeType;

    /**
     * 节点大小
     */
    private int nodeSize;

    /**
     * 节点数据
     */
    private List<Point> pointList;

    /**
     * 节点数据大小，节点完成时刷新
     */
    private int pointSize;

    /**
     * 父节点
     */
    private Node prev;

    /**
     * 子节点
     */
    private Node next;

    /**
     * 设置节点属性
     * 根节点不允许含有父节点，叶子节点不允许包含子节点
     *
     * @param type 节点类型
     * @return 设置情况
     */
    public boolean setNodeType(int type) {
        if (type == ROOT_NODE && prev != null) {
            return false;
        } else if (type == LEAF_NODE && next != null) {
            return false;
        }
        this.nodeType = type;
        return true;
    }

    public int[] getIndexCoordinate(int index) {
        int lx = -1, ly = -1;
        if (pointList != null) {
            if(index < pointSize) {
                Point p = pointList.get(index);
                lx = p.getPx();
                ly = p.getPy();
            }
        }
        return new int[]{lx, ly};
    }

    public int[] getLastCoordinate() {
        return getIndexCoordinate(pointSize - 1);
    }

    public int getArgbValueBySize() {
        int value = Math.min(255, pointSize * nodeSize);
        return (255 << 24) | (value << 16) | (value << 8) | value;
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize() {
        this.pointSize = pointList.size();
    }

    public void setPointList(List<Point> list) {
        pointList = list;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public int getNodeType() {
        return nodeType;
    }
}
