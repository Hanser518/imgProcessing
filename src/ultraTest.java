import Controller.ImgProcessingController;
import Entity.IMAGE;

import java.io.IOException;
import java.util.List;

public class ultraTest {
    static ImgProcessingController imgCtrl = new ImgProcessingController();
    public static void main(String[] args) throws IOException {
        IMAGE px = new IMAGE("rx78.jpg");
        imgCtrl.openMultiThreads();
        IMAGE gas = imgCtrl.getUltraGas(px, 40, 50);
        imgCtrl.saveByName(gas, "gas");
    }
}
