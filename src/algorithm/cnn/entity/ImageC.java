package algorithm.cnn.entity;

import algorithm.edgeTrace.main.EdgeTrace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 载入时就对数据进行复杂度分析并切分
 * 即将EventPool和IMAGE类相结合，执行多线程任务时不需要再去进行数据切分
 */
public class ImageC implements Runnable {

    /**
     * 图像源文件
     */
    private final BufferedImage bufferFile;

    /**
     * 像素存储
     */
    private int[][] argbMatrix;
    private int[][] hsvMatrix;
    private int[][] grayMatrix;

    /**
     * 图像宽高
     */
    private final int width;
    private final int height;

    /**
     * 图像块列表
     */
    private final List<ImageBlock> imageBlockList = new ArrayList<>();

    /**
     * 文件初始化情况
     */
    private volatile boolean hsvInitialization = false;
    private volatile boolean blockInitialization = false;

    /**
     * @param path
     * @throws IOException
     */
    public ImageC(String path) throws IOException {
        bufferFile = ImageIO.read(new File(path));
        width = bufferFile.getWidth();
        height = bufferFile.getHeight();
        argbMatrix = new int[width][height];
        int[] argbList = bufferFile.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < argbList.length; i++) {
            int x = i % width;
            int y = i / width;
            argbMatrix[x][y] = argbList[i];
        }
    }


    public ImageC(int[][] argbMatrix) {
        width = argbMatrix.length;
        height = argbMatrix[0].length;
        bufferFile = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferFile.setRGB(i, j, argbMatrix[i][j]);
            }
        }
        this.argbMatrix = argbMatrix;
        init();
    }


    /**
     * 文件图像载入后，会优先满足变量bufferFile和argbMatrix的生成
     * 其余的数据将会在额外线程中创建
     */
    @Override
    public void run() {
        this.hsvMatrix = transARGB2HSV(argbMatrix);
        hsvInitialization = true;
        // 初始化文件块
        int initBlockSize = 200;
        combineBlockBySize(buildBlockMatrix(initBlockSize), initBlockSize);
        blockInitialization = true;
    }

    private void init() {
        Thread thread = new Thread(this);
        thread.start();
        while (thread.isAlive()) ;
        System.out.println("ok");
    }

    public static int[][] transBlock2ArgbMatrix(List<ImageBlock> blockList) {
        int[][] argbMatrix = new int[blockList.get(0).getRawWidth()][blockList.get(0).getRawHeight()];
        for (ImageBlock block : blockList) {
            int x = block.getX();
            int y = block.getY();
            int width = block.getWidth();
            int height = block.getHeight();
            for (int i = x; i < x + width; i++) {
                for (int j = y; j < y + height; j++) {
                    argbMatrix[i][j] = block.getArgbMatrix()[i - x][j - y];
                    if (i - x == 0 || j - y == 0) {
                        argbMatrix[i][j] = (255 << 24) | (255 << 16) | (255 << 8) | 255;
                    }
                }
            }
        }
        return argbMatrix;
    }

    /**
     * 将输入的argb矩阵转换为hsv矩阵
     * <p> 兼容rgb矩阵
     *
     * @param argbMatrix argb/rgb矩阵
     * @return hsv矩阵
     */
    public static int[][] transARGB2HSV(int[][] argbMatrix) {
        // 0-H, 1-S, 2-V
        int width = argbMatrix.length;
        int height = argbMatrix[0].length;
        int[][] hsvMatrix = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double r = ((argbMatrix[i][j] >> 16) & 0xFF) / 255.0;
                double g = ((argbMatrix[i][j] >> 8) & 0xFF) / 255.0;
                double b = ((argbMatrix[i][j]) & 0xFF) / 255.0;
                double min = Math.min(r, Math.min(g, b));
                double v = Math.max(r, Math.max(g, b));

                double delta = v - min;
                double s = delta / (Math.abs(v) + 2.2204460492503131e-16);
                delta = 60.0 / (delta + 2.2204460492503131e-16);

                double h;
                if (v == r) {
                    h = (g - b) * delta;
                } else if (v == g) {
                    h = (b - r) * delta + 120;
                } else {
                    h = (r - g) * delta + 240;
                }
                h = h < 0 ? h + 360 : h;
                int ih = (int) (h / 2);
                int is = (int) (s * 255);
                int iv = (int) (v * 255);
                hsvMatrix[i][j] = (ih << 16) | (is << 8) | iv;
            }
        }
        return hsvMatrix;
    }

    /**
     * 将输入的hsv矩阵转换为argb矩阵
     *
     * @param hsvMatrix hsv矩阵
     * @return argb矩阵
     */
    public static int[][] transHSV2ARGB(int[][] hsvMatrix) {
        int width = hsvMatrix.length;
        int height = hsvMatrix[0].length;
        int[][] argbMatrix = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double h = (hsvMatrix[i][j] >> 16) & 0xFF * 2;
                double s = ((hsvMatrix[i][j] >> 8) & 0xFF) / 255.0;
                double v = (hsvMatrix[i][j] & 0xFF) / 255.0;
                int h1 = (int) Math.floor(h / 60);
                double f = h / 60 - h1;
                double p = v * (1 - s);
                double q = v * (1 - f * s);
                double t = v * (1 - (1 - f) * s);
                int r = 0, g = 0, b = 0;
                switch (h1) {
                    case 0 -> {
                        r = (int) (v * 255);
                        g = (int) (t * 255);
                        b = (int) (p * 255);
                    }

                    case 1 -> {
                        r = (int) (q * 255);
                        g = (int) (v * 255);
                        b = (int) (p * 255);
                    }

                    case 2 -> {
                        r = (int) (p * 255);
                        g = (int) (v * 255);
                        b = (int) (t * 255);
                    }

                    case 3 -> {
                        r = (int) (p * 255);
                        g = (int) (q * 255);
                        b = (int) (v * 255);
                    }

                    case 4 -> {
                        r = (int) (t * 255);
                        g = (int) (p * 255);
                        b = (int) (v * 255);
                    }

                    case 5 -> {
                        r = (int) (v * 255);
                        g = (int) (p * 255);
                        b = (int) (q * 255);
                    }

                }
                argbMatrix[i][j] = (255 << 24) | ((r) << 16) | ((g) << 8) | (b);
            }
        }
        return argbMatrix;
    }

    public static int[][] transARGB2Gray(int[][] argbMatrix) {
        int width = argbMatrix.length;
        int height = argbMatrix[0].length;
        int[][] gray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                gray[i][j] = (int) (((argbMatrix[i][j] >> 16) * 0xFF) * 0.29 + ((argbMatrix[i][j] >> 8) * 0xFF) * 0.59 + ((argbMatrix[i][j]) * 0xFF) * 0.12);
            }
        }
        return gray;

    }

    private ImageBlock[][] buildBlockMatrix(int initBlockSize) {
        ImageBlock[][] blockMatrix = new ImageBlock[width / initBlockSize + 1][height / initBlockSize + 1];
        for (int i = 0; i < blockMatrix.length; i++) {
            for (int j = 0; j < blockMatrix[0].length; j++) {
                int blockWidth = (i + 1) * initBlockSize > width ? width - (i) * initBlockSize : initBlockSize;
                int blockHeight = (j + 1) * initBlockSize > height ? height - (j) * initBlockSize : initBlockSize;
                if (blockHeight > 0 && blockWidth > 0) {
                    blockMatrix[i][j] = new ImageBlock(argbMatrix, i * initBlockSize, j * initBlockSize, blockWidth, blockHeight);
                }
            }
        }
        return blockMatrix;
    }

    public void buildBlockList(int blockSize) {
        ImageBlock[][] blockMatrix = new ImageBlock[width / blockSize + 1][height / blockSize + 1];
        for (int i = 0; i < blockMatrix.length; i++) {
            for (int j = 0; j < blockMatrix[0].length; j++) {
                int x = i * blockSize;
                int y = j * blockSize;
                if (x > 0 && y > 0) {
                    blockMatrix[i][j] = new ImageBlock(argbMatrix, x, y, blockSize, blockSize);
                }
            }
        }
    }

    private void combineBlockBySize(ImageBlock[][] blockMatrix, int initBlockSize) {
        boolean[][] isVisited = new boolean[width / initBlockSize + 1][height / initBlockSize + 1];
        int visitCount = 0;
        while (visitCount != isVisited.length * isVisited[0].length) {
            for (int i = 0; i < isVisited.length; i++) {
                for (int j = 0; j < isVisited[0].length; j++) {
                    if (blockMatrix[i][j].size() >= initBlockSize * initBlockSize) {
                        isVisited[i][j] = true;
                        imageBlockList.add(blockMatrix[i][j]);
                    } else {
                        if (!isVisited[i][j]) {
                            int[] dirAndSteps = getCombineDirAndSteps(blockMatrix, isVisited, i, j);
                            if (dirAndSteps[0] == -1) {
                                imageBlockList.add(blockMatrix[i][j]);
                            } else {
                                int[] param = getBlockParam(blockMatrix, i, j, dirAndSteps, isVisited);
                                imageBlockList.add(new ImageBlock(argbMatrix, blockMatrix[i][j].getX(), blockMatrix[i][j].getY(), param[0], param[1]));
                            }
                        }
                    }
                    visitCount += 1;
                }
            }
        }
    }

    private void combineBlockByComplexity(ImageBlock[][] blockMatrix, int initBlockSize) {
        boolean[][] isVisited = new boolean[width / initBlockSize + 1][height / initBlockSize + 1];
        for (int i = 0; i < isVisited.length; i++) {
            for (int j = 0; j < isVisited[0].length; j++) {
                if (blockMatrix[i][j].getComplexity() >= 7) {
                    isVisited[i][j] = true;
                    imageBlockList.add(blockMatrix[i][j]);
                } else {
                    if (!isVisited[i][j]) {
                        int[] dirAndSteps = getCombineDirAndSteps(blockMatrix, isVisited, i, j);
                        if (dirAndSteps[0] == -1) {
                            imageBlockList.add(blockMatrix[i][j]);
                        } else {
                            int[] param = getBlockParamByComplexity(blockMatrix, i, j, dirAndSteps, isVisited);
                            imageBlockList.add(new ImageBlock(argbMatrix, blockMatrix[i][j].getX(), blockMatrix[i][j].getY(), param[0], param[1]));
                        }
                    }
                }
            }
        }
    }

    private int[] getBlockParamByComplexity(ImageBlock[][] blockMatrix, int x, int y, int[] dirAndSteps, boolean[][] isVisited) {
        int cp = blockMatrix[x][y].getComplexity();
        int width = blockMatrix[x][y].getWidth();
        int height = blockMatrix[x][y].getHeight();
        int lx = x;
        int ly = y;
        isVisited[lx][ly] = true;
        for (int i = 0; i < dirAndSteps[1] && cp < 7; i++) {
            int[] nextCoordinate = EdgeTrace.getNextCoordinate(dirAndSteps[0], lx, ly);
            lx = nextCoordinate[0];
            ly = nextCoordinate[1];
            switch (dirAndSteps[0]) {
                case 2, 6 -> width += blockMatrix[lx][ly].getWidth();
                case 0, 4 -> height += blockMatrix[lx][ly].getHeight();
            }
            cp += blockMatrix[lx][ly].getComplexity();
            isVisited[lx][ly] = true;
        }
        return new int[]{width, height};
    }

    private int[] getBlockParam(ImageBlock[][] blockMatrix, int x, int y, int[] dirAndSteps, boolean[][] isVisited) {
        int width = blockMatrix[x][y].getWidth();
        int height = blockMatrix[x][y].getHeight();
        int lx = x;
        int ly = y;
        isVisited[lx][ly] = true;
        for (int i = 0; i < dirAndSteps[1] && width * height < 200 * 200; i++) {
            int[] nextCoordinate = EdgeTrace.getNextCoordinate(dirAndSteps[0], lx, ly);
            lx = nextCoordinate[0];
            ly = nextCoordinate[1];
            switch (dirAndSteps[0]) {
                case 2, 6 -> width += blockMatrix[lx][ly].getWidth();
                case 0, 4 -> height += blockMatrix[lx][ly].getHeight();
            }
            isVisited[lx][ly] = true;
        }
        return new int[]{width, height};
    }

    private int[] getCombineDirAndSteps(ImageBlock[][] blockMatrix, boolean[][] visited, int x, int y) {
        int dir = -1;
        int step = 0;
        for (int i = 0; i < 8; i += 2) {
            int len = getStepCount(blockMatrix, visited, i, x, y);
            if (len > step) {
                dir = i;
                step = len;
            }
        }
        return new int[]{dir, step};
    }

    private int getStepCount(ImageBlock[][] blockMatrix, boolean[][] isVisited, int dir, int lx, int ly) {
        int[] nextPoint = EdgeTrace.getNextCoordinate(dir, lx, ly);
        if (nextPoint[0] < 0 || nextPoint[1] < 0) {
            return 0;
        }
        if (nextPoint[0] < isVisited.length && nextPoint[1] < isVisited[0].length) {
            if (isVisited[nextPoint[0]][nextPoint[1]]) {
                return 0;
            }
            if (blockMatrix[lx][ly].getWidth() != blockMatrix[nextPoint[0]][nextPoint[1]].getWidth() && blockMatrix[lx][ly].getHeight() != blockMatrix[nextPoint[0]][nextPoint[1]].getHeight()) {
                return 0;
            }
            if (lx != nextPoint[0] || ly != nextPoint[1]) {
                return getStepCount(blockMatrix, isVisited, dir, nextPoint[0], nextPoint[1]) + 1;
            }
        }
        return 0;
    }

    public BufferedImage getImg() {
        return this.bufferFile;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int[][] getHSVMatrix() {
        if (hsvMatrix == null) {
            init();
        }
        return this.hsvMatrix;
    }

    public int[][] getArgbMatrix() {
        return this.argbMatrix;
    }

    public List<ImageBlock> getImageBlockList() {
        if (imageBlockList.isEmpty()) {
            init();
        }
        return imageBlockList;
    }
}
