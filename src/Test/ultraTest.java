package Test;

import Controller.ImgProcessingController;
import Entity.IMAGE;

import java.io.IOException;

public class ultraTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        IMAGE px = new IMAGE("rx78.jpg");
        imgCtrl.openMultiThreads();
        IMAGE gas = imgCtrl.getUltraGas(px, 30, 80);
        imgCtrl.save(gas, "gas");
    }
}
