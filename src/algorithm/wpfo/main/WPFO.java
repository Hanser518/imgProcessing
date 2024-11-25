package algorithm.wpfo.main;

import controller.EdgeController;
import entity.Image;

/**
 * wpfo : what are people focus on <p>
 * 该算法用于获取图像中的高频信息
 */
public class WPFO implements Runnable{
    private static EdgeController edgeCtrl = new EdgeController();
    private static int[][] data;
    private Image px = new Image();

    public WPFO(Image img) {
        Image edge = null;
        try{
            edge = edgeCtrl.getImgEdge(img, 0);
        } catch (Exception e) {
            edge = new Image(img.getWidth(), img.getHeight(), 0);
        }
        this.px = img;
        init(edge);
    }

    static void init(Image img){
        data = new int[img.getWidth()][img.getHeight()];
        int[][] matrix = img.getGrayMatrix();
        for(int i = 0;i < img.getWidth();i ++){
            for(int j = 0;j < img.getHeight();j ++){
                int level = (int) Math.log(matrix[i][j] & 0xFF + 1);
                switch (level){
                    case 1, 2 -> data[i][j] = 1;
                    case 3, 4 -> data[i][j] = 2;
                    case 5 -> data[i][j] = 3;
                    case 6 -> data[i][j] = 4;
                    case 7, 8 -> data[i][j] = 5;
                    default -> data[i][j] = 0;
                }
            }
        }
    }

    public static int[][] getData(Image img){
        Image edge = null;
        try{
            edge = edgeCtrl.getImgEdge(img, 1);
        } catch (Exception e) {
            edge = new Image(img.getWidth(), img.getHeight(), 0);
        }
        init(edge);
        return data;
    }

    @Override
    public void run() {
        init(px);
        System.out.println("okkkkkkkkkkkkkkkkkkkk");
    }
}
