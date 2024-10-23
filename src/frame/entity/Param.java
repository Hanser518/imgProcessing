package frame.entity;

import entity.IMAGE;

import java.io.File;
import java.util.ArrayList;

public class Param {

    /**
     * 图像缩放比例与窗口缩放比例
     */
    public static double zoom = 1.0;
    public static double rate = 0.6;

    /**
     * 窗口尺寸
     */
    public static int screenWidth;
    public static int screenHeight;
    public static int frameWidth;
    public static int frameHeight;

    public static int blurSize = 10;

    /**
     * 文件指定、目录参数
     */
    public static ArrayList<File> fileList = new ArrayList<>();
    public static ArrayList<String> path = new ArrayList<>();
    public static String path_head = "./";
    public static String pathNow = null;

    /**
     * 当前操作图像参数
     */
    public static IMAGE image = new IMAGE();
}
