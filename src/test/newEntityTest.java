package test;

import controller.ImgController;
import entity.IMAGE;
import entity.test.TestEntity;

import java.io.IOException;

public class newEntityTest {
    static ImgController imgCtrl2 = new ImgController();
    public static void main(String[] args) throws IOException {
        String fileName = "bus";
        IMAGE px = new IMAGE(fileName + ".jpg");
        long set;

        set = System.currentTimeMillis();
        IMAGE raw = new IMAGE(px.getPixelMatrix());
        System.out.println("RAW: " + (System.currentTimeMillis() - set));

        set = System.currentTimeMillis();
        TestEntity te = new TestEntity(px.getPixelMatrix());
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
