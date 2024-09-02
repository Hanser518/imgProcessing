import Controller.ImgProcessingController;
import Entity.IMAGE;

import java.io.IOException;
import java.util.List;

public class ultraTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        IMAGE px = new IMAGE("index1.png");
        imgCtrl.openMultiThreads();
        IMAGE gas = imgCtrl.getUltraGas(px, 40, 75);
        imgCtrl.saveByName(gas, "gas");
    }
}
