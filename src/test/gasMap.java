package test;

import controller.BlurController;
import controller.ImgController;
import discard.ImgProcessingController;
import entity.Image;
import algorithm.cnn.entity.Image2;

import java.io.IOException;

public class gasMap {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    static ImgController imgCtrl2 = new ImgController();
    static BlurController blurCtrl = new BlurController();

    public static void main(String[] args) throws IOException {
        String fileName = "building";
        Image px = new Image(fileName + ".jpg");

        Image gas = blurCtrl.getStrangeBlur(px, 67);
        imgCtrl2.showImg(gas, "gas");
        // imgCtrl.saveByName(gas, fileName, "strange");

        Image2 ic = new Image2(gas.getRawFile());

    }
}
