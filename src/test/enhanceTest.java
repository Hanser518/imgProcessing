package test;

import discard.ImgProcessingController;
import entity.Image;

import java.io.IOException;

public class enhanceTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        imgCtrl.openMultiThreads();
        imgCtrl.closeAccCalc();
        String fileName = "sun";
        Image px = new Image(fileName + ".jpg");

        Image enhance = imgCtrl.getEnhanceImage(px, 3.14);
        imgCtrl.save(enhance, "enhance_" + fileName);
    }
}
