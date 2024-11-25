package test;

import discard.ImgProcessingController;
import entity.Image;

import java.io.IOException;

public class ultraTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        Image px = new Image("rx78.jpg");
        imgCtrl.openMultiThreads();
        Image gas = imgCtrl.getUltraGas(px, 30, 80);
        imgCtrl.save(gas, "gas");
    }
}
