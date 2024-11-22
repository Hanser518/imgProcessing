package algorithm.edgeTrace.main;

import algorithm.edgeTrace.entity.Node;
import algorithm.edgeTrace.entity.Point;
import entity.IMAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * @author Sanfu
 */
public class EdgeTrace {
    public static int THRESHOLD = 16;
    public static final int PATTERN_ONE = 0;
    public static final int PATTERN_ALL = 1;

    public int buildPattern;

    /**
     * 路径列表
     */
    private final List<Node> pathList;

    /**
     * 图像数据，一般为argb模式，兼容rgb模式
     */
    private final int[][] imgData;

    /**
     * 图像宽高
     */
    private final int width;
    private final int height;

    /**
     * 图像访问记录
     */
    private final boolean[][] isVisited;

    private final int[][] result;

    int minX = 0, minY = 0;
    int maxX = 9999, maxY = 9999;

    public EdgeTrace(IMAGE img) {
        imgData = img.getArgbMatrix();
        width = img.getWidth();
        height = img.getHeight();
        pathList = new ArrayList<>();
        isVisited = new boolean[width][height];
        result = new int[width][height];
        for (int i = 0; i < width; i++) {
            Arrays.fill(result[i], ((255 << 24)));
        }
    }

    public void start(int pattern) {
        buildPattern = pattern;
        if (pattern == PATTERN_ONE) {
            THRESHOLD = 16;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = (imgData[i][j] >> 16) & 0xFF;
                if (value > THRESHOLD && !isVisited[i][j]) {
                    buildRootNode(i, j, pattern);
                }
            }
        }
        System.out.println(pathList.size());
    }

    public int[][] getData() {
        for (Node node : pathList) {
            if (buildPattern == PATTERN_ONE) {
                if (node.getNodeSize() > 4) {
                    System.out.print(node.getNodeSize() + "\t");
                    transNodeToData(node);
                }
            } else if (buildPattern == PATTERN_ALL) {
                if (node.getPointSize() > THRESHOLD) {
                    System.out.print(node.getPointSize() + "\t");
                    transNodeToData(node);
                }
            }
        }
        System.out.println("pathList.size = " + pathList.size());
        if (buildPattern == PATTERN_ONE) {
            optimizeResultInPatternOne();
        }
        return result;
    }

    /**
     * 将路径转换为矩阵
     *
     * @param node 路径
     */
    private void transNodeToData(Node node) {
        if (node.getNext() != null) {
            transNodeToData(node.getNext());
        }
        if (buildPattern == PATTERN_ONE) {
            if (node.getNodeType() == Node.LEAF_NODE && node.getPointSize() < THRESHOLD) {
                return;
            }
            if (node.getNodeType() == Node.PATH_NODE && node.getNodeSize() < THRESHOLD / 2) {
                return;
            }
            for (Point p : node.getPointList()) {
                result[p.getPx()][p.getPy()] = node.getArgbValueBySize();
            }
        } else if (buildPattern == PATTERN_ALL) {
            for (Point p : node.getPointList()) {
                int index = node.getPointSize();
                int r = Math.min(255, index);
                int g = Math.min(255, Math.max(1, index - 255));
                int b = Math.min(255, Math.max(1, index - 510));
                result[p.getPx()][p.getPy()] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    /**
     * 创建根节点，当前节点无子节点时，自动转换为原子节点
     *
     * @param px 起点坐标
     * @param py 起点坐标
     */
    private void buildRootNode(int px, int py, int pattern) {
        // 标记该点已访问
        isVisited[px][py] = true;
        // 创建根节点
        Node rootNode = new Node();
        // 添加节点数据
        if (pattern == PATTERN_ONE) {
            rootNode.setPointList(buildPointPathInOneDirection(px, py));
        } else if (pattern == PATTERN_ALL) {
            rootNode.setPointList(buildPointPathInAllDirection(px, py));
        }
        rootNode.setPointSize();
        rootNode.setNext(buildChildNode(rootNode));
        if (rootNode.getNext() == null) {
            rootNode.setNodeType(Node.ATOM_NODE);
            rootNode.setNodeSize(1);
        } else {
            rootNode.setNodeType(Node.ROOT_NODE);
            rootNode.setNodeSize(rootNode.getNext().getNodeSize() + 1);
        }
        pathList.add(rootNode);
    }

    /**
     * 创建子节点
     *
     * @param parent 父节点
     * @return 子节点
     */
    private Node buildChildNode(Node parent) {
        int[] local = parent.getLastCoordinate();
        int lx = local[0];
        int ly = local[1];
        Node childNode = new Node();
        if (buildPattern == PATTERN_ALL) {
            childNode.setPointList(buildPointPathInAllDirection(lx, ly));
        } else if (buildPattern == PATTERN_ONE) {
            childNode.setPointList(buildPointPathInOneDirection(lx, ly));
            for (int index = parent.getPointSize() / 2; index < parent.getPointSize() - 1; index++) {
                if (childNode.getPointList() != null && childNode.getPointList().size() > 4) {
                    break;
                } else {
                    local = parent.getIndexCoordinate(index);
                    lx = local[0];
                    ly = local[1];
                    childNode.setPointList(buildPointPathInOneDirection(lx, ly));
                }
            }
        }
        if (!childNode.getPointList().isEmpty()) {
            childNode.setPrev(parent);
            childNode.setPointSize();
            if (childNode.getPointSize() > 1) {
                childNode.setNext(buildChildNode(childNode));
                if (childNode.getNext() != null) {
                    childNode.setNodeType(Node.PATH_NODE);
                    childNode.setNodeSize(childNode.getNext().getNodeSize() + 1);
                } else {
                    childNode.setNodeType(Node.LEAF_NODE);
                    childNode.setNodeSize(1);
                }
                return childNode;
            }
        }
        return null;
    }

    /**
     * 创建节点数据，包含访问标记
     *
     * @param px 起点坐标
     * @param py 起点坐标
     * @return 节点数据
     */
    private List<Point> buildPointPathInOneDirection(int px, int py) {
        List<Point> result = new ArrayList<>();
        result.add(new Point(px, py, imgData[px][py]));
        int[] dirInfo = getDirectionAndLength(px, py);
        if (dirInfo[0] != -1) {
            int nx = px, ny = py;
            for (int i = 0; i < dirInfo[1]; i++) {
                int[] nextPoint = getNextCoordinate(dirInfo[0], nx, ny);
                nx = nextPoint[0];
                ny = nextPoint[1];
                result.add(new Point(nx, ny, imgData[nx][ny]));
                isVisited[nx][ny] = true;
            }
        }
        return result;
    }

    /**
     * 获取最长轨迹方向，共9个方向，返回值包含方向和长度
     * -1表示无符合方向，对应长度为0
     *
     * @param px 起点坐标
     * @param py 起点坐标
     * @return 长度为2的int数组，0-方向，1-长度
     */
    private int[] getDirectionAndLength(int px, int py) {
        int dir = -1;
        int len = 0;
        for (int i = 0; i < 16; i++) {
            int length = getStepCount(i, px, py);
            if (length > len) {
                dir = i;
                len = length;
            }
        }
        return new int[]{dir, len};
    }

    /**
     * 递归函数
     * 通过递归获取对应方向的最大步长，包含访问检测
     *
     * @param dir 方向
     * @param lx  起始坐标
     * @param ly  起始坐标
     * @return 步长
     */
    private int getStepCount(int dir, int lx, int ly) {
        int[] nextPoint = getNextCoordinate(dir, lx, ly);
        if (nextPoint[0] < 0 || nextPoint[1] < 0) {
            return 0;
        }
        if (nextPoint[0] < width - 1 && nextPoint[1] < height - 1) {
            int value = (imgData[nextPoint[0]][nextPoint[1]] >> 16) & 0xFF;
            if (isVisited[nextPoint[0]][nextPoint[1]] || value <= THRESHOLD) {
                return 0;
            }
            if (lx != nextPoint[0] || ly != nextPoint[1]) {
                return getStepCount(dir, nextPoint[0], nextPoint[1]) + 1;
            }
        }
        return 0;
    }

    public static int[] getNextCoordinate(int dir, int lx, int ly) {
        int nx = lx;
        int ny = ly;
        switch (dir) {
            case 0 -> ny -= 1;
            case 1 -> nx += 1;
            case 2 -> ny += 1;
            case 3 -> nx -= 1;
            default -> {
                return getNextCoordinate8(dir, lx, ly);
            }
        }
        return new int[]{nx, ny};
    }


    /**
     * 获取对应方向下一步坐标，共8个方向
     *
     * @param dir 方向
     * @param lx  起始坐标
     * @param ly  起始坐标
     * @return 坐标
     */
    private static int[] getNextCoordinate8(int dir, int lx, int ly) {
        int nx = lx;
        int ny = ly;
        switch (dir) {
            case 4 -> {
                nx += 1;
                ny -= 1;
            }
            case 5 -> {
                nx += 1;
                ny += 1;
            }
            case 6 -> {
                nx -= 1;
                ny += 1;
            }
            case 7 -> {
                nx -= 1;
                ny -= 1;
            }
            default -> {
                return getNextCoordinate16(dir, lx, ly);
            }
        }
        return new int[]{nx, ny};
    }

    /**
     * 获取对应方向下一步坐标，共16个方向
     *
     * @param dir 方向
     * @param lx  起始坐标
     * @param ly  起始坐标
     * @return 坐标
     */
    private static int[] getNextCoordinate16(int dir, int lx, int ly) {
        int nx = lx;
        int ny = ly;
        switch (dir) {
            case 8 -> {
                nx += 1;
                ny -= 2;
            }
            case 9 -> {
                nx += 2;
                ny -= 1;
            }
            case 10 -> {
                nx += 2;
                ny += 1;
            }
            case 11 -> {
                nx += 1;
                ny += 2;
            }
            case 12 -> {
                nx -= 1;
                ny += 2;
            }
            case 13 -> {
                nx -= 2;
                ny += 1;
            }
            case 14 -> {
                nx -= 2;
                ny -= 1;
            }
            case 15 -> {
                nx -= 1;
                ny -= 2;
            }
        }
        return new int[]{nx, ny};
    }

    /**
     * 创建节点数据，包含访问标记
     *
     * @param px 起点坐标
     * @param py 起点坐标
     * @return 节点数据
     */
    private List<Point> buildPointPathInAllDirection(int px, int py) {
        List<Point> result = new ArrayList<>();
        Stack<Point> stack = new Stack<>();
        result.add(new Point(px, py, imgData[px][py]));
        stack.push(new Point(px, py, imgData[px][py]));
        int prev = -1;
        while (!stack.isEmpty()) {
            Point p = stack.peek();
            int dir = getNextDir(p.getPx(), p.getPy(), prev);
            prev = dir;
            if (dir == -1) {
                stack.pop();
            } else {
                int[] nextCoordinate = getNextCoordinate(dir, p.getPx(), p.getPy());
                stack.push(new Point(nextCoordinate[0], nextCoordinate[1], imgData[nextCoordinate[0]][nextCoordinate[1]]));
                result.add(new Point(nextCoordinate[0], nextCoordinate[1], imgData[nextCoordinate[0]][nextCoordinate[1]]));
                isVisited[nextCoordinate[0]][nextCoordinate[1]] = true;
            }
        }
        // System.out.print(result.size() + " ");
        return result;
    }

    /**
     * 获取下一步方向
     *
     * @param lx 当前坐标
     * @param ly 当前坐标
     * @return 返回值（-1~7，共9位）
     */
    private int getNextDir(int lx, int ly, int prev) {
        List<Integer> dirBackup = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            int[] nc = getNextCoordinate(i, lx, ly);
            if (nc[0] < width && nc[1] < height && nc[0] > -1 && nc[1] > -1) {
                if (!isVisited[nc[0]][nc[1]]) {
                    int value = (imgData[nc[0]][nc[1]] >> 16) & 0xFF;
                    if (value > THRESHOLD) {
                        dirBackup.add(i);
                    }
                }
            }
        }
        if (dirBackup.isEmpty()) {
            return -1;
        } else {
            if (dirBackup.contains(prev)) {
                return prev;
            }
            return dirBackup.get((int) (Math.random() * dirBackup.size()));
        }
    }

    /**
     * 优化路径
     */
    private void optimizeResultInPatternOne() {
        for (Node node : pathList) {
            if (node.getNodeSize() > 4) {
                trackAndDilate(node);
            }
        }
    }

    /**
     * 跟随填充
     *
     * @param node 节点
     */
    private void trackAndDilate(Node node) {
        if (node.getNext() != null) {
            trackAndDilate(node.getNext());
        }
        if (node.getNodeType() == Node.LEAF_NODE && node.getPointSize() < THRESHOLD) {
            return;
        }
        if (node.getNodeType() == Node.PATH_NODE && node.getNodeSize() < THRESHOLD / 2) {
            return;
        }
        for (Point p : node.getPointList()) {
            for (int i = 0; i < 16; i++) {
                int[] nextDir = getNextCoordinate(i, p.getPx(), p.getPy());
                if (nextDir[0] > -1 && nextDir[0] < width && nextDir[1] > -1 && nextDir[1] < height) {
                    result[nextDir[0]][nextDir[1]] = result[p.getPx()][p.getPy()];
                }
            }
        }
    }

    /**
     * 获取节点列表
     *
     * @return 节点列表
     */
    public List<Node> getPathList() {
        return pathList;
    }
}
