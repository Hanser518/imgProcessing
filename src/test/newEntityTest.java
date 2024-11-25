package test;

import controller.ImgController;
import entity.Image;
import algorithm.cnnDiscard.entity.ImageC;

import java.io.IOException;

public class newEntityTest {
    static ImgController imgCtrl2 = new ImgController();
    public static void main(String[] args) throws IOException {
        String fileName = "bus";
        Image px = new Image(fileName + ".jpg");
        long set;

        set = System.currentTimeMillis();
        Image raw = new Image(px.getArgbMatrix());
        System.out.println("RAW: " + (System.currentTimeMillis() - set));

        set = System.currentTimeMillis();
        ImageC te = new ImageC(px.getArgbMatrix());
        System.out.println("NEW: " + (System.currentTimeMillis() - set));

        set = System.currentTimeMillis();
        Thread t = new Thread(te);
        t.start();
        te.getImageBlockList();
        System.out.println("NEW: " + (System.currentTimeMillis() - set));
//        imgCtrl2.showImg(px, "raw");
//        imgCtrl2.showImg(trans, "111");
    }
}
