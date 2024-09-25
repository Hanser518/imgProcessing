package Main;

import Controller.BlurController;
import Controller.ImgController;
import Discard.ImgProcessingController;
import Entity.IMAGE;

import java.io.IOException;

public class gasMap {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static BlurController blurCtrl = new BlurController();

    public static void main(String[] args) throws IOException {
        String fileName = "building";
        IMAGE px = new IMAGE(fileName + ".jpg");

        IMAGE gas = blurCtrl.getStrangeBlur(px, 67);
        imgCtrl2.showImg(gas, "gas");
        // imgCtrl.saveByName(gas, fileName, "strange");


    }
}
