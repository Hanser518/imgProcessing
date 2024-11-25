package frame.constant;

import entity.Image;
import frame.entity.ImageNode;
import frame.entity.Layer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static frame.FrameBase.fileChooser;
import static frame.FrameBase.updateFileList;

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

    public static int MAX_BLUR = 200;
    public static int blurRadio = 1;
    public static boolean BLUR_QUICK = true;
    public static int strangeBlurRadio = 1;
    public static int saturation = 0;
    public static int value = 0;

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
    public static Image image = new Image();
    public static ImageNode node = new ImageNode(image);
    public static Layer imageLayer = new Layer();
    public static ArrayList<JLabel> thumbList = new ArrayList<>();

    /**
     * 节点操作参数
     */
    public static final int CANCEL_NODE = 0;
    public static final int ADD_NODE = 1;
    public static final int RETRY_NODE = 2;
    public static final int CLEAR_NODE = 3;

    /**
     * 光栅操作参数
     */
    public static final int GRILLE_HORIZONTAL = 0;
    public static final int GRILLE_VERTICAL = 1;
    public static final int GRILLE_MULTIPLE = 2;
    public static int grilleParam = 1;
    public static int grilleType = 1;

    /**
     *
     */
    public static boolean window;

    /**
     * 封装后的字体
     */
    public static final Font countFont = new Font("Microsoft YaHei", Font.PLAIN, 16);
    public static final Font funcFont = new Font("Microsoft YaHei", Font.BOLD, 15);
    public static final Font applyBtnFont = new Font("MV Boli", Font.BOLD, 16);
    public static final Font fileBtnFont = new Font("Microsoft YaHei UI", Font.BOLD, 14);
    public static final Font titalFont = new Font("OPPO Sans Medium", Font.PLAIN, 15);

    public static final Color bkC1 = new Color(73, 82, 103);


    public static JButton buildApplyButton(){
        JButton apply = new JButton("Apply");
        apply.setFont(Param.applyBtnFont);
        apply.setContentAreaFilled(false);
        apply.setBorderPainted(false);
        return apply;
    }

    public static JButton functionButton(String name){
        JButton btn = new JButton(name);
        btn.setFont(Param.funcFont);
        btn.setContentAreaFilled(false);
        // btn.setBorderPainted(false);
        return btn;
    }

    public static JButton functionButton2(String name){
        JButton btn = functionButton(name);
        btn.setBorderPainted(false);
        return btn;
    }

    public static JButton buildBackButton(){
        JButton back = new JButton("<<<Back");
        back.setFont(Param.fileBtnFont);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        return back;
    }

    public static JButton fileButton(File file) {
        String fileName = (file.isDirectory() ? ">>>" : "-----") + file.getName();
        JButton fileBtn = new JButton(fileName);
        fileBtn.addActionListener(action -> {
            if (file.isDirectory()) {
                Param.path.add(file.getAbsolutePath());
                updateFileList();
            } else if (file.isFile()) {
                fileChooser(file);
            }
        });
        fileBtn.setBorderPainted(false);
        fileBtn.setContentAreaFilled(false);
        fileBtn.setFont(Param.fileBtnFont);
        return fileBtn;
    }

    public static JLabel processLabel = new JLabel("");



}
