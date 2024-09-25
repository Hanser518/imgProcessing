package Main;

import Discard.ImgProcessingController;
import Entity.IMAGE;

import java.io.IOException;

public class enhanceTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        imgCtrl.openMultiThreads();
        imgCtrl.closeAccCalc();
        String fileName = "sun";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE enhance = imgCtrl.getEnhanceImage(px, 3.14);
        imgCtrl.save(enhance, "enhance_" + fileName);
    }
}
